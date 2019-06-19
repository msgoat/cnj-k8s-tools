package group.msg.at.cloud.tools.helm.core.command;

import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DeployCommandTest {

    private String currentReleaseName;

    @After
    public void onAfter() {
        if (currentReleaseName != null) {
            DeleteCommand delete = new DeleteCommand();
            delete.setPurge(true);
            delete.setReleaseName(currentReleaseName);
            delete.setTillerNamespace(Constants.TILLER_NAMESPACE);
            try {
                delete.call();
            } catch (Exception ex) {
                System.err.println(String.format("unable to delete release %s: %s", currentReleaseName, ex.getMessage()));
            }
        }
        currentReleaseName = null;
    }

    @Test
    public void deployCommandWithoutPreviousReleaseSucceeds() throws Exception {
        final String RELEASE_NAME = "deploynoprevrelease";
        DeployCommand underTest = new DeployCommand();
        underTest.setAtomic(true);
        underTest.setDescription("test release of chart testok");
        // underTest.setDryRun(true);
        underTest.setDebug(true);
        underTest.setNamespace("default");
        currentReleaseName = RELEASE_NAME;
        underTest.setReleaseName(RELEASE_NAME);
        underTest.setWait(true);
        underTest.setChartDirectory(new File("src/test/helm/testok"));
        underTest.setTillerNamespace(Constants.TILLER_NAMESPACE);
        DeployCommandResult result = underTest.call();
        assertNotNull("command must return non-null result", result);
        assertEquals("command status code must be SUCCESS", CommandStatusCode.SUCCESS, result.getStatusCode());
    }

    @Test
    public void deployCommandWithPreviousReleaseSucceeds() throws Exception {
        final String RELEASE_NAME = "deploywithprevrelease";
        InstallCommand install = new InstallCommand();
        install.setAtomic(true);
        install.setDescription("test release of chart testok");
        // underTest.setDryRun(true);
        install.setDebug(true);
        install.setNamespace("default");
        currentReleaseName = RELEASE_NAME;
        install.setReleaseName(RELEASE_NAME);
        install.setWait(true);
        install.setChartDirectory(new File("src/test/helm/testok"));
        install.setTillerNamespace(Constants.TILLER_NAMESPACE);
        InstallCommandResult installResult = install.call();
        assertNotNull("command must return non-null result", installResult);
        assertEquals("command status code must be SUCCESS", CommandStatusCode.SUCCESS, installResult.getStatusCode());

        DeployCommand underTest = new DeployCommand();
        underTest.setAtomic(true);
        underTest.setDescription("test release of chart testok");
        // underTest.setDryRun(true);
        underTest.setDebug(true);
        underTest.setNamespace("default");
        underTest.setReleaseName(RELEASE_NAME);
        underTest.setWait(true);
        underTest.setChartDirectory(new File("src/test/helm/testok"));
        underTest.setTillerNamespace(Constants.TILLER_NAMESPACE);
        DeployCommandResult result = underTest.call();
        assertNotNull("command must return non-null result", result);
        assertEquals("command status code must be SUCCESS", CommandStatusCode.SUCCESS, result.getStatusCode());
    }
}
