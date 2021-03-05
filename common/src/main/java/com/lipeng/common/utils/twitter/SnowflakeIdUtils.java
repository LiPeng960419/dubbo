package com.lipeng.common.utils.twitter;


public class SnowflakeIdUtils {

    private static final SnowflakeIdWorker idWorker;

    static {
        // 使用静态代码块初始化 SnowflakeIdWorker
        idWorker = new SnowflakeIdWorker(1, 1);
    }

    public static String nextId() {
        return String.valueOf(idWorker.nextId());
    }

    public static Long nextLongId() {
        return idWorker.nextId();
    }

}