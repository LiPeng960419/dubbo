package com.lipeng.common.utils;

/**
 * @Author: lipeng
 * @Date: 2021/03/05 10:30
 */
public class TraceIdUtils {

    private static final ThreadLocal<String> TraceIds = new ThreadLocal<String>();

    public static String getTraceId() {
        return TraceIds.get();
    }

    public static void setTraceId(String traceId) {
        TraceIds.set(traceId);
    }

    public static void clear() {
        TraceIds.remove();
    }

}