package eve.apol.main;

import java.io.File;
import java.io.IOException;

import eve.apol.yamlparsing.SDEReader;

public class ParserRunner {

	public static void main(String[] args) throws IOException {
		SDEReader s = new SDEReader(new File("typeIDs.yaml"), new File("blueprints.yaml"));
		s.readItems().forEach(System.out::println);
	}

}
