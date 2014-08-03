package at.yawk.votifier;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.PrivateKey;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Builder for a VotifierServer instance.
 *
 * @author yawkat
 */
public class VotifierServerBuilder {
    private Logger logger = Logger.getLogger("at.yawk.votifier");
    private PrivateKey key = null;
    private VotifierVersion version = VotifierVersion.getDefault();
    private InetSocketAddress listenAddress = InetSocketAddress.createUnresolved("0.0.0.0", 8192);
    private Consumer<VoteEvent> voteListener =
            e -> logger.warning("Received vote event, but no listener is registered!");

    public VotifierServerBuilder logger(Logger logger) {
        Objects.requireNonNull(logger);
        this.logger = logger;
        return this;
    }

    public VotifierServerBuilder privateKey(PrivateKey key) {
        Objects.requireNonNull(key);
        this.key = key;
        return this;
    }

    public VotifierServerBuilder key(VotifierKeyPair keyPair) {
        return privateKey(keyPair.getPair().getPrivate());
    }

    public VotifierServerBuilder version(VotifierVersion version) {
        Objects.requireNonNull(version);
        this.version = version;
        return this;
    }

    public VotifierServerBuilder listenAddress(InetSocketAddress address) {
        Objects.requireNonNull(address);
        this.listenAddress = address;
        return this;
    }

    public VotifierServerBuilder address(InetAddress address) {
        return listenAddress(new InetSocketAddress(address, this.listenAddress.getPort()));
    }

    public VotifierServerBuilder port(int port) {
        return listenAddress(new InetSocketAddress(this.listenAddress.getAddress(), port));
    }

    public VotifierServerBuilder voteListener(Consumer<VoteEvent> listener) {
        Objects.requireNonNull(listener);
        this.voteListener = listener;
        return this;
    }

    public VotifierServer build() {
        return new VotifierServerImpl(logger, version, listenAddress, key, voteListener);
    }

    /**
     * build() and start the server.
     */
    public VotifierServer start() {
        VotifierServer server = build();
        server.start();
        return server;
    }
}
