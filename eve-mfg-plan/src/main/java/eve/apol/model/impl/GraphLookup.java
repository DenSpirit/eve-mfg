package eve.apol.model.impl;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eve.apol.model.TypeIDLookup;

public class GraphLookup implements TypeIDLookup {
    
    private static Logger log = LoggerFactory.getLogger(GraphLookup.class);
    
    private GraphTraversalSource g;

    public GraphLookup(Graph graph) {
        this.g = graph.traversal();
    }

    @Override
    public Long getTypeID(String name) {
        long ms = System.currentTimeMillis();
        Long typeId = (Long) g.V().has("name", name).id().next();
        log.trace("{} ms to lookup for name", System.currentTimeMillis() - ms);
        return typeId;
    }

    @Override
    public String getName(Long typeID) {
        long ms = System.currentTimeMillis();
        String name = (String) g.V(typeID).values("name").next();
        log.trace("{} ms to lookup for typeid", System.currentTimeMillis() - ms);
        return name;
    }

}
