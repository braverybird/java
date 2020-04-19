package com.imooc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class NioClient {
    public void start(String name)throws IOException{

        SocketChannel socketChannel=SocketChannel.open(new InetSocketAddress("127.0.0.1",8000));
        Selector selector=Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();
        Scanner scanner=new Scanner(System.in);
        while(scanner.hasNextLine()){
            String request=scanner.nextLine();
            if(request!=null&&request.length()!=0)
            socketChannel.write(Charset.forName("UTF-8").encode(name+":"+request));
        }
    }
}
