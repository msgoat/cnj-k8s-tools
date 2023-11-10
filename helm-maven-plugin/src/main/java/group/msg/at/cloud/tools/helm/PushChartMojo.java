package group.msg.at.cloud.tools.helm;

import group.msg.at.cloud.tools.helm.core.command.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;

import java.io.File;

/**
 * {@code Mojo} that pushes the specified chart package
 */
@Mojo(name = "push", requiresProject = true, defaultPhase = LifecyclePhase.INSTALL)
public final class PushChartMojo extends AbstractHelmMojo {

    @Parameter(property = "helm.chartRegistry", required = true)
    protected String chartRegistry;

    @Parameter(property = "helm.chartRepository", required = true)
    private String chartRepository;

    @Parameter(property = "helm.chartPackage", required = false)
    private File chartPackage;

    @Parameter(property = "helm.chartPackageDirectory", required = false)
    private File chartPackageDirectory;

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ensureLoginToRegistry();
        PushCommand command = new PushCommand(new Slf4jMavenLogAdapter(getLog()));
        command.setChartRegistry(chartRegistry);
        command.setChartRepository(chartRepository);
        command.setChartPackage(chartPackage);
        command.setChartPackageDirectory(chartPackageDirectory);
        PushCommandResult result = null;
        try {
            result = command.call();
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Failed to execute command %s", command), ex);
        }
        if (CommandStatusCode.FAILURE.equals(result.getStatusCode())) {
            String msg = String.format("Failed to push packaged helm chart at [%s] to registry [%s]: %s %s", chartPackage, chartRegistry, result.getStatusCode(), result.getStatusMessage());
            error(msg);
            throw new MojoExecutionException(msg);
        }
    }

    private void ensureLoginToRegistry() throws MojoExecutionException {
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
