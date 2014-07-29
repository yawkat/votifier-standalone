package at.yawk.votifier;

/**
 * @author yawkat
 */
class Operation {
    private final String operation;
    private final String username;
    private final String service;
    private final String address;
    private final String timestamp;

    public Operation(String operation, String username, String service, String address, String timestamp) {
        this.operation = operation;
        this.username = username;
        this.service = service;
        this.address = address;
        this.timestamp = timestamp;
    }

    public String getOperation() {
        return operation;
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
