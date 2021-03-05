package com.lipeng.providerdemo.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.List;

/**
 * @Author: lipeng
 * @Date: 2021/03/04 13:59
 */
@Activate(group = Constants.PROVIDER, order = 1)
@Slf4j
public class RedisRateLimitFilter implements Filter {

    private DefaultRedisScript<Number> redisluaScript;

    private RedisTemplate<String, Object> redisTemplate;

    public static final String LIMIT_TIME = "limitTime";
    public static final String LIMIT_COUNT = "limitCount";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        int time = invoker.getUrl().getParameter(LIMIT_TIME, 1);
        int count = invoker.getUrl().getParameter(LIMIT_COUNT, 1);
        String serviceKey = invoker.getUrl().getServiceKey();
        List<String> keys = Collections.singletonList(serviceKey);

        Number number = redisTemplate.execute(redisluaScript, keys, count, time);

        if (number != null && number.intValue() != 0 && number.intValue() <= count) {
            log.info("限流时间段内访问接口:{},第{}次", serviceKey, number.intValue());
            return invoker.invoke(invocation);
        } else {
            log.error("限流时间段内访问限制,接口名:{},访问次数限制为count:{}", serviceKey, count);
            throw new RpcException("限流时间段内访问限制");
        }
    }

    public void setRedisluaScript(DefaultRedisScript<Number> redisluaScript) {
        this.redisluaScript = redisluaScript;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}