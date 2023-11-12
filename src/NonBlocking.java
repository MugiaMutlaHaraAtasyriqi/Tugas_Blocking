import java.io.IOException;
        import java.net.InetSocketAddress;
        import java.nio.ByteBuffer;
        import java.nio.channels.SelectionKey;
        import java.nio.channels.Selector;
        import java.nio.channels.ServerSocketChannel;
        import java.nio.channels.SocketChannel;
        import java.util.Iterator;
        import java.util.Set;

public class NonBlocking {

    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(8888));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server is running. Waiting for clients...");

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("Client connected: " + clientChannel.getRemoteAddress());
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int bytesRead = clientChannel.read(buffer);

                        if (bytesRead > 0) {
                            buffer.flip();
                            String message = new String(buffer.array(), 0, bytesRead);
                            System.out.println("Received from client: " + message);

                            // Echo the message back to the client
                            clientChannel.write(ByteBuffer.wrap(message.getBytes()));
                        } else {
                            // Client disconnected
                            key.cancel();
                            clientChannel.close();
                        }
                    }

                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}