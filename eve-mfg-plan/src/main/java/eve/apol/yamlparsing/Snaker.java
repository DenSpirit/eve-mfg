package eve.apol.yamlparsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import eve.apol.yamlparsing.stringgrouping.StringGrouper;

public class Snaker {

    private static Logger log = LoggerFactory.getLogger(Snaker.class);

    private File file;

    public Snaker(File result) {
        this.file = result;
    }

    public List<Item> rock() {
        Yaml y = new Yaml();


        try (BufferedReader buf = new BufferedReader(new FileReader(file))) {
            StopWatch sw = new StopWatch();
            sw.start();
            Iterable<String> gen = new StringGrouper(buf.lines().iterator(), "^\\d+:$");
            List<Item> items = StreamSupport.stream(gen.spliterator(), false).map((str) -> {
                Item i = new Item();
                MappingNode c = (MappingNode) y.compose(new StringReader(str));
                NodeTuple tuple = c.getValue().iterator().next();
                ScalarNode typeid = (ScalarNode) tuple.getKeyNode();
                i.setTypeID(typeid.getValue());
                MappingNode item = (MappingNode) tuple.getValueNode();
                for (NodeTuple key : item.getValue()) {
                    ScalarNode field = (ScalarNode) key.getKeyNode();
                    if ("name".equals(field.getValue())) {
                        ScalarNode en = ((ScalarNode) toMap(key.getValueNode()).get("en"));
                        if (en != null) {
                            i.setField("name", en.getValue());
                        }
                    }
                }
                return i;
            }).collect(Collectors.toList());
            sw.stop();
            log.info("Loaded {} items in {}ms", items.size(), sw.getTime());
            return items;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    private Map<String, Node> toMap(Node valueNode) {
        MappingNode node = (MappingNode) valueNode;
        Map<String, Node> keys = new HashMap<>(node.getValue().size());
        for (NodeTuple n : node.getValue()) {
            keys.put(((ScalarNode) n.getKeyNode()).getValue(), n.getValueNode());
        }
        return keys;
    }
}
