package at.yawk.votifier;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/**
 * @author yawkat
 */
class VoteDecoder extends MessageToMessageDecoder<String> {
    private final Queue<String> lines = new ArrayDeque<>(5);

    @Override
    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        lines.add(msg);
        while (lines.size() >= 5) {
            String op = lines.poll();
            String service = lines.poll();
            String username = lines.poll();
            String address = lines.poll();
            String timestamp = lines.poll();
            out.add(new Operation(op, username, service, address, timestamp));
        }
    }
}
