/*
 * at41-tools-kubectl-maven-plugin:InstallChartMojo.java
 * (c) Copyright msg systems ag Automotive Technology 2017
 */
package group.msg.at.cloud.tools.helm.maven;

import group.msg.at.cloud.tools.helm.core.command.CommandStatusCode;
import group.msg.at.cloud.tools.helm.core.command.InstallCommand;
import group.msg.at.cloud.tools.helm.core.command.InstallCommandResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * {@code Mojo} that installs the specified chart.
 */
@Mojo(name = "install", requiresProject = true)
public final class InstallChartMojo extends AbstractHelmChartMojo {

	@Parameter(property = "helm.namespace", required = false, readonly = true)
	private String helmNamespace;

	/**
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		InstallCommand install = new InstallCommand(new Slf4jMavenLogAdapter(getLog()));
		install.setChartDirectory(helmChartDirectory);
		install.setReleaseName(helmReleaseName);
		install.setAtomic(true);
		install.setWait(true);
		if (this.helmNamespace != null) {
			install.setNamespace(this.helmNamespace);
		} else {
			warn("No Kubernetes namespace specified, will install on default namespace of current kubectl context!");
		}
		InstallCommandResult result = null;
		try {
			result = install.call();
		} catch (Exception ex) {
			throw new MojoExecutionException(String.format("Failed to install helm release %s", helmReleaseName), ex);
		}
		if (CommandStatusCode.FAILURE.equals(result.getStatusCode())) {
			String msg = String.format("Failed to install release [%s]: %s %s", helmReleaseName, result.getStatusCode(), result.getStatusMessage());
			error(msg);
			throw new MojoExecutionException(msg);
		}
	}
}
