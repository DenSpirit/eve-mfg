package eve.apol.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import eve.apol.yamlparsing.Activity;
import eve.apol.yamlparsing.ActivityType;
import eve.apol.yamlparsing.Blueprint;
import eve.apol.yamlparsing.ItemType;
import eve.apol.yamlparsing.Material;
import eve.apol.yamlparsing.Product;

public class CSVOutput {

    private Collection<ItemType> items;
    private Collection<Blueprint> blueprints;

    public CSVOutput(Collection<ItemType> items, Collection<Blueprint> blueprints) {
        this.items = items;
        this.blueprints = blueprints;
    }

    public void writeBlueprints(File file) throws IOException {
        file.createNewFile();
        try(PrintWriter pw = new PrintWriter(file)) {
            blueprints.stream()
                .flatMap(CSVOutput::convert)
                .forEach(pw::println);
        }
    }

    public void writeTypeids(File file) throws IOException {
        file.createNewFile();
        try(PrintWriter pw = new PrintWriter(file)) {
            items.forEach(item -> pw.println(convert(item)));
        }
    }

    private static String convert(ItemType type) {
        return String.join(";", String.valueOf(type.getTypeID()),
                type.getName(),
                String.valueOf(type.getGroupID()),
                String.valueOf(type.getVolume()),
                String.valueOf(type.getBasePrice()));
    }

    private static Stream<String> convert(Blueprint bp) {
        long bpId = bp.getTypeID();
        return bp.getActivities().entrySet()
                .stream()
                .flatMap(entry -> {
                    List<String> list = new ArrayList<>();
                    ActivityType type = entry.getKey();
                    Activity act = entry.getValue();
                    for(Product pr: act.getProducts()) {
                        for(Material mat: act.getMaterials()) {
                            list.add(String.format("%d;%s;%d;%d;%d;%d",
                                    bpId, type.name(),
                                    mat.getTypeID(), mat.getQuantity(),
                                    pr.getTypeID(), pr.getQuantity()));
                        }
                    }
                    return list.stream();
                });
    }

}
