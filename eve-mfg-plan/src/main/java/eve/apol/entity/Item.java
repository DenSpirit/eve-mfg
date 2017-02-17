package eve.apol.entity;

public class Item {

    private String name;

    private long typeID;

    public Item() {
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Item other = (Item) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        if (typeID != other.typeID) return false;
        return true;
    }

    public String getName() {
        return name;
    }

    public long getTypeID() {
        return typeID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (typeID ^ (typeID >>> 32));
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeID(long typeID) {
        this.typeID = typeID;
    }
}