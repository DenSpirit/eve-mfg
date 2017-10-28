package eve.apol.yamlparsing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.EnumMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Blueprint {
    private long typeID;
    @JsonDeserialize(as = EnumMap.class)
    private Map<ActivityType, Activity> activities;

    public Map<ActivityType, Activity> getActivities() {
        return activities;
    }

    public void setActivities(Map<ActivityType, Activity> activities) {
        this.activities = activities;
    }

    public long getTypeID() {
        return typeID;
    }

    public void setTypeID(long typeID) {
        this.typeID = typeID;
    }

    @Override
    public String toString() {
        return "Blueprint [typeID=" + typeID + ", activities=" + activities + "]";
    }
}
