package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents the {@code helm push} command which uploads the given packaged chart to the given registry.
 */
public final class PushCommand extends AbstractChartCommand<PushCommandResult> {

    private String chartRegistry;

    private String chartRepository;

    public PushCommand() {
        super();
    }

    public PushCommand(Logger logger) {
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

    protected void collectCommandLineArguments(List<String> arguments) {
        arguments.add(getChartPackage().getAbsolutePath());
        arguments.add(buildRepositoryUrl());
        super.collectCommandLineArguments(arguments);
    }

    @Override
    public PushCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        ResultParser parsingConsumer = new ResultParser();
        Consumer<String> compositeConsumer = loggingConsumer.andThen(parsingConsumer);
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("push");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", arguments));
        runner.run(getCurrentDirectory(), compositeConsumer, arguments.toArray(new String[0]));
        return parsingConsumer.parse();
    }

    private static final class ResultParser implements Consumer<String> {

        private CommandStatusCode statusCode;
        private final List<String> statusMessageParts = new ArrayList<>();

        public PushCommandResult parse() {
            PushCommandResult result = new PushCommandResult();
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
        String repositoryPath = removeChartNameFromRepositoryPath(chartRepository);
        StringBuilder result = new StringBuilder();
        result.append(schema);
        result.append(chartRegistry);
        if (!repositoryPath.startsWith("/")) {
            result.append("/");
        }
        result.append(repositoryPath);
        return result.toString();
    }

    private String removeChartNameFromRepositoryPath(String repositoryPath) {
        String[] repositoryPathComponents = repositoryPath.split("/");
        String lastRepositoryPathComponent;
        if (repositoryPathComponents.length - 1 >= 0) {
            lastRepositoryPathComponent = repositoryPathComponents[repositoryPathComponents.length - 1];
            if (getChartPackage().getName().startsWith(lastRepositoryPathComponent)) {
                repositoryPathComponents = Arrays.copyOf(repositoryPathComponents, repositoryPathComponents.length - 1);
            }
        } else {
            lastRepositoryPathComponent = repositoryPathComponents[0];
            if (getChartPackage().getName().startsWith(lastRepositoryPathComponent)) {
                repositoryPathComponents = new String[0];
            }
        }
        return String.join("/", repositoryPathComponents);
    }
}
