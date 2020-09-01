import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NetWork {
    //SocketChannel из netty пакета, не IO
    private SocketChannel channel;
//    private Callback onMsgReceivedCallback;

    //можно вынести в отдельный конфигурационный файл
    private static final String HOST = "localhost";
    private static final int PORT = 8198;

    //при создании объекта в паралельном потоке запустится клиент
    public NetWork(Callback onMsgReceivedCallback) {
//        this.onMsgReceivedCallback = onMsgReceivedCallback;
        //если не создать отдельный поток, то блокирующая операция future.channel().closeFuture().sync() заблокирует запуск всего интерфейса
        Thread netThread = new Thread(() -> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        //стандартный NIO сетевой канал
                        .channel(NioSocketChannel.class)
                        //при подключении к серверу открывается SocketChannel со стороны клиента
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            //когда соединение открывается получаем ссылку на соединение - socketChannel
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                //запоминаем ссылку на соединение
                                channel = socketChannel;
                                //StringDecoder-входящий handler. StringEncoder - исходящий handler. Хэндлеры из коробки
                                //когда в канал отправим строку(sendMessage(String str)), строка пролетит через StringEncoder
                                //StringEncoder преобразует строку в ByteBuf
                                socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ClientHandler(onMsgReceivedCallback));
                            }
                        });
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
                //Чтобы клиент после открытия соединения моментально не закрылся, делаем ожидание закрытия
                future.channel().closeFuture().sync();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        netThread.setDaemon(true);
        netThread.start();
    }

    public void sendMessage(String str) {
        channel.writeAndFlush(str);
    }
}
