import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerApp {
    private static final int PORT = 8189;
    public static void main(String[] args) {
        //создание 2 пула потоков (менеджеры потоков)
        //bossGroup отвечает за подключающихся клиентов, достаточно одного потока
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //workerGroup - обработка данных, всё сетевое взаимодействие
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //создание и настройка сервера. ServerBootstrap выполняет преднастройку сервера
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //назначение серверу два пула потоков
            serverBootstrap.group(bossGroup, workerGroup)
                    //создание канала для подключения клиентов
                    .channel(NioServerSocketChannel.class)
                    //при подключении клиента, информация о соединении будет в SocketChannel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //добавление handler`а в конвеер(pipeline)
                            //для каждого клиента будет свой конвеер (new MainHandler())
                            //поставив new StringDecoder(), new StringEncoder() - мы уверены, что работаем только со строками
                            socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new MainHandler());
                        }
                    });
            //Обыекты типа Future - это информация о выполняемой задаче. ChannelFuture - доступ к запущенному серверу
            //.sync() - старт сервера
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            //это блокирующая операция. пока сервер не остановится, дальше код не будет обрабатываться
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            //после остановки сервера закрытие пулов потоков
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
