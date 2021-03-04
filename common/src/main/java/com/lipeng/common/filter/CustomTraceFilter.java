package com.lipeng.common.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: lipeng
 * @Date: 2021/03/04 13:59
 */
@Activate(group = Constants.PROVIDER)
@Slf4j
public class CustomTraceFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        log.info("ProviderFilter invoked");
        return invoker.invoke(invocation);
    }

}