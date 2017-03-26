package eve.apol.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eve.apol.entity.Item;
import eve.apol.entity.Requisite;
import eve.apol.model.ApplicationFactory;
import eve.apol.model.PriceLookup;
import eve.apol.model.RequisiteLookup;
import eve.apol.model.TypeIDLookup;

public class Shell {

    private static final String QUANTITY_REGEX = "(?<quantity>[\\d,.]+)";

    private static final String SPACES_REGEX = "\\s+";

    private static final String ITEM_REGEX = "(?<item>[\\w ]+)";

    private static final Logger log = LoggerFactory.getLogger(Shell.class);

    private TypeIDLookup typeIDLookup;
    private RequisiteLookup requisiteLookup;
    private PriceLookup priceLookup;

    public Shell(TypeIDLookup typeIDLookup, RequisiteLookup requisiteLookup, PriceLookup priceLookup) {
        this.typeIDLookup = typeIDLookup;
        this.requisiteLookup = requisiteLookup;
        this.priceLookup = priceLookup;
    }

    public static void main(String[] args) {
        Shell shell = ApplicationFactory.getShell();
        shell.run();
    }

    private void run() {
        Map<Item, Integer> reqs = readItems().values().stream()
        .map(req -> {
            req.getItem().setTypeID(typeIDLookup.getTypeID(req.getItem().getName()));
            log.trace("{} of {} ({})", req.getQuantity(), req.getItem().getName(), req.getItem().getTypeID());
            return req;
        }).flatMap(requisiteLookup::getRequisites)
        .collect(Collectors.toMap(req -> req.getItem(), Requisite::getQuantity, Integer::sum));
        reqs.entrySet().forEach(item -> System.out.println(item.getKey().getName() + " " + item.getValue()));
        priceLookup.getPrices(reqs.keySet()).thenAccept(prices -> {
           double total = reqs.entrySet().stream().mapToDouble(e -> e.getValue() * prices.get(e.getKey()).getSell()).sum();
           System.out.println("Would buy at " + total);
        });
    }

    private static Map<String, Requisite> readItems() {
        List<Requisite> items = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
            Pattern inputPattern = Pattern.compile("^" + QUANTITY_REGEX + SPACES_REGEX + ITEM_REGEX + "$");
            while (true) {
                String input = r.readLine();
                if (input == null || input.isEmpty()) {
                    break;
                }
                Matcher matcher = inputPattern.matcher(input);
                if (matcher.matches()) {
                    Integer quantity = null;
                    if (matcher.group("quantity") != null) {
                        quantity = Integer.parseInt(matcher.group("quantity"));
                    }
                    String name = matcher.group("item");
                    log.debug("item: {}, quantity: {}", name, quantity);
                    Item item = new Item();
                    item.setName(name);
                    Requisite o = new Requisite();
                    o.setItem(item);
                    o.setQuantity(quantity);
                    items.add(o);
                } else {
                    log.warn("Does not match: {}", input);
                }
            }
        } catch (IOException e) {
            log.error("something bad happened with input", e);
        }
        return items.stream().collect(Collectors.toMap(order -> order.getItem().getName(), Function.identity(), (o1, o2) -> {
            Requisite r = new Requisite();
            r.setItem(o1.getItem());
            r.setQuantity(o1.getQuantity() + o2.getQuantity());
            return r;
        }));
    }

}
