package group.msg.at.cloud.tools.helm;

import group.msg.at.cloud.tools.helm.core.command.UninstallCommand;
import group.msg.at.cloud.tools.helm.core.command.UninstallCommandResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * {@code Mojo} that deletes the specified release.
 */
@Mojo(name = "uninstall", requiresProject = true)
public final class UninstallReleaseMojo extends AbstractHelmReleaseMojo {

    /**
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        UninstallCommand delete = new UninstallCommand(new Slf4jMavenLogAdapter(getLog()));
        delete.setReleaseName(releaseName);
        if (this.namespace != null) {
            delete.setNamespace(this.namespace);
        }
        try {
            UninstallCommandResult installResult = delete.call();
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Failed to delete helm release %s", releaseName), ex);
        }
    }
}
