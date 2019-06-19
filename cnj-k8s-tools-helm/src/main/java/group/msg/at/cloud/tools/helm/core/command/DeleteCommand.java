package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents a {@code helm delete} command which deletes the given release.
 */
public final class DeleteCommand extends AbstractCommand<DeleteCommandResult> {

    private String description;
    private boolean dryRun;
    private String releaseName;
    private boolean purge;
    private int timeout;
    /*
          --tls                      enable TLS for request
          --tls-ca-cert string       path to TLS CA certificate file (default "$HELM_HOME/ca.pem")
          --tls-cert string          path to TLS certificate file (default "$HELM_HOME/cert.pem")
          --tls-hostname string      the server name used to verify the hostname on the returned certificates from the server
          --tls-key string           path to TLS key file (default "$HELM_HOME/key.pem")
          --tls-verify               enable TLS for request and verify remote

     */
    private boolean wait;

    public DeleteCommand() {
        super();
    }

    public DeleteCommand(Logger logger) {
        super(logger);
    }

    /**
     * If set, remove the release from the store and make its name free for later use.
     */
    public boolean isPurge() {
        return this.purge;
    }

    public void setPurge(boolean purge) {
        this.purge = purge;
    }

    /**
     * Optional description of this release.
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    public void setDescription(String description) {
        this.description = description;
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
     * Required release name
     */
    public String getReleaseName() {
        return this.releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    /**
     * Optional duration in seconds helm will wait for any individual Kubernetes operation (like Jobs for hooks) (default: 300)
     */
    public final Optional<Integer> getTimeout() {
        return timeout != 0 ? Optional.of(this.timeout) : Optional.empty();
    }

    /**
     * Sets the duration in seconds Helm will wait to establish a connection to tiller.
     */
    public final void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected void collectCommandLineArguments(List<String> arguments) {
        super.collectCommandLineArguments(arguments);
        if (isPurge()) {
            arguments.add("--purge");
        }
        getDescription().ifPresent(description -> {
            arguments.add("--description");
            arguments.add("\"" + description + "\"");
        });
        if (isDryRun()) {
            arguments.add("--dry-run");
        }
        getTimeout().ifPresent(timeout -> {
            arguments.add("--timeout");
            arguments.add(Integer.toString(timeout));
        });
        arguments.add(getReleaseName());
    }

    @Override
    public DeleteCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        Consumer<String> consumer = loggingConsumer;
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("delete");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", arguments));
        runner.run(getCurrentDirectory(), consumer, arguments.toArray(new String[0]));
        return new DeleteCommandResult();
    }
}
