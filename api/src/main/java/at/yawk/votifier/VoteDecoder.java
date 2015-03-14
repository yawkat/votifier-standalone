/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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
