package eve.apol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create TinkerPop Graph instance holding blueprint information
 * @author dborisevich
 *
 */
public class Importer {
    private static final String DEFAULT_BLUEPRINTS_PATH = "/blueprint_graph.csv";
    private static final String DEFAULT_TYPEIDS_PATH = "/typeids.csv";
    private static final String QUANTITY_PROP = "quantity";
    private static final String NAME_PROP = "name";
    private static final Logger log = LoggerFactory.getLogger(Importer.class);

    public Importer(InputStream typeidsFile, InputStream blueprintsFile) {
        super();
        this.typeidsStream = typeidsFile;
        this.blueprintsStream = blueprintsFile;
    }

    private InputStream typeidsStream;
    private InputStream blueprintsStream;

    public Graph importBlueprints() throws GraphReadException {
        Graph graph = TinkerGraph.open();
        try {
            readItems(graph);
            readBlueprintConnections(graph);
        } catch (IOException e) {
            throw new GraphReadException("Could not load blueprint graph", e);
        }
        return graph;
    }

    private void readBlueprintConnections(Graph graph) throws IOException {
        log.debug("connecting blueprints");
        try (BufferedReader blueprints = new BufferedReader(new InputStreamReader(blueprintsStream))) {
            blueprints.lines().filter(line -> !line.startsWith("#")).forEach(line -> readConnection(graph, line));
        }
    }

    private void readItems(Graph graph) throws IOException {
        log.debug("reading types");
        try (BufferedReader typeIDs = new BufferedReader(new InputStreamReader(typeidsStream))) {
            typeIDs.lines().filter(line -> !line.startsWith("#")).forEach(line -> readItem(graph, line));
        }
    }

    private void readConnection(Graph graph, String line) {
        String[] edgeInformation = line.split(";");

        long blueprintId = Long.parseLong(edgeInformation[0]);
        String activityLabel = edgeInformation[1];
        long materialId = Long.parseLong(edgeInformation[2]);
        int materialCount = Integer.parseInt(edgeInformation[3]);
        long productId = Long.parseLong(edgeInformation[4]);
        int productQuantity = Integer.parseInt(edgeInformation[5]);

        Vertex blueprint = graph.vertices(blueprintId).next();
        Vertex product = graph.vertices(productId).next();
        Vertex activity = getActivity(graph, activityLabel, blueprint, productId);
        Vertex material = graph.vertices(materialId).next();

        if (!iteratorContains(product.vertices(Direction.IN, activityLabel), activity)) {
            activity.addEdge(activityLabel, product, QUANTITY_PROP, productQuantity);
        }

        material.addEdge("material", activity, QUANTITY_PROP, materialCount);
    }


    private void readItem(Graph graph, String line) {
        String[] items = line.split(";");
        long typeID = Long.parseLong(items[0]);
        String name = items[1];
        long groupID = Long.parseLong(items[2]);
        BigDecimal volume = new BigDecimal(items[3]);
        BigDecimal basePrice = new BigDecimal(items[4]);
        graph.addVertex(T.id, typeID, T.label, "item", 
                NAME_PROP, name.trim(), 
                "groupID", groupID, 
                "volume", volume,
                "basePrice", basePrice);
        log.debug("added {}, {}", typeID, name);
    }

    private static <THING> boolean iteratorContains(Iterator<THING> iterator, THING thing) {
        while (iterator.hasNext()) {
            if (iterator.next().equals(thing)) {
                return true;
            }
        }
        return false;
    }

    private static Vertex getActivity(Graph graph, String activityLabel, Vertex blueprint, long productId) {
        String activityId = String.format("%s_%s_%s", blueprint.id(), activityLabel, productId);
        Vertex activity;
        Iterator<Vertex> toActivity = graph.vertices(activityId);
        if (toActivity.hasNext()) {
            log.debug("found activity for {}", blueprint.value(NAME_PROP).toString());
            activity = toActivity.next();
        } else {
            activity = graph.addVertex(T.id, activityId, T.label, "activity", "type", activityLabel);
            blueprint.addEdge("blueprint", activity);
            log.debug("created activity for {}", blueprint.property(NAME_PROP).value());
        }
        return activity;
    }

    public static Graph fromClassPath() {
        return fromClassPath(DEFAULT_TYPEIDS_PATH, DEFAULT_BLUEPRINTS_PATH);
    }
    
    public static Graph fromClassPath(String typeidPath, String blueprintsPath) {
        InputStream typeids = Importer.class.getResourceAsStream(typeidPath);
        InputStream blueprints = Importer.class.getResourceAsStream(blueprintsPath);
        return new Importer(typeids, blueprints).importBlueprints();
    }
}

