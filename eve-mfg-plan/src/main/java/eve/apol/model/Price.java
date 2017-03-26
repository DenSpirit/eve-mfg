package eve.apol.model;

public class Price {

    private long typeid;
    private float buy;
    private float sell;
    private Region region;

    public long getTypeid() {
        return typeid;
    }
    public void setTypeid(long typeid) {
        this.typeid = typeid;
    }
    public float getBuy() {
        return buy;
    }
    public void setBuy(float buy) {
        this.buy = buy;
    }
    public float getSell() {
        return sell;
    }
    public void setSell(float sell) {
        this.sell = sell;
    }
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    
}
