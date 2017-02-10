package eve.apol.entity;

public class Facility {
    
    private FacilityType type;
    private String name;
    private long stationID;
    
    public Facility() {
    }
    public FacilityType getType() {
        return type;
    }
    public void setType(FacilityType type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getStationID() {
        return stationID;
    }
    public void setStationID(long stationID) {
        this.stationID = stationID;
    }
    
}
