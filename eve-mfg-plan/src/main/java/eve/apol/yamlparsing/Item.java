package eve.apol.yamlparsing;

import eve.apol.reflection.SetField;

public class Item implements SetField {
    long typeID;
    String name;
    
    public void setTypeID(String typeID) {
        this.typeID = Long.parseLong(typeID);
    }
    
    @Override
    public void setField(String name, Object value) {
        if("typeID".equals(name)) {
            typeID = (long) value;
        } else if ("name".equals(name)){
            this.name = (String) value;
        }
    }

    @Override
    public String toString() {
        return "Item [typeID=" + typeID + ", name=" + name + "]";
    }
}