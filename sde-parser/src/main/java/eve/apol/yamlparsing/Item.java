package eve.apol.yamlparsing;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    private long typeID;
    @JsonDeserialize(using = NameDeserializer.class)
    private String name;
    private boolean published;
    private long groupID;
    private BigDecimal basePrice;
    private BigDecimal volume;

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public long getGroupID() {
        return groupID;
    }

    public String getName() {
        return name;
    }

    public long getTypeID() {
        return typeID;
    }

    public boolean isPublished() {
        return published;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public void setTypeID(long typeID) {
        this.typeID = typeID;
    }

    @Override
    public String toString() {
        return "Item [typeID=" + typeID + ", name=" + name + ", published=" + published + ", groupID=" + groupID
                + ", basePrice=" + basePrice + ", volume=" + volume + "]";
    }

}
