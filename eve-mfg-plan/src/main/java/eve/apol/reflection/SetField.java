package eve.apol.reflection;

import java.lang.reflect.Field;

public interface SetField {
    default void setField(String name, Object value) {
        try {
            Field f = this.getClass().getField(name);
            boolean origAccess = f.isAccessible();
            f.setAccessible(true);
            f.set(this, value);
            f.setAccessible(origAccess);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
