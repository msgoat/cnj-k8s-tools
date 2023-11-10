package group.msg.at.cloud.tools.helm;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public abstract class AbstractHelmChartMojo extends AbstractHelmMojo {

    @Parameter(property = "helm.chartDirectory", required = true)
    protected File chartDirectory;
}
