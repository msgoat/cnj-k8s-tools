package group.msg.at.cloud.tools.helm.core.command;

public class AbstractCommandResult {

    private CommandStatusCode statusCode;

    private String statusMessage;

    public CommandStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(CommandStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
