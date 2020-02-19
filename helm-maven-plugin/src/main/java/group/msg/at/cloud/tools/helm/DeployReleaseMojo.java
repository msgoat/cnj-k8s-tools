package group.msg.at.cloud.tools.helm;

import group.msg.at.cloud.tools.helm.core.command.CommandStatusCode;
import group.msg.at.cloud.tools.helm.core.command.UpgradeCommand;
import group.msg.at.cloud.tools.helm.core.command.UpgradeCommandResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * {@code Mojo} that installs the specified chart.
 */
@Mojo(name = "deploy", requiresProject = true)
public final class DeployReleaseMojo extends AbstractHelmReleaseMojo {

    @Parameter(property = "helm.chartDirectory", required = true, readonly = true)
    protected File chartDirectory;

    @Parameter(property = "helm.force", required = false, readonly = true, defaultValue = "false")
    protected boolean force;

    @Parameter(property = "helm.cleanupOnFail", required = false, readonly = true, defaultValue = "true")
    protected boolean cleanupOnFail;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        UpgradeCommand upgrade = new UpgradeCommand(new Slf4jMavenLogAdapter(getLog()));
        upgrade.setChartDirectory(chartDirectory);
        upgrade.setReleaseName(releaseName);
        upgrade.setAtomic(true);
        upgrade.setWait(true);
        upgrade.setInstall(true);
        upgrade.setCleanupOnFail(this.cleanupOnFail);
        upgrade.setForce(this.force);
        if (this.debug) {
            upgrade.setDebug(true);
        }
        if (this.namespace != null) {
            upgrade.setNamespace(this.namespace);
        } else {
            warn("No Kubernetes namespace specified, will install on default namespace of current kubectl context!");
        }
        if (this.timeout != null) {
            upgrade.setTimeout(this.timeout);
        }
        UpgradeCommandResult result = null;
        try {
            result = upgrade.call();
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Failed to install helm release %s", releaseName), ex);
        }
        if (CommandStatusCode.FAILURE.equals(result.getStatusCode())) {
            String msg = String.format("Failed to deploy release [%s]: %s %s", releaseName, result.getStatusCode(), result.getStatusMessage());
            error(msg);
            throw new MojoExecutionException(msg);
        }
    }
}
