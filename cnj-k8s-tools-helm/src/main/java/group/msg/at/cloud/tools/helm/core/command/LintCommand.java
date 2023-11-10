package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents the {@code helm install} command which installs the specified chart as a release.
 */
public final class LintCommand extends AbstractChartCommand<LintCommandResult> {

    public LintCommand() {
        super();
    }

    public LintCommand(Logger logger) {
        super(logger);
    }

    protected void collectCommandLineArguments(List<String> arguments) {
        arguments.add(getChartDirectory().getAbsolutePath());
        super.collectCommandLineArguments(arguments);
    }

    @Override
    public LintCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        ResultParser parsingConsumer = new ResultParser();
        Consumer<String> compositeConsumer = loggingConsumer.andThen(parsingConsumer);
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("lint");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", arguments));
        runner.run(getCurrentDirectory(), compositeConsumer, arguments.toArray(new String[0]));
        return parsingConsumer.parse();
    }

    private static final class ResultParser implements Consumer<String> {

        private CommandStatusCode statusCode;
        private final List<String> statusMessageParts = new ArrayList<>();

        public LintCommandResult parse() {
            LintCommandResult result = new LintCommandResult();
            result.setStatusCode(statusCode);
            result.setStatusMessage(String.join(" ", statusMessageParts));
            return result;
        }

        @Override
        public void accept(String s) {
            if (s != null && s.contains("chart(s) failed")) {
                if (s.endsWith("0 chart(s) failed")) {
                    statusCode = CommandStatusCode.SUCCESS;
                } else {
                    statusCode = CommandStatusCode.FAILURE;
                }
                statusMessageParts.add(s);
            }
        }
    }

}
