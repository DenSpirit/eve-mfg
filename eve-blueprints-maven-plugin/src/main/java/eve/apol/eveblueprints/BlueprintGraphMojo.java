package eve.apol.eveblueprints;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.sonatype.plexus.build.incremental.BuildContext;


/** @goal generate-graph
 *  @phase process-resources */
//@Mojo(name = "generate-graph", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
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
        File blueprintsGraphFile = new File(outputDirectory, "blueprints.gryo");
        boolean upToDate = buildContext.isUptodate(blueprintsGraphFile, typeIDs) && buildContext.isUptodate(blueprintsGraphFile, blueprints);
        if (upToDate) {
            getLog().info("typeIDs and blueprints up-to-date");
            return;
        }
        
        getLog().info("trying to write into " + blueprintsGraphFile);
        getLog().info("directory exists: " + outputDirectory.exists());
        
        
        Importer i = new Importer(typeIDs, blueprints, blueprintsGraphFile);
        i.importBlueprints();

        getLog().info(getPluginContext().toString());
    }
    
}
