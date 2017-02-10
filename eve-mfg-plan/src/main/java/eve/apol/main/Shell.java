package eve.apol.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eve.apol.Importer;
import eve.apol.entity.Item;
import eve.apol.entity.Order;
import eve.apol.model.FactoryOfEverything;
import eve.apol.model.TypeIDLookup;

public class Shell {

    private static final Logger log = LoggerFactory.getLogger(Shell.class);
    
    private TypeIDLookup typeIDLookup = FactoryOfEverything.getLookup();

    public static void main(String[] args) {
        Shell shell = new Shell();
        shell.run();
    }

    private void run() {
        Map<String, Order> items = readItems();
        for(Order order : items.values()) {
            order.getItem().setTypeID(typeIDLookup.getTypeID(order.getItem().getName()));
            log.info("{} of {} ({})", order.getQuantity(), order.getItem().getName(), order.getItem().getTypeID());
        }
    }

    private static Map<String, Order> readItems() {
        List<Order> items = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
            Pattern inputPattern = Pattern.compile("^(?<quantity>[\\d,.]+)" + "\\s+" + "(?<item>[\\w ]+)$");
            while (true) {
                String input = r.readLine();
                if (input == null || input.isEmpty()) {
                    break;
                }
                Matcher matcher = inputPattern.matcher(input);
                if (matcher.matches()) {
                    Integer quantity = Integer.parseInt(matcher.group("quantity"));
                    String name = matcher.group("item");
                    log.debug("item: {}, quantity: {}", name, quantity);
                    Item item = new Item();
                    item.setName(name);
                    Order o = new Order();
                    o.setItem(item);
                    o.setQuantity(quantity);
                    items.add(o);
                } else {
                    log.warn("Does not match: {}", input);
                }
            }
        } catch (IOException e) {
            log.error("something bad happened with input", e);
        }
        return items.stream().collect(Collectors.toMap(order -> order.getItem().getName(), Function.identity(), (o1, o2) -> {
            Order r = new Order();
            r.setItem(o1.getItem());
            r.setQuantity(o1.getQuantity() + o2.getQuantity());
            return r;
        }));
    }

}
