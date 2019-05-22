package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.command.*;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class UpgradeCommandTest {

    private String currentReleaseName;

    @After
    public void onAfter() {
        if (currentReleaseName != null) {
            DeleteCommand delete = new DeleteCommand();
            delete.setPurge(true);
            delete.setReleaseName(currentReleaseName);
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
        InstallCommand preTestInstall = new InstallCommand();
        preTestInstall.setAtomic(true);
        preTestInstall.setDescription("test release of chart testok");
        // preTestInstall.setDebug(true);
        preTestInstall.setNamespace("default");
        currentReleaseName = "testok";
        preTestInstall.setReleaseName("testok");
        preTestInstall.setWait(true);
        preTestInstall.setChartDirectory(new File("src/test/helm/testok"));
        InstallCommandResult preTestInstallResult = preTestInstall.call();

        UpgradeCommand underTest = new UpgradeCommand();
        underTest.setAtomic(true);
        underTest.setDescription("updated test release of chart testok");
        //underTest.setDebug(true);
        underTest.setNamespace("default");
        underTest.setReleaseName("testok");
        underTest.setWait(true);
        underTest.setChartDirectory(new File("src/test/helm/testok"));
        UpgradeCommandResult result = underTest.call();
        assertNotNull("command must return non-null result", result);
        assertEquals("command status code must be SUCCESS", CommandStatusCode.SUCCESS, result.getStatusCode());
    }

    @Test
    public void upgradeWithoutPreviousInstallFails() throws Exception {

        UpgradeCommand underTest = new UpgradeCommand();
        underTest.setAtomic(true);
        underTest.setDescription("updated test release of chart testok");
        underTest.setNamespace("default");
        underTest.setReleaseName("testok");
        underTest.setWait(true);
        underTest.setChartDirectory(new File("src/test/helm/testok"));
        UpgradeCommandResult result = underTest.call();
        assertNotNull("command must return non-null result", result);
        assertEquals("command status code must be FAILURE", CommandStatusCode.FAILURE, result.getStatusCode());
    }
}
