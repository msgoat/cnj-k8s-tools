/*
 * at41-tools-kubectl-maven-plugin:InstallChartMojo.java
 * (c) Copyright msg systems ag Automotive Technology 2017
 */
package group.msg.at.cloud.tools.helm.maven;

import group.msg.at.cloud.tools.helm.core.command.DeleteCommand;
import group.msg.at.cloud.tools.helm.core.command.DeleteCommandResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * {@code Mojo} that deletes the specified release.
 */
@Mojo(name = "delete", requiresProject = true)
public final class DeleteReleaseMojo extends AbstractHelmMojo {

	/**
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		DeleteCommand delete = new DeleteCommand(new Slf4jMavenLogAdapter(getLog()));
		delete.setReleaseName(helmReleaseName);
		delete.setPurge(true);
		try {
			DeleteCommandResult installResult = delete.call();
		} catch (Exception ex) {
			throw new MojoExecutionException(String.format("Failed to delete helm release %s", helmReleaseName), ex);
		}

	}
}
