import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

//обработчик входящих сообщений/ Inbound-значит на вход
public class MainHandler extends SimpleChannelInboundHandler<String> {
    //список каналов
    private final static List<Channel> CHANNELS = new ArrayList<>();
    private static int newClientIndex = 1;
    //имя клиента
    private String clientName;


    //срабатывает при подключении клиента
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился" + ctx);
        //обавляем канал в список каналов и присваивем клиенту имя
        CHANNELS.add(ctx.channel());
        clientName = "client #" + newClientIndex;
        newClientIndex++;
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        //считать сообщение, присланное клиентом
//        //в Netty о что приходит из сети и попадает в первый handler заворачивается в ByteBuf,то же и на выход
//        ByteBuf buf = (ByteBuf) msg;
//        //пока в буфере есть байты
//        while (buf.readableBytes() > 0) {
//            System.out.print((char)buf.readByte());
//        }
//        //после прочтения буфера, его необходимо освободить
//        buf.release();
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        System.out.println("Полученно сообщение " + msg);
        String out = String.format("[%s]:%s\n", clientName, msg);
        for (Channel ch : CHANNELS) {
            ch.writeAndFlush(out);
        }
    }

    //Если в процессе обработки посылки вылетает исключение
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //закрыть соединение
        ctx.close();
    }
}
