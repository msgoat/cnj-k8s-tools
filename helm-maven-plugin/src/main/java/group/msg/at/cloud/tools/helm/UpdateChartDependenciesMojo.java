package group.msg.at.cloud.tools.helm;

import group.msg.at.cloud.tools.helm.core.command.CommandStatusCode;
import group.msg.at.cloud.tools.helm.core.command.UpdateDependenciesCommand;
import group.msg.at.cloud.tools.helm.core.command.UpdateDependenciesCommandResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * {@code Mojo} that updates all dependencies of the specified chart.
 */
@Mojo(name = "dependency-update", requiresProject = true)
public final class UpdateChartDependenciesMojo extends AbstractHelmChartMojo {

    @Parameter(property = "helm.namespace", required = false, readonly = true)
    private String namespace;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        UpdateDependenciesCommand update = new UpdateDependenciesCommand(new Slf4jMavenLogAdapter(getLog()));
        update.setChartDirectory(chartDirectory);
        update.setAtomic(false);
        update.setWait(false);
        if (this.debug) {
            update.setDebug(true);
        }
        if (this.namespace != null) {
            update.setNamespace(this.namespace);
        }
        UpdateDependenciesCommandResult result = null;
        try {
            result = update.call();
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Failed to update dependencies of chart [%s]", chartDirectory), ex);
        }
        if (CommandStatusCode.FAILURE.equals(result.getStatusCode())) {
            String msg = String.format("Failed to update dependencies of chart [%s]: %s %s", chartDirectory, result.getStatusCode(), result.getStatusMessage());
            error(msg);
            throw new MojoExecutionException(msg);
        }
    }
}
