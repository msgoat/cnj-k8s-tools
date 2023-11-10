package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Represents the {@code helm package} command which installs the specified chart as a release.
 */
public final class PackageCommand extends AbstractChartCommand<PackageCommandResult> {

    private String chartAppVersion;

    private File chartPackageDirectory;

    public PackageCommand() {
        super();
    }

    public PackageCommand(Logger logger) {
        super(logger);
    }

    public Optional<String> getChartAppVersion() {
        return Optional.ofNullable(this.chartAppVersion);
    }

    public void setChartAppVersion(String appVersion) {
        this.chartAppVersion = appVersion;
    }

    public File getChartPackageDirectory() {
        return chartPackageDirectory;
    }

    public void setChartPackageDirectory(File chartPackageDirectory) {
        this.chartPackageDirectory = chartPackageDirectory;
    }

    protected void collectCommandLineArguments(List<String> arguments) {
        arguments.add(getChartDirectory().getAbsolutePath());
        super.collectCommandLineArguments(arguments);
        arguments.add("--destination");
        arguments.add(chartPackageDirectory.getAbsolutePath());
        getChartAppVersion().ifPresent(version -> {
            arguments.add("--app-version");
            arguments.add(version);
        });
    }

    @Override
    public PackageCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        ResultParser parsingConsumer = new ResultParser();
        Consumer<String> compositeConsumer = loggingConsumer.andThen(parsingConsumer);
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("package");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", arguments));
        runner.run(getCurrentDirectory(), compositeConsumer, arguments.toArray(new String[0]));
        return parsingConsumer.parse();
    }

    private static final class ResultParser implements Consumer<String> {

        private CommandStatusCode statusCode;
        private final List<String> statusMessageParts = new ArrayList<>();

        public PackageCommandResult parse() {
            PackageCommandResult result = new PackageCommandResult();
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

}
