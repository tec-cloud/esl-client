package org.freeswitch.esl.client.inbound;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.freeswitch.esl.client.transport.message.EslFrameDecoder;

/**
 * End users of the {@link Client} should not need to use this class.
 * <p/>
 * Convenience factory to assemble a Netty processing pipeline for inbound clients.
 */
class InboundChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ChannelHandler handler;

    public InboundChannelInitializer(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new EslFrameDecoder(8192));
        pipeline.addLast("writeTimeoutHandler", new WriteTimeoutHandler(30));

        // now the inbound client logic
        pipeline.addLast("clientHandler", handler);
        pipeline.addLast("encoder", new StringEncoder());
    }
}