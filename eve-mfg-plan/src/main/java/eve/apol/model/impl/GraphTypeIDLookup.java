package eve.apol.model.impl;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eve.apol.entity.Item;
import eve.apol.model.TypeIDLookup;

public class GraphTypeIDLookup implements TypeIDLookup {
    
    private static Logger log = LoggerFactory.getLogger(TypeIDLookup.class);
    
    private GraphTraversalSource g;

    public GraphTypeIDLookup(Graph graph) {
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
        log.trace("{} ms to lookup name for typeid", System.currentTimeMillis() - ms);
        return name;
    }
    
    @Override
    public Item getItem(Long typeID) {
        long ms = System.currentTimeMillis();
        Vertex v = g.V(typeID).next();
        log.trace("{} ms to lookup Vertex for typeid", System.currentTimeMillis() - ms);
        Item i = new Item();
        i.setName((String) v.property("name").value());
        i.setTypeID((Long) v.id());
        return i;
    }
}
