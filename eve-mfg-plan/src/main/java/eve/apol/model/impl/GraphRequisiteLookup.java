package eve.apol.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eve.apol.entity.Requisite;
import eve.apol.model.RequisiteLookup;
import eve.apol.model.TypeIDLookup;

public class GraphRequisiteLookup implements RequisiteLookup {
    
    private static Logger log = LoggerFactory.getLogger(RequisiteLookup.class);

    private Graph graph;
    private TypeIDLookup typeIDLookup;

    public GraphRequisiteLookup(Graph graph, TypeIDLookup lookup) {
        this.graph = graph;
        this.typeIDLookup = lookup;
    }

    @Override
    public Stream<Requisite> getRequisites(Requisite order) {
        long ms = System.currentTimeMillis();
        GraphTraversalSource g = graph.traversal();
        GraphTraversal<Vertex, Map<String, Object>> requirementsTraversal = g
                .V(order.getItem().getTypeID())
                .repeat(__
                    .in("manufacturing")
                    .inE("material").as("matQuantity")
                    .outV().as("matItem"))
                .until(__.inE("manufacturing").count().is(0))
                .project("quantity", "material")
                .by(__.select("matQuantity").unfold().tail().properties("quantity").value())
                .by(__.select("matItem").unfold().tail().id());
        //List<Map<String, Object>> stuff = new ArrayList<>();
        Stream<Requisite> requisites = stream(requirementsTraversal).map(map -> {
            Requisite req = new Requisite();
            Long typeID = (Long) map.get("material");
            req.setItem(typeIDLookup.getItem(typeID));
            req.setQuantity((Integer) map.get("quantity"));
            return req;
        });
        log.trace("{} ms to lookup requisites for {}", System.currentTimeMillis() - ms, order.getItem().getName());
        return requisites;
    }

    private <T> Stream<T> stream(final Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.IMMUTABLE | Spliterator.SIZED), false);
    }

}
