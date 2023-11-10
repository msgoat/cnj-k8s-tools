package group.msg.at.cloud.tools.helm;

import group.msg.at.cloud.tools.helm.core.command.CommandStatusCode;
import group.msg.at.cloud.tools.helm.core.command.InstallCommand;
import group.msg.at.cloud.tools.helm.core.command.InstallCommandResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * {@code Mojo} that installs the specified chart.
 */
@Mojo(name = "install", requiresProject = true)
public final class InstallReleaseMojo extends AbstractHelmReleaseMojo {

    @Parameter(property = "helm.chartDirectory", required = true)
    private File chartDirectory;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        InstallCommand install = new InstallCommand(new Slf4jMavenLogAdapter(getLog()));
        install.setChartDirectory(chartDirectory);
        install.setReleaseName(releaseName);
        install.setAtomic(true);
        install.setWait(true);
        if (this.debug) {
            install.setDebug(true);
        }
        if (this.namespace != null) {
            install.setNamespace(this.namespace);
        } else {
            warn("No Kubernetes namespace specified, will install on default namespace of current kubectl context!");
        }
        InstallCommandResult result = null;
        try {
            result = install.call();
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Failed to install helm release %s", releaseName), ex);
        }
        if (CommandStatusCode.FAILURE.equals(result.getStatusCode())) {
            String msg = String.format("Failed to install release [%s]: %s %s", releaseName, result.getStatusCode(), result.getStatusMessage());
            error(msg);
            throw new MojoExecutionException(msg);
        }
    }
}
