package group.msg.at.cloud.tools.helm;

import group.msg.at.cloud.tools.helm.core.command.CommandStatusCode;
import group.msg.at.cloud.tools.helm.core.command.LintCommand;
import group.msg.at.cloud.tools.helm.core.command.LintCommandResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * {@code Mojo} that lints the specified chart.
 */
@Mojo(name = "lint", requiresProject = true)
public final class LintChartMojo extends AbstractHelmChartMojo {

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LintCommand command = new LintCommand(new Slf4jMavenLogAdapter(getLog()));
        command.setChartDirectory(chartDirectory);
        LintCommandResult result = null;
        try {
            result = command.call();
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Failed execute command %s", command), ex);
        }
        if (CommandStatusCode.FAILURE.equals(result.getStatusCode())) {
            String msg = String.format("Failed to lint helm chart at [%s]: %s %s", chartDirectory, result.getStatusCode(), result.getStatusMessage());
            error(msg);
            throw new MojoExecutionException(msg);
        }
    }
}
