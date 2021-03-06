package group.msg.at.cloud.tools.helm.core.command;

import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UpgradeCommandTest {

    private String currentReleaseName;

    @After
    public void onAfter() {
        if (currentReleaseName != null) {
            UninstallCommand delete = new UninstallCommand();
            delete.setReleaseName(currentReleaseName);
            delete.setNamespace(Constants.RELEASE_NAMESPACE);
            try {
                delete.call();
            } catch (Exception ex) {
                System.err.println(String.format("unable to delete release %s: %s", currentReleaseName, ex.getMessage()));
            }
        }
        currentReleaseName = null;
    }

    @Test
    public void upgradeAfterInstallWorksOk() throws Exception {
        final String RELEASE_NAME = "upgradewithprevrelease";
        InstallCommand preTestInstall = new InstallCommand();
        preTestInstall.setAtomic(true);
        preTestInstall.setNamespace("default");
        currentReleaseName = RELEASE_NAME;
        preTestInstall.setReleaseName(RELEASE_NAME);
        preTestInstall.setWait(true);
        preTestInstall.setChartDirectory(new File("src/test/helm/testok"));
        preTestInstall.setNamespace(Constants.RELEASE_NAMESPACE);
        InstallCommandResult preTestInstallResult = preTestInstall.call();

        UpgradeCommand underTest = new UpgradeCommand();
        underTest.setAtomic(true);
        underTest.setNamespace("default");
        underTest.setReleaseName(RELEASE_NAME);
        underTest.setWait(true);
        underTest.setChartDirectory(new File("src/test/helm/testok"));
        underTest.setNamespace(Constants.RELEASE_NAMESPACE);
        UpgradeCommandResult result = underTest.call();
        assertNotNull("command must return non-null result", result);
        assertEquals("command status code must be SUCCESS", CommandStatusCode.SUCCESS, result.getStatusCode());
    }

    @Test
    public void upgradeWithoutPreviousInstallFails() throws Exception {
        final String RELEASE_NAME = "upgradenoprevrelease";
        UpgradeCommand underTest = new UpgradeCommand();
        underTest.setAtomic(true);
        underTest.setNamespace("default");
        currentReleaseName = RELEASE_NAME;
        underTest.setReleaseName(RELEASE_NAME);
        underTest.setWait(true);
        underTest.setChartDirectory(new File("src/test/helm/testok"));
        underTest.setNamespace(Constants.RELEASE_NAMESPACE);
        UpgradeCommandResult result = underTest.call();
        assertNotNull("command must return non-null result", result);
        assertEquals("command status code must be FAILURE", CommandStatusCode.FAILURE, result.getStatusCode());
    }
}
