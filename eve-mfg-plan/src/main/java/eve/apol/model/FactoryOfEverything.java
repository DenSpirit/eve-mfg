package eve.apol.model;

import org.apache.tinkerpop.gremlin.structure.Graph;

import eve.apol.Importer;
import eve.apol.model.FactoryOfEverything.GraphHolder;
import eve.apol.model.impl.GraphLookup;

public class FactoryOfEverything {
    
    public static class GraphHolder {
        private static Graph instance = Importer.fromClassPath();
    }

    private static class LookupHolder {
        private static TypeIDLookup lookup = new GraphLookup(GraphHolder.instance);
    }
    
    public static TypeIDLookup getLookup() {
        return LookupHolder.lookup;
    }

}
