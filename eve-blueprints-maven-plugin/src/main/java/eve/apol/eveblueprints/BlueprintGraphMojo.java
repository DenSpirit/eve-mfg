package eve.apol.eveblueprints;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;


/** @goal generate-graph
 *  @phase process-resources */
//@Mojo(name = "generate-graph", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class BlueprintGraphMojo extends AbstractMojo {

    @Component
    /** @component */
    private BuildContext buildContext;

    @Parameter(defaultValue = "${basedir}/src/main/resources/typeids.csv", property = "typeIDs")
    /** @parameter name="typeIDs" default-value="${basedir}/src/main/resources/typeids.csv" */
    private File typeIDs;

    @Parameter(defaultValue = "${basedir}/src/main/resources/blueprint_graph.csv", property = "typeIDs")
    /** @parameter name="blueprints" default-value="${basedir}/src/main/resources/blueprint_graph.csv" */
    private File blueprints;

    @Parameter(defaultValue = "${project.build.outputDirectory}", property = "outputDir", required = true)
    /** @parameter name="outputDir" default-value="${project.build.outputDirectory}" */
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        File blueprintsGraphFile = new File(outputDirectory, "blueprints.gryo");
        getLog().info("trying to write into " + blueprintsGraphFile);
        getLog().info("directory exists: " + outputDirectory.exists());
        
        
        Importer i = new Importer(typeIDs, blueprints, blueprintsGraphFile);
        i.importBlueprints();

        getLog().info(getPluginContext().toString());
    }
    
}
