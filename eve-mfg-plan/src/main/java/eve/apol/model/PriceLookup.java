package eve.apol.model;

import java.util.List;
import java.util.Map;

import eve.apol.entity.Item;

public interface PriceLookup {

    Map<Item, Float> getPrices(List<Item> items);
    
}
