package eve.apol.yamlparsing;

public class CountedItem {
    private int quantity;
    private long typeID;

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public long getTypeID() {
        return typeID;
    }
    public void setTypeID(long typeID) {
        this.typeID = typeID;
    }
    @Override
    public String toString() {
        return quantity + " of typeID=" + typeID;
    }
}
