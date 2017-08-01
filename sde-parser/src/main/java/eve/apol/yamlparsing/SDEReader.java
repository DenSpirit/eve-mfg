package eve.apol.yamlparsing;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class SDEReader {

    private File typeIDs;
    private File blueprints;

    public SDEReader(File typeIDs, File blueprints) {
        this.typeIDs = typeIDs;
        this.blueprints = blueprints;
    }

    public Stream<Item> readItems() throws IOException {
        ObjectMapper jacksonMapper = new ObjectMapper(new YAMLFactory());
        Map<String, Item> idItem = jacksonMapper.readValue(typeIDs,
                new TypeReference<Map<String, Item>>() {});
        return idItem.entrySet().stream()
                .map(entry -> {
                    Item item = entry.getValue();
                    long id = Long.parseLong(entry.getKey());
                    item.setTypeID(id);
                    return item;
                })
                .filter(Item::isPublished);
    }

}
