package group.msg.at.cloud.tools.helm;

import group.msg.at.cloud.tools.helm.core.command.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;

import java.io.File;

/**
 * {@code Mojo} that pulls the specified chart from the given registry.
 */
@Mojo(name = "pull", requiresProject = true)
public final class PullChartMojo extends AbstractHelmMojo {

    @Parameter(property = "helm.chartRegistry", required = true)
    private String chartRegistry;

    @Parameter(property = "helm.chartRepository", required = true)
    private String chartRepository;

    @Parameter(property = "helm.chartPackageDirectory", required = true)
    private File chartPackageDirectory;

    @Parameter(property = "helm.chartName", required = true)
    private String chartName;

    @Parameter(property = "helm.chartVersion", required = false)
    private String chartVersion;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        PullCommand command = new PullCommand(new Slf4jMavenLogAdapter(getLog()));
        command.setChartRegistry(chartRegistry);
        command.setChartRepository(chartRepository);
        command.setChartPackageDirectory(chartPackageDirectory);
        command.setChartName(chartName);
        command.setChartVersion(chartVersion);
        PullCommandResult result = null;
        try {
            result = command.call();
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Failed to execute command %s", command), ex);
        }
        if (CommandStatusCode.FAILURE.equals(result.getStatusCode())) {
            String msg = String.format("Failed to pull helm chart [%s] from registry [%s]: %s %s", chartName, chartRegistry, result.getStatusCode(), result.getStatusMessage());
            error(msg);
            throw new MojoExecutionException(msg);
        }
    }

    private void loginToRegistry() throws MojoExecutionException {
        Server chartRegistrySettings = settings.getServer(chartRegistry);
        Slf4jMavenLogAdapter logAdapter = new Slf4jMavenLogAdapter(getLog());
        if (chartRegistrySettings != null) {
            RegistryLoginCommand command = new RegistryLoginCommand(logAdapter);
            command.setChartRegistry(chartRegistry);
            command.setUsername(chartRegistrySettings.getUsername());
            command.setPassword(chartRegistrySettings.getPassword());
            RegistryLoginCommandResult result = null;
            try {
                result = command.call();
            } catch (Exception ex) {
                throw new MojoExecutionException(String.format("Failed to execute command %s", command), ex);
            }
            if (CommandStatusCode.FAILURE.equals(result.getStatusCode())) {
                String msg = String.format("Failed to login to registry [%s]: %s %s", chartRegistry, result.getStatusCode(), result.getStatusMessage());
                error(msg);
                throw new MojoExecutionException(msg);
            }
        }
    }
}
