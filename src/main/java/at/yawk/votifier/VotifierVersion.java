package at.yawk.votifier;

/**
 * @author yawkat
 */
public class VotifierVersion {
    private static final VotifierVersion LATEST = new VotifierVersion("1.9");

    private final String name;

    public static VotifierVersion getDefault() {
        return LATEST;
    }

    public VotifierVersion(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
