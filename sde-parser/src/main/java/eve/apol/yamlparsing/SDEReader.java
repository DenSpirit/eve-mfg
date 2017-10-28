package eve.apol.yamlparsing;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class SDEReader {

    private File typeIDs;
    private Map<Long, ItemType> items;
    private File blueprints;

    public SDEReader(File typeIDs, File blueprints) {
        this.typeIDs = typeIDs;
        this.blueprints = blueprints;
    }

    public Collection<ItemType> readItems() throws IOException {
        if (items == null) {
            ObjectMapper jacksonMapper = new ObjectMapper(new YAMLFactory());
            items = jacksonMapper.readValue(typeIDs,
                    new TypeReference<Map<Long, ItemType>>() {});
            items.values().removeIf(type -> !type.isPublished());
            items.forEach((typeID, item) -> item.setTypeID(typeID));
        }
        return items.values();
    }

    public Collection<Blueprint> readBlueprints() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        Map<Long, Blueprint> idBlueprint = mapper.readValue(blueprints,
                new TypeReference<Map<Long, Blueprint>>() {});
        return idBlueprint.entrySet().stream()
                .peek(entry -> entry.getValue().setTypeID(entry.getKey()))
                .filter(entry -> blueprintExists(entry.getValue()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private boolean blueprintExists(Blueprint bp) {
        boolean bpExists = items.containsKey(bp.getTypeID());
        boolean matsExist = bp.getActivities().values().stream()
                .flatMap(act -> (Stream<CountedItem>) Stream.concat(act.getMaterials().stream(), act.getProducts().stream()))
                .allMatch(ci -> items.containsKey(ci.getTypeID()));
        return bpExists && matsExist;
    }

}
