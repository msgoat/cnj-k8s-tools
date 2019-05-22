package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents the {@code helm upgrade} command which update the specified release with the specified chart.
 */
public final class UpgradeCommand extends AbstractChartCommand<UpgradeCommandResult> {

    private String namespace;
    private boolean force;

    public UpgradeCommand() {
        super();
    }

    public UpgradeCommand(Logger logger) {
        super(logger);
    }

    /**
     * Optional namespace to install to (default: current kubeconfig namespace).
     */
    public Optional<String> getNamespace() {
        return Optional.ofNullable(this.namespace);
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    private boolean install;

    /**
     * If a release by this name doesn't already exist, run an install.
     */
    public boolean isInstall() {
        return install;
    }

    public void setInstall(boolean install) {
        this.install = install;
    }

    /**
     * If set, force resource update through delete/recreate if needed.
     */
    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    private String releaseName;

    /**
     * Required release name of an already installed release
     */
    public String getReleaseName() {
        return this.releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }


    protected void collectCommandLineValues(List<String> arguments) {
/*
        StringBuilder regularValuesBuilder = new StringBuilder();
        StringBuilder stringValuesBuilder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> current : values.entrySet()) {
            if (!first) {

            }
        }
 */
    }

    protected void collectCommandLineArguments(List<String> arguments) {
        arguments.add(getReleaseName());
        arguments.add(getChartDirectory().getAbsolutePath());
        super.collectCommandLineArguments(arguments);
        if (isForce()) {
            arguments.add("--force");
        }
        if (isInstall()) {
            arguments.add("--install");
        }
        getNamespace().ifPresent(namespace -> {
            arguments.add("--namespace");
            arguments.add(namespace);
        });
    }

    @Override
    public UpgradeCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        ResultParser parsingConsumer = new ResultParser();
        Consumer<String> compositeConsumer = loggingConsumer.andThen(parsingConsumer);
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("upgrade");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", arguments));
        runner.run(getCurrentDirectory(), compositeConsumer, arguments.toArray(new String[arguments.size()]));
        return parsingConsumer.parse();
    }

    private static final class ResultParser implements Consumer<String> {

        private CommandStatusCode statusCode;
        private List<String> statusMessageParts = new ArrayList<>();

        public UpgradeCommandResult parse() {
            UpgradeCommandResult result = new UpgradeCommandResult();
            result.setStatusCode(statusCode);
            result.setStatusMessage(String.join(" ", statusMessageParts));
            return result;
        }

        @Override
        public void accept(String s) {
            if (s != null) {
                if (s.startsWith("Release") && s.contains("has been upgraded")) {
                    statusCode = CommandStatusCode.SUCCESS;
                    statusMessageParts.add(s);
                } else if (s.startsWith("UPGRADE FAILED")) {
                    statusCode = CommandStatusCode.FAILURE;
                    statusMessageParts.add(s);
                } else if (s.startsWith("ROLLING BACK")) {
                    statusMessageParts.add(s);
                } else if (s.startsWith("Error:")) {
                    statusMessageParts.add(s);
                }
            }
        }
    }
}
