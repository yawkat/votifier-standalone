package at.yawk.votifier;

/**
 * @author yawkat
 */
public class VoteEvent {
    private final String username;
    private final String service;
    private final String address;
    private final String timestamp;

    VoteEvent(String username, String service, String address, String timestamp) {
        this.username = username;
        this.service = service;
        this.address = address;
        this.timestamp = timestamp;
    }

    /**
     * Username of the player that voted.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Name of the service the player voted on.
     */
    public String getService() {
        return service;
    }

    /**
     * IP address the player voted from.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Timestamp of the vote (usually seconds since epoch).
     */
    public String getTimestamp() {
        return timestamp;
    }
}
