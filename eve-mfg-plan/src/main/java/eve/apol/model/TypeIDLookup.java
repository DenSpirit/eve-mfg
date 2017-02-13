package eve.apol.model;

import eve.apol.entity.Item;

public interface TypeIDLookup {

    Long getTypeID(String name);
    String getName(Long typeID);
    Item getItem(Long typeID);

}
