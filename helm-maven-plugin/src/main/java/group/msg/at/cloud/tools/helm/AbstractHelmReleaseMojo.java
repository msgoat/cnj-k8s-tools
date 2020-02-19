package group.msg.at.cloud.tools.helm;

import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractHelmReleaseMojo extends AbstractHelmMojo {

    @Parameter(property = "helm.releaseName", readonly = true, required = true)
    protected String releaseName;

    @Parameter(property = "helm.namespace", required = true, readonly = true)
    protected String namespace;

    @Parameter(property = "helm.timeout", required = false, readonly = true)
    protected String timeout;
}
