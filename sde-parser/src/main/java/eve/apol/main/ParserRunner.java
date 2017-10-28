package eve.apol.main;

import java.io.File;
import java.io.IOException;

import eve.apol.yamlparsing.SDEReader;

public class ParserRunner {

    public static void main(String[] args) throws IOException {
        long time = System.currentTimeMillis();
        SDEReader s = new SDEReader(new File("typeIDs.yaml"), new File("blueprints.yaml"));
        CSVOutput csv = new CSVOutput(s.readItems(), s.readBlueprints());
        csv.writeBlueprints(new File("blueprint_graph.csv"));
        csv.writeTypeids(new File("typeids.csv"));
        System.out.println("RUNTIME MS " + (System.currentTimeMillis() - time));
        System.out.println("MAX " + Runtime.getRuntime().maxMemory());
        System.out.println("FREE " + Runtime.getRuntime().freeMemory());
        System.out.println("TOTAL " + Runtime.getRuntime().totalMemory());
    }

}
