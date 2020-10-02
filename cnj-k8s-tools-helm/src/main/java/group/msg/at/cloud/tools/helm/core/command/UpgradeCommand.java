package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents the {@code helm upgrade} command which update the specified release with the specified chart.
 */
public final class UpgradeCommand extends AbstractChartCommand<UpgradeCommandResult> {

    private boolean force;
    private boolean install;
    private boolean cleanupOnFail;
    private String releaseName;
    private boolean resetValues;
    private boolean reuseValues;
    private Map<String, String> values = new LinkedHashMap<>();

    public UpgradeCommand() {
        super();
    }

    public UpgradeCommand(Logger logger) {
        super(logger);
    }

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

    /**
     * If set, allow deletion of new resources created in this upgrade when upgrade fails.
     */
    public boolean isCleanupOnFail() {
        return cleanupOnFail;
    }

    public void setCleanupOnFail(boolean cleanupOnFail) {
        this.cleanupOnFail = cleanupOnFail;
    }

    /**
     * Required release name of an already installed release
     */
    public String getReleaseName() {
        return this.releaseName;
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    /**
     * When upgrading, reset the values to the ones built into the chart.
     */
    public boolean isResetValues() {
        return resetValues;
    }

    public void setResetValues(boolean resetValues) {
        this.resetValues = resetValues;
    }

    /**
     * When upgrading, reuse the last release's values and merge in any overrides from the command line via --set and -f.
     * <p>
     * If {@link #resetValues} is specified, this is ignored.
     * </p>
     */
    public boolean isReuseValues() {
        return reuseValues;
    }

    public void setReuseValues(boolean reuseValues) {
        this.reuseValues = reuseValues;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
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
        if (isCleanupOnFail()) {
            arguments.add("--cleanup-on-fail");
        }
        if (isResetValues()) {
            arguments.add("--reset-values");
        }
        if (isReuseValues()) {
            arguments.add("--reuse-values");
        }
        if (!values.isEmpty()) {
            arguments.add("--set");
            arguments.add(String.join(",", values.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList())));
        }
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
        // TODO: put after collectCommandLineArguments when filter of set argument is implemented
        this.logger.info("running command: " + String.join(" ", arguments));
        collectCommandLineArguments(arguments);
        runner.run(getCurrentDirectory(), compositeConsumer, arguments.toArray(new String[0]));
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
                } else if (s.startsWith("STATUS: deployed")) {
                    statusCode = CommandStatusCode.SUCCESS;
                    statusMessageParts.add(s);
                } else if (s.contains("UPGRADE FAILED")) {
                    statusCode = CommandStatusCode.FAILURE;
                    statusMessageParts.add(s);
                } else if (s.startsWith("ROLLING BACK")) {
                    statusCode = CommandStatusCode.FAILURE;
                    statusMessageParts.add(s);
                } else if (s.startsWith("Error:")) {
                    statusCode = CommandStatusCode.FAILURE;
                    statusMessageParts.add(s);
                }
            }
        }
    }
}
