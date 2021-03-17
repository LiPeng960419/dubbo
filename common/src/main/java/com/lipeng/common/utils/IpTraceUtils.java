package com.lipeng.common.utils;

/**
 * @Author: lipeng
 * @Date: 2021/03/05 10:30
 */
public class IpTraceUtils {

    private static final ThreadLocal<String> IP = new ThreadLocal<String>();

    public static String getIp() {
        return IP.get();
    }

    public static void setIp(String traceId) {
        IP.set(traceId);
    }

    public static void clear() {
        IP.remove();
    }

}