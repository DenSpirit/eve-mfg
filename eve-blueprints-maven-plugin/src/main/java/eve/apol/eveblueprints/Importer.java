package eve.apol.eveblueprints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Importer {
    private static final String QUANTITY_PROP = "quantity";
    private static final String NAME_PROP = "name";
    private static final String TYPE_ID = "typeID";
    private static final Logger log = LoggerFactory.getLogger(Importer.class);

    public Importer(InputStream typeids, InputStream blueprints, File outputFile) {
        super();
        this.typeidsStream = typeids;
        this.blueprintsStream = blueprints;
        this.outputFile = outputFile;
    }

    private InputStream typeidsStream;
    private InputStream blueprintsStream;
    private File outputFile;
    
    public void importBlueprints() {
    	log.info("Opening graph");
        try (TinkerGraph graph = TinkerGraph.open()) {
            try (BufferedReader typeIDs = new BufferedReader(new InputStreamReader(typeidsStream))) {
                String line = typeIDs.readLine();
                while (line != null) {
                    int sep = line.indexOf(';');
                    long typeID = Long.parseLong(line.substring(0, sep));
                    String name = line.substring(sep + 1);
                    Vertex v = graph.addVertex(T.id, typeID, T.label, "item", 
                    		TYPE_ID, typeID, NAME_PROP, name.trim());
                    v.property(TYPE_ID, typeID, NAME_PROP, name.trim());
                    log.debug("added {}, {}", typeID, name);
                    line = typeIDs.readLine();
                }
            }
            graph.createIndex(TYPE_ID, Vertex.class);
            log.debug("connecting blueprints");
            try (BufferedReader blueprints = new BufferedReader(new InputStreamReader(blueprintsStream))) {
                String line = blueprints.readLine(); // headers
                line = blueprints.readLine();
                Vertex blueprint = graph.vertices(blueprintId).next();
                Vertex product = graph.vertices(productId).next();
                Vertex activity = getActivity(graph, activityLabel, blueprint, productId);
                Vertex material = graph.vertices(materialId).next();

                if (!iteratorContains(product.vertices(Direction.IN, activityLabel), activity)) {
                    activity.addEdge(activityLabel, product, QUANTITY_PROP, productQuantity);
                }

                material.addEdge("material", activity, QUANTITY_PROP, materialCount);

            }
        } catch (IOException e) {
            log.error("Manufacturing info not loaded", e);
            return null;
        }
        return graph;
    }

    private static <THING> boolean iteratorContains(Iterator<THING> iterator, THING thing) {
        while (iterator.hasNext()) {
            if (iterator.next().equals(thing)) {
                return true;
            }
        }
        return false;
    }

    private static Vertex getActivity(TinkerGraph graph, String activityLabel, Vertex blueprint, long productId) {
    	String activityID = blueprint.id() + "_" + activityLabel + "_" + productId;
		Iterator<Vertex> activities = graph.vertices(activityID);
        Vertex activity;
        if(activities.hasNext()) {
        	log.debug("found activity for {}", blueprint.property(NAME_PROP).value());
            activity = activities.next();
        } else {
            activity = graph.addVertex(T.id, activityID, T.label, "activity", "type", activityLabel);
            blueprint.addEdge("blueprint", activity);
            log.debug("created activity for {}", blueprint.property(NAME_PROP).value());
        }
        return activity;
    }
}
