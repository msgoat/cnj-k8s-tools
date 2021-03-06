package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import group.msg.at.cloud.tools.helm.core.model.Release;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents the {@code helm ls} command which lists all installed releases.
 */
public final class ListCommand extends AbstractCommand<ListCommandResult> {

    private String tillerNamespace;

    public ListCommand() {
        super();
    }

    public ListCommand(Logger logger) {
        super(logger);
    }

    @Override
    protected ListCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        ReleaseParser parsingConsumer = new ReleaseParser();
        Consumer<String> consumer = loggingConsumer.andThen(parsingConsumer);
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("list");
        arguments.add("--short");
        if (getNamespace().isPresent()) {
            arguments.add("--namespace");
            arguments.add(getNamespace().get());
        } else {
            arguments.add("--all-namespaces");
        }
        collectCommandLineArguments(arguments);
        runner.run(getCurrentDirectory(), consumer, arguments.toArray(new String[0]));
        return new ListCommandResult(parsingConsumer.getReleases());
    }

    private static final class ReleaseParser implements Consumer<String> {

        private final List<Release> releases = new ArrayList<>();

        public List<Release> getReleases() {
            return releases;
        }

        @Override
        public void accept(String s) {
            if (s != null) {
                if (!s.startsWith("NAME")) {
                    Release release = parse(s);
                    if (release != null) {
                        releases.add(release);
                    }
                }
            }
        }

        private Release parse(String s) {
            Release result = new Release(s);
            return result;
        }
    }
}
