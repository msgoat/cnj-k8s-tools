package group.msg.at.cloud.tools.helm;

import group.msg.at.cloud.tools.helm.core.command.CommandStatusCode;
import group.msg.at.cloud.tools.helm.core.command.PackageCommand;
import group.msg.at.cloud.tools.helm.core.command.PackageCommandResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * {@code Mojo} that lints the specified chart.
 */
@Mojo(name = "package", requiresProject = true, defaultPhase = LifecyclePhase.PACKAGE)
public final class PackageChartMojo extends AbstractHelmChartMojo {

    @Parameter(property = "helm.chartVersion", required = false)
    private String chartVersion;

    @Parameter(property = "helm.chartAppVersion", required = false)
    private String chartAppVersion;

    @Parameter(property = "helm.chartPackageDirectory", required = true)
    private File chartPackageDirectory;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        PackageCommand command = new PackageCommand(new Slf4jMavenLogAdapter(getLog()));
        command.setChartDirectory(chartDirectory);
        command.setChartPackageDirectory(chartPackageDirectory);
        if (chartVersion != null) {
            command.setChartVersion(chartVersion);
        }
        if (chartAppVersion != null) {
            command.setChartAppVersion(chartAppVersion);
        }
        PackageCommandResult result = null;
        try {
            result = command.call();
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Failed execute command %s", command), ex);
        }
        if (CommandStatusCode.FAILURE.equals(result.getStatusCode())) {
            String msg = String.format("Failed to package helm chart at [%s]: %s %s", chartDirectory, result.getStatusCode(), result.getStatusMessage());
            error(msg);
            throw new MojoExecutionException(msg);
        }
    }
}
