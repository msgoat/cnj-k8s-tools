package group.msg.at.cloud.tools.helm.core.command;

import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;

/**
 * Composite command which uses either {@code helm install} if the given release does not exit or {@code helm upgrade}
 * if the given release exists.
 */
public final class DeployCommand extends AbstractChartCommand<DeployCommandResult> {

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

    public DeployCommand() {
        super();
    }

    public DeployCommand(Logger logger) {
        super(logger);
    }

    /**
     * Required release name
     */
    public String getReleaseName() {
        return this.releaseName;
    }

    public void setReleaseName(String releaseName) {
        Objects.requireNonNull(releaseName, "releaseName must not be null!");
        this.releaseName = releaseName;
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


    @Override
    public DeployCommandResult internalCall() throws Exception {
        ListCommand list = new ListCommand(this.logger);
        getTillerNamespace().ifPresent(ns -> list.setTillerNamespace(ns));
        this.logger.info(String.format("checking if release [%s] exists...", getReleaseName()));
        ListCommandResult listResult = list.call();

        AbstractCommandResult resultPrototype;
        if (listResult.containsRelease(getReleaseName())) {
            this.logger.info(String.format("release [%s] exists: running helm upgrade", getReleaseName()));
            UpgradeCommand upgrade = createUpgradeCommand();
            UpgradeCommandResult upgradeResult = upgrade.call();
            resultPrototype = upgradeResult;
        } else {
            this.logger.info(String.format("release [%s] does not exist: running helm install", getReleaseName()));
            InstallCommand install = createInstallCommand();
            InstallCommandResult installResult = install.call();
            resultPrototype = installResult;
        }

        DeployCommandResult result = new DeployCommandResult();
        result.setStatusCode(resultPrototype.getStatusCode());
        result.setStatusMessage(resultPrototype.getStatusMessage());
        return result;
    }

    private InstallCommand createInstallCommand() {
        InstallCommand result = new InstallCommand(this.logger);
        result.setAtomic(isAtomic());
        result.setCurrentDirectory(getCurrentDirectory());
        result.setChartDirectory(getChartDirectory());
        result.setDebug(isDebug());
        getNamespace().ifPresent(ns -> result.setNamespace(ns));
        result.setNoCrdHook(isNoCrdHook());
        result.setReleaseName(getReleaseName());
        getTillerNamespace().ifPresent(ns -> result.setTillerNamespace(ns));
        getTillerConnectionTimeout().ifPresent(ct -> result.setTillerConnectionTimeout(ct));
        result.setWait(isWait());
        return result;
    }

    private UpgradeCommand createUpgradeCommand() {
        UpgradeCommand result = new UpgradeCommand(this.logger);
        result.setAtomic(isAtomic());
        result.setCurrentDirectory(getCurrentDirectory());
        result.setChartDirectory(getChartDirectory());
        result.setDebug(isDebug());
        result.setReleaseName(getReleaseName());
        getTillerNamespace().ifPresent(ns -> result.setTillerNamespace(ns));
        getTillerConnectionTimeout().ifPresent(ct -> result.setTillerConnectionTimeout(ct));
        result.setWait(isWait());
        return result;
    }

}
