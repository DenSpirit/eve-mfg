package eve.apol.yamlparsing;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class NameDeserializer extends StdDeserializer<String> {

    private static final long serialVersionUID = -340793382642772120L;

    protected NameDeserializer(Class<?> vc) {
        super(vc);
    }

    public NameDeserializer() {
        this(null);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode name = p.getCodec().readTree(p);
        if(name.get("en") != null) {
            return name.get("en").asText();
        } else {
            return null;
        }
    }

}
