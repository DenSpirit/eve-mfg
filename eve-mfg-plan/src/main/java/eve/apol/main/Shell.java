package eve.apol.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoIo;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shell {

    private static final Logger log = LoggerFactory.getLogger(Shell.class);
    
    private class Item {
        private Integer quantity;
        private String name;
        private long typeID;
        private List<Item> requirements;

        public Item(Integer quantity, String name) {
            super();
            this.quantity = quantity;
            this.name = name;
        }
    }

    public static void main(String[] args) {
        try (TinkerGraph graph = TinkerGraph.open(); InputStream directIn = Shell.class.getResourceAsStream("/blueprints.gryo");
             InputStream in = new GZIPInputStream(directIn)) {
            graph.io(GryoIo.build()).reader().create().readGraph(in, graph);

            GraphTraversalSource g = graph.traversal();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
                Pattern inputPattern = Pattern.compile("^(?<quantity>[\\d,.]+)" + "\\s+" + "(?<item>[\\w ]+)$");
                while (true) {
                    String input = r.readLine();
                    if (input == null) {
                        break;
                    }
                    Matcher matcher = inputPattern.matcher(input);
                    if (matcher.matches()) {
                        Integer quantity = Integer.parseInt(matcher.group("quantity"));
                        String item = matcher.group("item");
                        log.debug("item: {}, quantity: {}", item, quantity);
                    } else {
                        log.warn("Does not match: {}", input);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("bad build, no graph in classpath", e);
        }
    }

}
