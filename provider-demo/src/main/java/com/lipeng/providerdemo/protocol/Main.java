package com.lipeng.providerdemo.protocol;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Protocol;

/**
 * @Author: lipeng
 * @Date: 2021/03/04 11:39
 */
public class Main {

    public static void main(String[] args) {
        Protocol myProtocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension("myProtocol");
        System.out.println("port:" + myProtocol.getDefaultPort());
    }

}