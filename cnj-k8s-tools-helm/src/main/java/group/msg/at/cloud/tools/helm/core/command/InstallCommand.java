package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.ExecutableRunner;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents the {@code helm install} command which installs the specified chart as a release.
 */
public final class InstallCommand extends AbstractChartCommand<InstallCommandResult> {

    private String releaseName;
    private String releaseNameTemplate;
    private String namespace;
    private boolean noCrdHook;
    private boolean replace;
    /*
          --set stringArray          set values on the comma>d line (can specify multiple or separate values with commas: key1=val1,key2=val2)
          --set-file stringArray     set values from respective files specified via the command line (can specify multiple or separate values with commas: key1=path1,key2=path2)
          --set-string stringArray   set STRING values on the command line (can specify multiple or separate values with commas: key1=val1,key2=val2)
     */
    private int timeout;
    /*
          --tls                      enable TLS for request
          --tls-ca-cert string       path to TLS CA certificate file (default "$HELM_HOME/ca.pem")
          --tls-cert string          path to TLS certificate file (default "$HELM_HOME/cert.pem")
          --tls-hostname string      the server name used to verify the hostname on the returned certificates from the server
          --tls-key string           path to TLS key file (default "$HELM_HOME/key.pem")
          --tls-verify               enable TLS for request and verify remote

     */
/*
  -f, --values valueFiles        specify values in a YAML file or a URL(can specify multiple) (default [])

 */

    public InstallCommand() {
        super();
    }

    public InstallCommand(Logger logger) {
        super(logger);
    }

    /**
     * Optional custom release name (default: auto-generated release name)
     */
    public Optional<String> getReleaseName() {
        return Optional.ofNullable(this.releaseName);
    }

    public void setReleaseName(String releaseName) {
        this.releaseName = releaseName;
    }

    /**
     * Optional release name template.
     */
    public Optional<String> getReleaseNameTemplate() {
        return Optional.ofNullable(this.releaseNameTemplate);
    }

    public void setReleaseNameTemplate(String releaseNameTemplate) {
        this.releaseNameTemplate = releaseNameTemplate;
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

    /**
     * If set, prevent CRD hooks from running, but run other hooks.
     */
    public boolean isNoCrdHook() {
        return noCrdHook;
    }

    public void setNoCrdHook(boolean noCrdHook) {
        this.noCrdHook = noCrdHook;
    }

    /**
     * If set, re-use the given release name, even if that release name is already used (default: {@code false}).
     * <p>
     * <strong>Unsafe in production!!!</strong>
     * </p>
     */
    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
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
        arguments.add(getChartDirectory().getAbsolutePath());
        super.collectCommandLineArguments(arguments);
        getReleaseName().ifPresent(name -> {
            arguments.add("--name");
            arguments.add(name);
        });
        getReleaseNameTemplate().ifPresent(template -> {
            arguments.add("--name-template");
            arguments.add(template);
        });
        getNamespace().ifPresent(namespace -> {
            arguments.add("--namespace");
            arguments.add(namespace);
        });
        if (isNoCrdHook()) {
            arguments.add("--no-crd-hook");
        }
        if (isReplace()) {
            arguments.add("--replace");
        }
    }

    @Override
    public InstallCommandResult internalCall() throws Exception {
        ExecutableRunner runner = new ExecutableRunner();
        Consumer<String> loggingConsumer = s -> this.logger.info(s);
        ResultParser parsingConsumer = new ResultParser();
        Consumer<String> compositeConsumer = loggingConsumer.andThen(parsingConsumer);
        List<String> arguments = new ArrayList<>();
        arguments.add("helm");
        arguments.add("install");
        collectCommandLineArguments(arguments);
        this.logger.info("running command: " + String.join(" ", arguments));
        runner.run(getCurrentDirectory(), compositeConsumer, arguments.toArray(new String[arguments.size()]));
        return parsingConsumer.parse();
    }

    private static final class ResultParser implements Consumer<String> {

        private CommandStatusCode statusCode;
        private List<String> statusMessageParts = new ArrayList<>();

        public InstallCommandResult parse() {
            InstallCommandResult result = new InstallCommandResult();
            result.setStatusCode(statusCode);
            result.setStatusMessage(String.join(" ", statusMessageParts));
            return result;
        }

        @Override
        public void accept(String s) {
            if (s != null) {
                if (s.startsWith("STATUS: DEPLOYED")) {
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
