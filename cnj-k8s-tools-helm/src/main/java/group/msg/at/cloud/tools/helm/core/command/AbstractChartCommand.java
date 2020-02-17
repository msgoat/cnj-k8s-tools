package group.msg.at.cloud.tools.helm.core.command;

import org.slf4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Abstract base class of all {@code Helm} commands that require a {@code Chart} directory.
 *
 * @param <V>
 */
public abstract class AbstractChartCommand<V> extends AbstractCommand<V> {

    private File chartDirectory;
    private boolean atomic;
    private boolean depUp;
    private boolean devel;
    private boolean dryRun;
    private boolean noHooks;
    private URL chartRepoUrl;
    private String chartRepoUser;
    private String chartRepoPassword;
    private boolean renderSubChartNotes;
    private Map<String, Object> values = new LinkedHashMap<>();
    private String timeout;
    private boolean wait;
    private boolean verify;
    private String chartVersion;
    /*
              --set stringArray          set values on the comma>d line (can specify multiple or separate values with commas: key1=val1,key2=val2)
              --set-file stringArray     set values from respective files specified via the command line (can specify multiple or separate values with commas: key1=path1,key2=path2)
              --set-string stringArray   set STRING values on the command line (can specify multiple or separate values with commas: key1=val1,key2=val2)
         */
     /*
          --tls                      enable TLS for request
          --tls-ca-cert string       path to TLS CA certificate file (default "$HELM_HOME/ca.pem")
          --tls-cert string          path to TLS certificate file (default "$HELM_HOME/cert.pem")
          --tls-hostname string      the server name used to verify the hostname on the returned certificates from the server
          --tls-key string           path to TLS key file (default "$HELM_HOME/key.pem")
          --tls-verify               enable TLS for request and verify remote

     */
/*
  -f, --values valueFiles        specify values in a YAML file or a URL(can specify multiple) (default [])

 */

    public AbstractChartCommand() {
        super();
    }

    public AbstractChartCommand(Logger logger) {
        super(logger);
    }

    public File getChartDirectory() {
        return chartDirectory;
    }

    public void setChartDirectory(File chartDirectory) {
        this.chartDirectory = chartDirectory;
    }

    /**
     * If set, installation process purges chart on fail, also sets {@code wait} flag
     */
    public boolean isAtomic() {
        return this.atomic;
    }

    public void setAtomic(boolean atomic) {
        this.atomic = atomic;
    }

    /**
     * If set, runs an update on all chart dependencies before installing the chart.
     */
    public boolean isDepUp() {
        return depUp;
    }

    public void setDepUp(boolean depUp) {
        this.depUp = depUp;
    }

    /**
     * If set, use development versions, too (equivalent to version ">0.0.0-0").
     */
    public boolean isDevel() {
        return devel;
    }

    public void setDevel(boolean devel) {
        this.devel = devel;
    }

    /**
     * If set, only simulates an install.
     */
    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * If set, prevent all hooks from running.
     */
    public boolean isNoHooks() {
        return noHooks;
    }

    public void setNoHooks(boolean noHooks) {
        this.noHooks = noHooks;
    }

    /**
     * Optional URL to connect to a chart repository containing the chart to install.
     */
    public Optional<URL> getChartRepoUrl() {
        return Optional.ofNullable(this.chartRepoUrl);
    }

    public void setChartRepoUrl(URL chartRepoUrl) {
        this.chartRepoUrl = chartRepoUrl;
    }

    /**
     * Optional user to connect to a chart repository containing the chart to install.
     */
    public Optional<String> getChartRepoUser() {
        return Optional.ofNullable(this.chartRepoUser);
    }

    public void setChartRepoUser(String chartRepoUser) {
        this.chartRepoUser = chartRepoUser;
    }

    /**
     * Optional password to connect to a chart repository containing the chart to install.
     */
    public Optional<String> getChartRepoPassword() {
        return Optional.ofNullable(this.chartRepoPassword);
    }

    public void setChartRepoPassword(String chartRepoPassword) {
        this.chartRepoPassword = chartRepoPassword;
    }

    /**
     * If set, renders subchart notes along with the parent (default: false)
     */
    public boolean isRenderSubChartNotes() {
        return renderSubChartNotes;
    }

    public void setRenderSubChartNotes(boolean renderSubChartNotes) {
        this.renderSubChartNotes = renderSubChartNotes;
    }

    public void addValue(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        values.put(key, value);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * Optional duration in seconds helm will wait for any individual Kubernetes operation (like Jobs for hooks) (default: 300)
     */
    public final Optional<String> getTimeout() {
        return Optional.ofNullable(this.timeout);
    }

    /**
     * Sets the duration Helm will wait on atomic operations to finish.
     */
    public final void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    /**
     * If set, will wait for {@link #timeout} seconds until all Pods, PVCs, Services, and minimum number of Pods of a Deployment are in a ready
     * state before marking the release as successful.
     */
    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    /**
     * If set, verifies the package before installing/upgrading it (default: {@code false}).
     */
    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    /**
     * Optional exact chart version to install. If this is not specified, the latest version is installed.
     */
    public Optional<String> getChartVersion() {
        return Optional.ofNullable(this.chartVersion);
    }

    public void setChartVersion(String chartVersion) {
        this.chartVersion = chartVersion;
    }

    @Override
    protected void collectCommandLineArguments(List<String> arguments) {
        super.collectCommandLineArguments(arguments);
        if (isAtomic()) {
            arguments.add("--atomic");
        }
        if (isDepUp()) {
            arguments.add("--dep-up");
        }
        if (isDevel()) {
            arguments.add("--devel");
        }
        if (isDryRun()) {
            arguments.add("--dry-run");
        }
        if (isNoHooks()) {
            arguments.add("--no-hooks");
        }
        getChartRepoUrl().ifPresent(url -> {
            arguments.add("--repo");
            arguments.add(url.toString());
        });
        getChartRepoUser().ifPresent(user -> {
            arguments.add("--username");
            arguments.add(user);
        });
        getChartRepoPassword().ifPresent(password -> {
            arguments.add("--password");
            arguments.add(password);
        });
        if (isRenderSubChartNotes()) {
            arguments.add("--render-subchart-notes");
        }
        getTimeout().ifPresent(timeout -> {
            arguments.add("--timeout");
            arguments.add(timeout);
        });
        if (isWait()) {
            arguments.add("--wait");
        }
        if (isVerify()) {
            arguments.add("--verify");
        }
        getChartVersion().ifPresent(version -> {
            arguments.add("--version");
            arguments.add(version);
        });

    }
}
