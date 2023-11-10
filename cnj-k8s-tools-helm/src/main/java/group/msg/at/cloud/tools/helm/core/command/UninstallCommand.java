package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents a {@code helm uninstall} command which deletes the given release.
 */
public final class UninstallCommand extends AbstractCommand<UninstallCommandResult> {

    private boolean dryRun;
    private String releaseName;
    private boolean keepHistory;
    private int timeout;
    private boolean wait;

    public UninstallCommand() {
        super();
    }

    public UninstallCommand(Logger logger) {
        super(logger);
    }

    /**
     * If set, remove the release but keep its history
     */
    public boolean isKeepHistory() {
        return this.keepHistory;
    }

    public void setKeepHistory(boolean keep) {
        this.keepHistory = keep;
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
    public Optional<Integer> getTimeout() {
        return timeout != 0 ? Optional.of(this.timeout) : Optional.empty();
    }

    /**
     * Sets the duration in seconds Helm will wait to establish a connection to tiller.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected void collectCommandLineArguments(List<String> arguments) {
        arguments.add(getReleaseName());
        super.collectCommandLineArguments(arguments);
        if (isKeepHistory()) {
            arguments.add("--keep-history");
        }
        if (isDryRun()) {
            arguments.add("--dry-run");
        }
        getTimeout().ifPresent(timeout -> {
            arguments.add("--timeout");
            arguments.add(Integer.toString(timeout));
        });
    }

    @Override
    public UninstallCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        Consumer<String> consumer = loggingConsumer;
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("uninstall");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", arguments));
        runner.run(getCurrentDirectory(), consumer, arguments.toArray(new String[0]));
        return new UninstallCommandResult();
    }
}
