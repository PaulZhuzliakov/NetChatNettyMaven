import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private Callback onMsgReceivedCallback;

    public ClientHandler(Callback onMsgReceivedCallback) {
        this.onMsgReceivedCallback = onMsgReceivedCallback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        if (onMsgReceivedCallback != null) {
            onMsgReceivedCallback.callback(s);
        }
    }
}
