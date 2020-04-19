package com.imooc;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;

public class AClient {
    public static void main(String[] args) throws IOException {
        new NioClient().start("AClient");
    }
}
