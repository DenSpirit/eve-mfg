package eve.apol.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoIo;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Importer {
    
    private static final String QUANTITY_PROP = "quantity";
    private static final String NAME_PROP = "name";
    private static final String TYPE_ID = "typeID";
    private static final Logger log = LoggerFactory.getLogger(Importer.class);
    
    public static void main(String[] args) {
        try (TinkerGraph graph = TinkerGraph.open()) {
            try (BufferedReader typeIDs = new BufferedReader(new InputStreamReader(Importer.class.getResourceAsStream("/typeids.csv")))) {
                String line = typeIDs.readLine();
                while (line != null) {
                    int sep = line.indexOf(';');
                    long typeID = Long.parseLong(line.substring(0, sep));
                    String name = line.substring(sep + 1);
                    Vertex v = graph.addVertex(T.id, typeID, T.label, "item", 
                    		TYPE_ID, typeID, NAME_PROP, name.trim());
                    v.property(TYPE_ID, typeID, NAME_PROP, name.trim());
                    log.info("added {}, {}", typeID, name);
                    line = typeIDs.readLine();
                }
            }
            graph.createIndex(TYPE_ID, Vertex.class);
            log.info("connecting blueprints");
            try (BufferedReader blueprints = new BufferedReader(new InputStreamReader(Importer.class.getResourceAsStream("/blueprint_graph.csv")))) {
                String line = blueprints.readLine(); // headers
                line = blueprints.readLine();
                while (line != null) {
                    String[] edgeInformation = line.split(" ");
                    long blueprintId = Long.parseLong(edgeInformation[0]);
                    String activityLabel = edgeInformation[1];
                    long materialId = Long.parseLong(edgeInformation[2]);
                    int materialCount = Integer.parseInt(edgeInformation[3]);
                    long productId = Long.parseLong(edgeInformation[4]);
                    int productQuantity = Integer.parseInt(edgeInformation[5]);
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
            }
            log.info("serializing graph");
            graph.io(GryoIo.build()).writeGraph("/tmp/graph.kryo");
            log.info("finished");
        } catch (Exception e) {
            log.error("Failed to close graph", e);
        }
    }

    private static <THING> boolean iteratorContains(Iterator<THING> iterator, THING thing) {
        while(iterator.hasNext()) {
            if (iterator.next().equals(thing)) {
                return true;
            }
        }
        return false;
    }

    private static Vertex getActivity(TinkerGraph graph, String activityLabel, Vertex blueprint, long productId) {
        Vertex activity;
        Iterator<Edge> toActivity = blueprint.edges(Direction.OUT, "blueprint");
        if(toActivity.hasNext()) {
        	log.info("found activity for {}", blueprint.property(NAME_PROP).value());
            activity = toActivity.next().inVertex();
        } else {
            activity = graph.addVertex(T.id, blueprint.id() + "_" + activityLabel + "_" + productId, T.label, "activity", "type", activityLabel);
            blueprint.addEdge("blueprint", activity);
            log.info("created activity for {}", blueprint.property(NAME_PROP).value());
        }
        return activity;
    }
}
