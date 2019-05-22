package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.command.CommandStatusCode;
import group.msg.at.cloud.tools.helm.core.command.DeleteCommand;
import group.msg.at.cloud.tools.helm.core.command.InstallCommand;
import group.msg.at.cloud.tools.helm.core.command.InstallCommandResult;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class InstallCommandTest {

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
    public void installCommandWorksOk() throws Exception {
        InstallCommand underTest = new InstallCommand();
        underTest.setAtomic(true);
        underTest.setDescription("test release of chart testok");
        // underTest.setDryRun(true);
        underTest.setDebug(true);
        underTest.setNamespace("default");
        currentReleaseName = "testok";
        underTest.setReleaseName("testok");
        underTest.setWait(true);
        underTest.setChartDirectory(new File("src/test/helm/testok"));
        InstallCommandResult result = underTest.call();
        assertNotNull("command must return non-null result", result);
        assertEquals("command status code must be SUCCESS", CommandStatusCode.SUCCESS, result.getStatusCode());
    }
}
