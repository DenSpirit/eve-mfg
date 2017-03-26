package eve.apol.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import eve.apol.entity.Item;

public interface PriceLookup {

    CompletionStage<Map<Item, Price>> getPrices(Collection<Item> items);
    
}
