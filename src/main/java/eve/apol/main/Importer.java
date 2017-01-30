package eve.apol.main;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.Event;

public class Importer {
    
    private static Logger log = LoggerFactory.getLogger(Importer.class);
    
    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        int batch = 0;
        Runtime runtime = Runtime.getRuntime();
        try (Reader r = new FileReader("/home/dborisevich/devel/CCP/sde/fsd/typeIDs.yaml")) {
            for (Event ev : yaml.parse(r)) {
                log.info(ev.getClass().getSimpleName());
                /*batch++;
                if (batch == 1000) {
                    log.info("BATCH! {} kb used", (runtime.totalMemory() - runtime.freeMemory()) / 1024);
                    batch = 0;
                }*/
            }
        } catch (IOException e) {
            log.error("Problem with reading the file", e);
        }
    }
}
