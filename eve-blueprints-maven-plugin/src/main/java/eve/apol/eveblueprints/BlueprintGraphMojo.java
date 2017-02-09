package eve.apol.eveblueprints;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoIo;
import org.sonatype.plexus.build.incremental.BuildContext;


/**
 * @goal generate-graph
 * @phase process-resources
 */
// @Mojo(name = "generate-graph", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class BlueprintGraphMojo extends AbstractMojo {

    /** @component */
    private BuildContext buildContext;

    /** @parameter name="typeIDs" default-value="${basedir}/src/main/resources/typeids.csv" */
    private File typeIDs;

    /** @parameter name="blueprints" default-value="${basedir}/src/main/resources/blueprint_graph.csv" */
    private File blueprints;

    /** @parameter name="outputDir" default-value="${project.build.outputDirectory}" */
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
    	try {
        File blueprintsGraphFile = new File(outputDirectory, "blueprints.gryo");
        boolean upToDate = buildContext.isUptodate(blueprintsGraphFile, typeIDs) && buildContext.isUptodate(blueprintsGraphFile, blueprints);
        if (upToDate) {
            getLog().info("typeIDs and blueprints up-to-date");
            return;
        }

        getLog().info("trying to write into " + blueprintsGraphFile);
        getLog().info("directory exists: " + outputDirectory.exists());
        Importer i;
			i = new Importer(new FileInputStream(typeIDs), new FileInputStream(blueprints), blueprintsGraphFile);
        i.importBlueprints();

        Importer i = new Importer(typeIDs, blueprints);
        try (Graph g = i.importBlueprints(); GZIPOutputStream blueprintStream = new GZIPOutputStream(new FileOutputStream(blueprintsGraphFile))) {
            if (g == null) {
                throw new MojoExecutionException("Some files were not present");
            }
            g.io(GryoIo.build()).writer().create().writeGraph(blueprintStream, g);
        } catch (Exception e) {
            throw new MojoExecutionException("Could not create graph", e);
        }
        getLog().info(getPluginContext().toString());
    	} catch (FileNotFoundException e) {
    		throw new MojoExecutionException("Input files are not present", e);
    	}
    }

}
