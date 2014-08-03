package at.yawk.votifier;

/**
 * Votifier TCP server that operates on one listen address.
 *
 * @author yawkat
 */
public interface VotifierServer {
    void start();

    void stop();
}
