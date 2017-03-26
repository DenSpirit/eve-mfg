package eve.apol.model;

import org.apache.tinkerpop.gremlin.structure.Graph;

import eve.apol.Importer;
import eve.apol.main.Shell;
import eve.apol.model.impl.GraphTypeIDLookup;
import eve.apol.model.impl.EveCentralLookup;
import eve.apol.model.impl.GraphRequisiteLookup;

public class ApplicationFactory {

    public static Shell getShell() {
        Graph graph = Importer.fromClassPath();
        TypeIDLookup typeIDLookup = new GraphTypeIDLookup(graph);
        RequisiteLookup requisiteLookup = new GraphRequisiteLookup(graph, typeIDLookup);
        PriceLookup priceLookup = new EveCentralLookup();
        return new Shell(typeIDLookup, requisiteLookup, priceLookup);
    }

}
