package com.imooc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    public void start(){
        try{
            ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();

            serverSocketChannel.bind(new InetSocketAddress(8000));
            Selector selector=Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server start successfully");
            for(;;){
                int readyChannel=selector.select();
                if(readyChannel==0)continue;
                Set<SelectionKey> set=selector.selectedKeys();
                Iterator<SelectionKey> iterator=set.iterator();
                while(iterator.hasNext()){
                    SelectionKey selectionKey=iterator.next();
                    iterator.remove();
                    if(selectionKey.isReadable()){
                        readHandler(selectionKey,selector);
                    }
                    if(selectionKey.isAcceptable()){
                        acceptHandler(serverSocketChannel,selector);
                    }
                }
            }

        }catch(IOException e){

        }

    }

    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException{
        SocketChannel socketChannel=serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector,SelectionKey.OP_READ);


    }

    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException{
        SocketChannel socketChannel=(SocketChannel)selectionKey.channel();
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        String request="";
        while(socketChannel.read(byteBuffer)>0){
            byteBuffer.flip();
            request+= Charset.forName("UTF-8").decode(byteBuffer);
        }
        socketChannel.register(selector,SelectionKey.OP_READ);
        if(request.length()>0)
            broadCast(socketChannel,selector,request);
    }

    private void broadCast(SocketChannel sourceChannel, Selector selector, String request) {
        Set<SelectionKey> set=selector.keys();
        set.forEach(selectionKey -> {
            Channel channel=selectionKey.channel();
            if(channel instanceof SocketChannel&&channel!=sourceChannel){
                try {
                    ((SocketChannel) channel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        new NioServer().start();
    }
}
