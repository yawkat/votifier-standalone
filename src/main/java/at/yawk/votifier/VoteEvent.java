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

    public String getUsername() {
        return username;
    }

    public String getService() {
        return service;
    }

    public String getAddress() {
        return address;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
