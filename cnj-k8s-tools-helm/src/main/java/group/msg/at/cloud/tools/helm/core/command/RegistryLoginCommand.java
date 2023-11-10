package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents the {@code helm package} command which installs the specified chart as a release.
 */
public final class RegistryLoginCommand extends AbstractCommand<RegistryLoginCommandResult> {

    private static final Set<String> SENSITIVE_ARGUMENT_NAMES = Set.of("--password");

    private String chartRegistry;

    private String username;

    private String password;

    public RegistryLoginCommand() {
        super();
    }

    public RegistryLoginCommand(Logger logger) {
        super(logger);
    }

    public String getChartRegistry() {
        return chartRegistry;
    }

    public void setChartRegistry(String chartRegistry) {
        this.chartRegistry = chartRegistry;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected void collectCommandLineArguments(List<String> arguments) {
        arguments.add(chartRegistry);
        super.collectCommandLineArguments(arguments);
        arguments.add("--username");
        arguments.add(getUsername());
        arguments.add("--password");
        arguments.add(getPassword());
    }

    @Override
    public RegistryLoginCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        ResultParser parsingConsumer = new ResultParser();
        Consumer<String> compositeConsumer = loggingConsumer.andThen(parsingConsumer);
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("registry");
        arguments.add("login");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", filterSensitiveArguments(arguments)));
        runner.run(getCurrentDirectory(), compositeConsumer, arguments.toArray(new String[0]));
        return parsingConsumer.parse();
    }

    private static final class ResultParser implements Consumer<String> {

        private CommandStatusCode statusCode;
        private final List<String> statusMessageParts = new ArrayList<>();

        public RegistryLoginCommandResult parse() {
            RegistryLoginCommandResult result = new RegistryLoginCommandResult();
            result.setStatusCode(statusCode);
            result.setStatusMessage(String.join(" ", statusMessageParts));
            return result;
        }

        @Override
        public void accept(String s) {
            statusCode = CommandStatusCode.FAILURE;
            if (s != null) {
                if (s.startsWith("Login Succeeded")) {
                    statusCode = CommandStatusCode.SUCCESS;
                    statusMessageParts.add(s);
                }
            }
        }
    }
    private List<String> filterSensitiveArguments(List<String> arguments) {
        List<String> result = new ArrayList<>();
        Iterator<String> argItr = arguments.iterator();
        while (argItr.hasNext()) {
            String currentArg = argItr.next();
            result.add(currentArg);
            if (SENSITIVE_ARGUMENT_NAMES.contains(currentArg) && argItr.hasNext()) {
                String nextArg = argItr.next();
                result.add("__redacted(" + nextArg.length() + ")__");
            }
        }
        return result;
    }
}
