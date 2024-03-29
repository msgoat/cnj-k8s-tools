package group.msg.at.cloud.tools.helm.core.command;

import group.msg.at.cloud.tools.helm.core.model.Release;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a {@code helm ls} command which is a list of installed {@link Release}s.
 */
public final class ListCommandResult {

    private final List<Release> releases = new ArrayList<>();

    public ListCommandResult(List<Release> releases) {
        this.releases.addAll(releases);
    }

    public List<Release> getReleases() {
        return releases;
    }

    public boolean containsRelease(String releaseName) {
        boolean result = false;
        for (Release current : this.releases) {
            if (current.getName().equals(releaseName)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
