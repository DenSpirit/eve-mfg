package eve.apol.yamlparsing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ItemDeserializer.class)
public class Item {
    private long typeID;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTypeID() {
        return typeID;
    }

    public void setTypeID(long typeID) {
        this.typeID = typeID;
    }

    @Override
    public String toString() {
        return "Item [typeID=" + getTypeID() + ", name=" + getName() + "]";
    }
}