package eve.apol.eveblueprints;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoIo;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BadOpenProfilingTest {
	
	private static Logger log = LoggerFactory.getLogger(BadOpenProfilingTest.class);
	
	@Test
	public void test() throws IOException {
		StopWatch sw = new StopWatch();
		log.info("creating graph and writing");
		sw.start();
		File output = File.createTempFile("graph", "gryo");
		InputStream typeids = BadOpenProfilingTest.class.getResourceAsStream("/typeids.csv");
		InputStream blueprints = BadOpenProfilingTest.class.getResourceAsStream("/blueprint_graph.csv");
		new Importer(typeids, blueprints, output).importBlueprints();
		sw.stop();
		log.info("created in {}", sw.getTime());
		sw.reset();
		Runtime rt = Runtime.getRuntime();
		rt.gc();
		rt.gc();
		NumberFormat nf = DecimalFormat.getNumberInstance();
		nf.setGroupingUsed(true);
		log.info("memory {}", nf.format(rt.totalMemory() - rt.freeMemory()));
		log.info("opening graph", sw.getTime());
		sw.start();
		try(TinkerGraph g = TinkerGraph.open()) {
			g.io(GryoIo.build()).readGraph(output.getAbsolutePath());
			rt.gc();
			rt.gc();
			log.info("memory {}", nf.format(rt.totalMemory() - rt.freeMemory()));
		}
		sw.stop();
		log.info("opened in {}", sw.getTime());
		
	}

}
