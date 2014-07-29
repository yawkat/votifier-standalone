package at.yawk.votifier;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author yawkat
 */
class LineSplitter extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        int lineLength = 0;
        boolean found = false;
        while (in.isReadable()) {
            if (in.readByte() == '\n') {
                found = true;
                break;
            }
            lineLength++;
        }
        in.resetReaderIndex();
        if (found) {
            byte[] line = new byte[lineLength];
            in.readBytes(line);
            in.readByte(); // newline
            out.add(new String(line, StandardCharsets.UTF_8));
        }
    }
}
