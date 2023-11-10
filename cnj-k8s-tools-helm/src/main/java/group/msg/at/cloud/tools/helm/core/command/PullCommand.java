package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents the {@code helm pull} command which downloads the given chart from the given registry.
 */
public final class PullCommand extends AbstractChartCommand<PullCommandResult> {

    private String chartRegistry;

    private String chartRepository;

    private String chartName;

    public PullCommand() {
        super();
    }

    public PullCommand(Logger logger) {
        super(logger);
    }

    public String getChartRegistry() {
        return chartRegistry;
    }

    public void setChartRegistry(String chartRegistry) {
        this.chartRegistry = chartRegistry;
    }

    public String getChartRepository() {
        return chartRepository;
    }

    public void setChartRepository(String chartRepository) {
        this.chartRepository = chartRepository;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public String getChartName() {
        return chartName;
    }

    protected void collectCommandLineArguments(List<String> arguments) {
        arguments.add(buildRepositoryUrl());
        super.collectCommandLineArguments(arguments);
        arguments.add("--destination");
        arguments.add(getChartPackageDirectory().getAbsolutePath());
    }

    @Override
    public PullCommandResult internalCall() throws Exception {
        ensureDestination();
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        ResultParser parsingConsumer = new ResultParser();
        Consumer<String> compositeConsumer = loggingConsumer.andThen(parsingConsumer);
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("pull");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", arguments));
        runner.run(getCurrentDirectory(), compositeConsumer, arguments.toArray(new String[0]));
        return parsingConsumer.parse();
    }

    private static final class ResultParser implements Consumer<String> {

        private CommandStatusCode statusCode;
        private final List<String> statusMessageParts = new ArrayList<>();

        public PullCommandResult parse() {
            PullCommandResult result = new PullCommandResult();
            result.setStatusCode(statusCode);
            result.setStatusMessage(String.join(" ", statusMessageParts));
            return result;
        }

        @Override
        public void accept(String s) {
            if (s != null) {
                if (s.startsWith("Successfully packaged chart")) {
                    statusCode = CommandStatusCode.SUCCESS;
                    statusMessageParts.add(s);
                } else if (s.startsWith("Error:")) {
                    statusCode = CommandStatusCode.FAILURE;
                    statusMessageParts.add(s);
                }
            }
        }
    }

    private String buildRepositoryUrl() {
        String schema = "oci://";
        StringBuilder result = new StringBuilder();
        result.append(schema);
        result.append(chartRegistry);
        if (!chartRepository.startsWith("/")) {
            result.append("/");
        }
        result.append(chartRepository);
        if (!chartRepository.endsWith("/")) {
            result.append("/");
        }
        result.append(chartName);
        return result.toString();
    }

    private void ensureDestination() {
        if (!getChartPackageDirectory().exists()) {
            getChartPackageDirectory().mkdirs();
        }
    }
}
