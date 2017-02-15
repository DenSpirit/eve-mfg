package eve.apol.model.impl;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import eve.apol.entity.Item;
import eve.apol.model.PriceLookup;

public class EveCentralLookup implements PriceLookup {
    
    @Override
    public Map<Item, Float> getPrices(List<Item> items) {
        HttpGet req = new HttpGet("http://api.eve-central.com/api/marketstat");
        HttpClient client = HttpClients.createSystem();
        //client.execute(req, HttpContext)
        // TODO Auto-generated method stub
        return null;
    }

}
