package group.msg.at.cloud.tools.helm.core.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Abstract base class for all helm command classes that encapsulates some common behaviour.
 * @param <V> concrete helm command subclass
 */
public abstract class AbstractCommand<V> implements Callable<V> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private boolean debug;
    private File home;
    private String host;
    private String kubeContext;
    private File kubeConfig;
    private int tillerConnectionTimeout;
    private String tillerNamespace;
    private File currentDirectory;

    public AbstractCommand() {
    }

    public AbstractCommand(Logger logger) {
        this.logger = logger;
    }

    /**
     * Enables verbose output (default: {@code false}).
     *
     * @return {@code true}, if verbose output is enabled; {@code false} otherwise.
     */
    public final boolean isDebug() {
        return this.debug;
    }

    public final void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Optional override of Helms configuration directory (default: contents of env var {@code HELM_HOME}.
     */
    public final Optional<File> getHome() {
        return Optional.ofNullable(this.home);
    }

    public final void setHome(File home) {
        Objects.requireNonNull(home, "home must not be null");
        this.home = home;
    }

    /**
     * Optional override of address of Tiller (default: contents of env var {@code HELM_HOST}.
     */
    public final Optional<String> getHost() {
        return Optional.ofNullable(this.host);
    }

    public final void setHost(String host) {
        Objects.requireNonNull(host, "host must not be null");
        this.host = host;
    }

    /**
     * Optional name of a specific kubectl context to use (default: current kubectl context).
     */
    public final Optional<String> getKubeContext() {
        return Optional.ofNullable(this.kubeContext);
    }

    public final void setKubeContext(String kubeContext) {
        Objects.requireNonNull(kubeContext, "kubeContext must not be null");
        this.kubeContext = kubeContext;
    }

    /**
     * Optional override of the kubeconfig file to use (default: contents of env var {@code KUBECONFIG}).
     */
    public final Optional<File> getKubeConfig() {
        return Optional.ofNullable(this.kubeConfig);
    }

    public final void setKubeConfig(File kubeConfig) {
        Objects.requireNonNull(kubeConfig, "kubeConfig must not be null");
        this.kubeConfig = kubeConfig;
    }

    public final Optional<Integer> getTillerConnectionTimeout() {
        return tillerConnectionTimeout != 0 ? Optional.of(this.tillerConnectionTimeout) : Optional.empty();
    }

    /**
     * Sets the duration in seconds Helm will wait to establish a connection to tiller.
     *
     * @param timeout
     */
    public final void setTillerConnectionTimeout(int timeout) {
        this.tillerConnectionTimeout = timeout;
    }

    public final File getCurrentDirectory() {
        if (this.currentDirectory == null) {
            try {
                this.currentDirectory = new File(".").getCanonicalFile();
            } catch (IOException ex) {
                throw new UncheckedIOException("Failed to retrieve current directory!", ex);
            }
        }
        return this.currentDirectory;
    }

    public final void setCurrentDirectory(File currentDirectory) {
        Objects.requireNonNull(currentDirectory, "currentDirectory must not be null");
        this.currentDirectory = currentDirectory;
    }

    public final Optional<String> getTillerNamespace() {
        return Optional.ofNullable(this.tillerNamespace);
    }

    public final void setTillerNamespace(String tillerNamespace) {
        Objects.requireNonNull(currentDirectory, "tillerNamespace must not be null");
        this.tillerNamespace = tillerNamespace;
    }

    @Override
    public V call() throws Exception {
        return internalCall();
    }

    protected abstract V internalCall() throws Exception;

    protected void collectCommandLineArguments(List<String> arguments) {
        if (isDebug()) {
            arguments.add("--debug");
        }
        getHome().ifPresent(home -> {
            arguments.add("--home");
            arguments.add(home.getAbsolutePath());
        });
        getHost().ifPresent(host -> {
            arguments.add("--host");
            arguments.add(host);
        });
        getKubeContext().ifPresent(context -> {
            arguments.add("--kube-context");
            arguments.add(context);
        });
        getKubeConfig().ifPresent(config -> {
            arguments.add("--kubeconfig");
            arguments.add(config.getAbsolutePath());
        });
        getTillerConnectionTimeout().ifPresent(timeout -> {
            arguments.add("--tiller-connection-timeout");
            arguments.add(Integer.toString(timeout));
        });
        getTillerNamespace().ifPresent(namespace -> {
            arguments.add("--tiller-namespace");
            arguments.add(namespace);
        });
    }
}
