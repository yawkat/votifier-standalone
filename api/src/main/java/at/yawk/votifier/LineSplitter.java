/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
