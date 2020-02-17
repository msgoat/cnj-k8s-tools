package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents the {@code helm dependency update} command which updates all dependencies of the specified chart.
 */
public final class UpdateDependenciesCommand extends AbstractChartCommand<UpdateDependenciesCommandResult> {

    public UpdateDependenciesCommand() {
        super();
    }

    public UpdateDependenciesCommand(Logger logger) {
        super(logger);
    }

    protected void collectCommandLineArguments(List<String> arguments) {
        arguments.add(getChartDirectory().getAbsolutePath());
        super.collectCommandLineArguments(arguments);
    }

    @Override
    public UpdateDependenciesCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        ResultParser parsingConsumer = new ResultParser();
        Consumer<String> compositeConsumer = loggingConsumer.andThen(parsingConsumer);
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("dependency");
        arguments.add("update");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", arguments));
        runner.run(getCurrentDirectory(), compositeConsumer, arguments.toArray(new String[0]));
        return parsingConsumer.parse();
    }

    private static final class ResultParser implements Consumer<String> {

        private CommandStatusCode statusCode;
        private List<String> statusMessageParts = new ArrayList<>();

        public UpdateDependenciesCommandResult parse() {
            UpdateDependenciesCommandResult result = new UpdateDependenciesCommandResult();
            result.setStatusCode(statusCode);
            result.setStatusMessage(String.join(" ", statusMessageParts));
            return result;
        }

        @Override
        public void accept(String s) {
            if (s != null) {
                if (s.startsWith("Update Complete")) {
                    statusCode = CommandStatusCode.SUCCESS;
                    statusMessageParts.add(s);
                } else if (s.startsWith("Error:")) {
                    statusCode = CommandStatusCode.FAILURE;
                    statusMessageParts.add(s);
                }
            }
        }
    }

}
