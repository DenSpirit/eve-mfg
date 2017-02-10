package eve.apol.model;

public interface TypeIDLookup {

    Long getTypeID(String name);
    String getName(Long typeID);

}
