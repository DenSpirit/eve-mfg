package eve.apol.main;

import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shell {
    
    private static final Logger log = LoggerFactory.getLogger(Shell.class);
    
    public static void main(String[] args) {
        try(Neo4jGraph graph = Neo4jGraph.open("/tmp/neo4j")) {
            graph.addVertex(T.label, "item", "typeID", 34, "name", "Tritanium");
        } catch (Exception e) {
            log.error("Failed to close graph", e);
        } 
    }

}
