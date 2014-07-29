package at.yawk.votifier;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yawkat
 */
class VotifierServerImpl implements VotifierServer {
    private final Logger logger;
    private final VotifierVersion version;
    private final SocketAddress listenAddress;
    private final PrivateKey key;
    private final Consumer<VoteEvent> listener;

    private final ServerBootstrap bootstrap;

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);
    private Channel boundChannel;

    public VotifierServerImpl(Logger logger,
                              VotifierVersion version,
                              SocketAddress listenAddress,
                              PrivateKey key,
                              Consumer<VoteEvent> listener) {
        this.logger = logger;
        this.version = version;
        this.listenAddress = listenAddress;
        this.key = key;
        this.listener = listener;

        bootstrap = new ServerBootstrap();
    }

    public void init() {
        if (!initialized.compareAndSet(false, true)) {
            return;
        }

        logger.info("Initializing votifier server...");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                        logger.fine("Client disconnected.");
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        handleError(cause);
                    }

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new VoteDecrypter(key))
                                .addLast(new LineSplitter())
                                .addLast(new VoteDecoder())
                                .addLast(new VersionEncoder())
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        Operation operation = (Operation) msg;
                                        if (operation.getOperation().equals("VOTE")) {
                                            listener.accept(new VoteEvent(operation.getUsername(),
                                                                          operation.getService(),
                                                                          operation.getAddress(),
                                                                          operation.getTimestamp()));
                                        } else {
                                            throw new UnsupportedOperationException(operation.getOperation());
                                        }
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                                            throws Exception {
                                        handleError(cause);
                                    }
                                });

                        logger.info("Client connected: Sending version packet.");
                        ch.writeAndFlush(version);
                    }
                });
    }

    @Override
    public void start() {
        if (!started.compareAndSet(false, true)) {
            return;
        }

        init();
        boundChannel = bootstrap.bind(listenAddress).awaitUninterruptibly().channel();
    }

    @Override
    public void stop() {
        if (!started.compareAndSet(true, false)) {
            return;
        }

        boundChannel.close();
        boundChannel = null;
    }

    private void handleError(Throwable cause) {
        logger.log(Level.WARNING, "Error while handling votifier message", cause);
    }
}
