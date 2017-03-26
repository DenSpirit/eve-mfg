package eve.apol.model.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.json.JSONArray;
import org.json.JSONObject;

import eve.apol.entity.Item;
import eve.apol.model.Price;
import eve.apol.model.PriceLookup;
import eve.apol.model.Region;

public class EveCentralLookup implements PriceLookup {

    private static URI EVE_CENTRAL;

    private CloseableHttpAsyncClient client;
    
    public EveCentralLookup() {
        client = HttpAsyncClients.createDefault();
    }

    static {
        try {
            EVE_CENTRAL = new URI("https://api.eve-central.com/api/marketstat/json");
        } catch (URISyntaxException e) {
            throw new Error(e);
        }
    }

    private URI buildEveCentralQuery(Iterable<Item> items) {
        try {
            URIBuilder b = new URIBuilder(EVE_CENTRAL);
            items.forEach(item -> b.addParameter("typeid", String.valueOf(item.getTypeID())));
            Arrays.stream(Region.values()).forEach(region -> b.addParameter("regionlimit", String.valueOf(region.getRegionID())));
            return b.build();
        } catch (URISyntaxException e) {
            throw new Error(e);
        }
    }

    @Override
    public CompletionStage<Map<Item, Price>> getPrices(Collection<Item> items) {
        ensureRunning();
        Map<Long, Item> typeID = items.stream().collect(Collectors.toMap(Item::getTypeID, Function.identity()));
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        client.execute(new HttpGet(buildEveCentralQuery(items)), new FutureResolver<>(future));
        return future
                .thenApply(HttpResponse::getEntity)
                .thenApply(EveCentralLookup::readJSONPrices)
                .handle((prices, ex) -> ex == null ? prices : Stream.<Entry<Long, Price>>empty())
                .thenApply(itemPrices -> itemPrices.collect(Collectors.toMap(e -> typeID.get(e.getKey()), e -> e.getValue())));
    }

    private void ensureRunning() {
        if (!client.isRunning()) {
            client.start();
        }
    }

    private static Stream<Entry<Long, Price>> readJSONPrices(HttpEntity entity) {
        try (InputStream stream = entity.getContent();
                BufferedReader buf = new BufferedReader(new InputStreamReader(stream))) {
            String jsonString = buf.lines().collect(Collectors.joining());
            @SuppressWarnings("unused")
            JSONArray arr = new JSONArray(jsonString);
            return Stream.empty();
        } catch (UnsupportedOperationException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return Stream.empty();
        }
    }

}
