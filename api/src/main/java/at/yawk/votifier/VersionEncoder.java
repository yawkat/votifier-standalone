package at.yawk.votifier;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author yawkat
 */
class VersionEncoder extends MessageToByteEncoder<VotifierVersion> {
    @Override
    protected void encode(ChannelHandlerContext ctx, VotifierVersion msg, ByteBuf out) throws Exception {
        String message = "VOTIFIER " + msg.getName() + "\n";
        // apparently votifier uses platform encoding by default .-., we'll use UTF-8
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        out.writeBytes(messageBytes);
    }
}
