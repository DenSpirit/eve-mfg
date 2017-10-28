package eve.apol.yamlparsing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product extends CountedItem {
    @JsonProperty
    private Float probability;
}
