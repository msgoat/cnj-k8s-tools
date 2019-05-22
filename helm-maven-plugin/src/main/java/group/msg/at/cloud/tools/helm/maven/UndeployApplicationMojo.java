/*
 * at41-tools-kubectl-maven-plugin:UndeployApplicationMojo.java
 * (c) Copyright msg systems ag Automotive Technology 2017
 */
package group.msg.at.cloud.tools.helm.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * {@code Mojo} that deploys the current application to the specified AWS ECS
 * cluster environment.
 * <p>
 * Currently, only pushes with application descriptors work properly.
 * </p>
 * 
 * @author theism
 * @version 1.0
 * @since 04.11.2017
 */
@Mojo(name = "undeploy", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST, requiresProject = true)
public final class UndeployApplicationMojo extends AbstractHelmMojo {

	/**
	 * Name of the application descriptor file to use.
	 * <p>
	 * Defaults to {@code aws-ecs.yml}.
	 * </p>
	 */
	@Parameter(defaultValue = "aws-ecs.yml", required = false, readonly = true)
	private String applicationDescriptor;

	/**
	 * Controls, if the undeployment of an application should be skipped.
	 * <p>
	 * Skipping an undeploy goal makes sense if an application should stay on an
	 * environment after all tests have passed green.
	 * </p>
	 */
	@Parameter(property = "aws-ecs.undeploy.skip", defaultValue = "false", required = false)
	private boolean skip;

	/**
	 * Undeploys the specified application from the specified AWS ECS cluster.
	 * <p>
	 * All heavy-lifting is actually done by the AT.41 AWS ECS command line
	 * tools.
	 * </p>
	 * 
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
	}
}
