package group.msg.at.cloud.tools.helm;

import group.msg.at.cloud.tools.helm.core.command.CommandStatusCode;
import group.msg.at.cloud.tools.helm.core.command.UpgradeCommand;
import group.msg.at.cloud.tools.helm.core.command.UpgradeCommandResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@code Mojo} that installs the specified chart.
 */
@Mojo(name = "deploy", requiresProject = true)
public final class DeployReleaseMojo extends AbstractHelmReleaseMojo {

    @Parameter(property = "helm.chartDirectory", required = false)
    private File chartDirectory;

    @Parameter(property = "helm.chartPackageDirectory", required = false)
    private File chartPackageDirectory;

    @Parameter(property = "helm.chartPackage", required = false)
    private File chartPackage;

    @Parameter(property = "helm.force", required = false, defaultValue = "false")
    private boolean force;

    @Parameter(property = "helm.cleanupOnFail", required = false, defaultValue = "true")
    private boolean cleanupOnFail;

    @Parameter(property = "helm.resetValues", required = false, defaultValue = "false")
    private boolean resetValues;

    @Parameter(property = "helm.reuseValues", required = false, defaultValue = "false")
    private boolean reuseValues;

    @Parameter(property = "helm.values", required = false)
    private final Map<String, String> values = new LinkedHashMap<>();

    @Parameter(property = "helm.dryRun", required = false, defaultValue = "false")
    private boolean dryRun;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        UpgradeCommand upgrade = new UpgradeCommand(new Slf4jMavenLogAdapter(getLog()));
        upgrade.setChartDirectory(chartDirectory);
        upgrade.setChartPackageDirectory(chartPackageDirectory);
        upgrade.setChartPackage(chartPackage);
        upgrade.setReleaseName(releaseName);
        upgrade.setAtomic(true);
        upgrade.setWait(true);
        upgrade.setInstall(true);
        upgrade.setCleanupOnFail(this.cleanupOnFail);
        upgrade.setForce(this.force);
        upgrade.setDryRun(dryRun);
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
        upgrade.setResetValues(this.resetValues);
        upgrade.setReuseValues(this.reuseValues);
        upgrade.setValues(this.values);
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
