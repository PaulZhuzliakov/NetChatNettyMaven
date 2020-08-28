import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//обработчик входящих сообщений/ Inbound-значит на вход
public class MainHandler extends ChannelInboundHandlerAdapter {

    //срабатывает при подключении клиента
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //считать сообщение, присланное клиентом
        //в Netty о что приходит из сети и попадает в первый handler заворачивается в ByteBuf,то же и на выход
        ByteBuf buf = (ByteBuf) msg;
        //пока в буфкрк есть байты
        while (buf.readableBytes() > 0) {
            System.out.print((char)buf.readByte());
        }
        //после прочтения буфера, его необходимо освободить
        buf.release();
    }

    //Если в процессе обработки посылки вылетает исключение
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //распечатать эксептион
        cause.printStackTrace();
        //закрыть соединение
        ctx.close();
    }
}
