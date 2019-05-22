package group.msg.at.cloud.tools.helm.core.model;

import java.util.Objects;

/**
 * Represents a Helm release as returned by helm list.
 */
public final class Release {

    private String name;

    public Release(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Release release = (Release) o;
        return Objects.equals(name, release.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
