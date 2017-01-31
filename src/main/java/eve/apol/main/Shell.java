package eve.apol.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shell {

	private static final Logger log = LoggerFactory.getLogger(Shell.class);

	public static void main(String[] args) {

		try (Neo4jGraph graph = Neo4jGraph.open("/tmp/neo4j")) {
			try (BufferedReader typeIDs = new BufferedReader(
					new InputStreamReader(Shell.class.getResourceAsStream("/typeids.csv")))) {
				String line = typeIDs.readLine();
				while (line != null) {
					int sep = line.indexOf(';');
					long typeID = Long.parseLong(line.substring(0, sep));
					String name = line.substring(sep + 1);
					graph.addVertex(T.label, "item", "typeID", typeID, "name", name.trim());
					log.info("added {}, {}", typeID, name);
					line = typeIDs.readLine();
				}
			}
		} catch (Exception e) {
			log.error("Failed to close graph", e);
		}
	}

}
