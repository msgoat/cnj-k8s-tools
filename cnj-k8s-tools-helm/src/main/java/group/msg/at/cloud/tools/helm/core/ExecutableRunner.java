package group.msg.at.cloud.tools.helm.core;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ExecutableRunner {

    public static final int INTERRUPTED_EXIT_CODE = -666;

    public int run(File currentDirectory, Consumer<String> outputProcessor, String... command) {
        Process process = null;
        try {
            process = new ProcessBuilder().command(command).directory(currentDirectory).redirectErrorStream(true).start();
        } catch (IOException ex) {
            throw new UncheckedIOException(String.format("I/O Error while executing command [%s] in directory [%s]", command[0], currentDirectory.getAbsolutePath()), ex);
        }
        Executor backgroundProcessor = Executors.newSingleThreadExecutor();
        backgroundProcessor.execute(new StreamGobbler(process.getInputStream(), outputProcessor));
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException ex) {
            exitCode = INTERRUPTED_EXIT_CODE;
        }
        return exitCode;
    }
}
