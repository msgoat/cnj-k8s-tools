package group.msg.at.cloud.tools.helm.core.command;

import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InstallCommandTest {

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
    public void installCommandWorksOk() throws Exception {
        final String RELEASE_NAME = "installnoprevrelease";
        InstallCommand underTest = new InstallCommand();
        underTest.setAtomic(true);
        underTest.setDebug(true);
        underTest.setNamespace("default");
        currentReleaseName = RELEASE_NAME;
        underTest.setReleaseName(RELEASE_NAME);
        underTest.setWait(true);
        underTest.setChartDirectory(new File("src/test/helm/testok"));
        underTest.setNamespace(Constants.RELEASE_NAMESPACE);
        InstallCommandResult result = underTest.call();
        assertNotNull("command must return non-null result", result);
        assertEquals("command status code must be SUCCESS", CommandStatusCode.SUCCESS, result.getStatusCode());
    }

    @Test
    public void installWithExistingReleaseFails() throws Exception {
        final String RELEASE_NAME = "installwithprevrelease";
        InstallCommand install = new InstallCommand();
        install.setAtomic(true);
        install.setDebug(true);
        install.setNamespace("default");
        currentReleaseName = RELEASE_NAME;
        install.setReleaseName(RELEASE_NAME);
        install.setWait(true);
        install.setChartDirectory(new File("src/test/helm/testok"));
        install.setNamespace(Constants.RELEASE_NAMESPACE);
        InstallCommandResult installResult = install.call();
        assertNotNull("command must return non-null result", installResult);
        assertEquals("command status code must be SUCCESS", CommandStatusCode.SUCCESS, installResult.getStatusCode());

        InstallCommand underTest = new InstallCommand();
        underTest.setAtomic(true);
        underTest.setDebug(true);
        underTest.setNamespace("default");
        currentReleaseName = RELEASE_NAME;
        underTest.setReleaseName(RELEASE_NAME);
        underTest.setWait(true);
        underTest.setChartDirectory(new File("src/test/helm/testok"));
        underTest.setNamespace(Constants.RELEASE_NAMESPACE);
        InstallCommandResult result = underTest.call();
        assertNotNull("command must return non-null result", result);
        assertEquals("command status code must be SUCCESS", CommandStatusCode.FAILURE, result.getStatusCode());
    }
}
