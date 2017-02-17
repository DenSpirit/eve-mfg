package eve.apol.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eve.apol.entity.Item;
import eve.apol.model.PriceLookup;

public class EveCentralLookup implements PriceLookup {

    private static long[] REGIONLOCK;

    private static URI EVE_CENTRAL;


    private HttpClient client = HttpClients.createSystem();

    private static DocumentBuilder b;

    static {
        try {
            EVE_CENTRAL = new URI("https://api.eve-central.com/api/marketstat");
            REGIONLOCK = new long[]{ 10000032L, // SinqLaison
                    10000042L, // Metropolis
                    10000002L, // TheForge
                    10000030L, // Heimatar
                    10000028L, // MoldenHeath
                    10000043L  // Domain
            };

            b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (URISyntaxException | ParserConfigurationException e) {
            throw new Error(e);
        }
    }

    private URI buildEveCentralQuery(List<Item> items) {
        try {
            URIBuilder b = new URIBuilder(EVE_CENTRAL);
            items.forEach(item -> b.addParameter("typeid", String.valueOf(item.getTypeID())));
            Arrays.stream(REGIONLOCK).forEach(region -> b.addParameter("regionlimit", String.valueOf(region)));
            return b.build();
        } catch (URISyntaxException e) {
            throw new Error(e);
        }
    }

    @Override
    public Map<Item, Float> getPrices(List<Item> items) {
        Map<Long, Item> typeID = items.stream().collect(Collectors.toMap(Item::getTypeID, Function.identity()));

        try {
            HttpResponse pricesData = client.execute(new HttpGet(buildEveCentralQuery(items)));
            Stream<Map.Entry<Long, Float>> itemPrices = readXMLPrices(pricesData.getEntity());
            return itemPrices.collect(Collectors.toMap(e -> typeID.get(e.getKey()), e -> e.getValue()));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private Stream<Entry<Long, Float>> readXMLPrices(HttpEntity entity) {
        try(InputStream stream = entity.getContent()) {
            Document d = b.parse(stream);
            d.get
        } catch (UnsupportedOperationException | IOException | SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return Stream.empty();
        }
    }

}
