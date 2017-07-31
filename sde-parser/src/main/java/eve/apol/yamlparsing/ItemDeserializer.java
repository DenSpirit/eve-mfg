package eve.apol.yamlparsing;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ItemDeserializer extends StdDeserializer<Item> {

    public static final Item EMPTY_ITEM = new Item();
    
    private static final long serialVersionUID = -340793382642772120L;

    protected ItemDeserializer(Class<?> vc) {
        super(vc);
    }

    public ItemDeserializer() {
        this(null);
    }

    @Override
    public Item deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Item item = new Item();
        JsonNode node = p.getCodec().readTree(p);
        if (!node.get("published").asBoolean()) {
            return EMPTY_ITEM;
        }
        item.setName(node.get("name").get("en").asText());
        item.setTypeID(Long.parseLong(p.getCurrentName()));
        return item;
    }

}
