package com.imooc;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioClientHandler implements Runnable {
    private Selector selector;
    public NioClientHandler(Selector selector){
        this.selector=selector;
    }


    public void run() {
        try{
            for(;;){
                int readyChannel=selector.select();
                if(readyChannel==0)
                    continue;
                Set<SelectionKey> set=selector.selectedKeys();
                Iterator<SelectionKey> iterator=set.iterator();
                while(iterator.hasNext()){
                    SelectionKey selectionKey=iterator.next();
                    iterator.remove();
                    if(selectionKey.isReadable()){
                        readHandler(selectionKey,selector);
                    }

                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException{
        SocketChannel socketChannel=(SocketChannel)selectionKey.channel();

        ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
        StringBuilder stringBuilder=new StringBuilder();
        while(socketChannel.read(byteBuffer)>0){
            byteBuffer.flip();
            stringBuilder.append(Charset.forName("UTF-8").decode(byteBuffer));
        }
        socketChannel.register(selector,SelectionKey.OP_READ);
        if(stringBuilder.length()>0){
            System.out.println(stringBuilder.toString());
        }
    }
}
