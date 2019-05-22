package group.msg.at.cloud.tools.helm.maven;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public abstract class AbstractHelmChartMojo extends AbstractHelmMojo {

    @Parameter(property = "helm.chartDirectory", required = true, readonly = true)
    protected File helmChartDirectory;
}
