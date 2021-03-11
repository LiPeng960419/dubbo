package com.lipeng.consumerdemo.config;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: lipeng
 * @Date: 2021/03/11 18:17
 */
public class GrayLoadBalance extends AbstractLoadBalance {

    private BasicConf basicConf;

    public void setBasicConf(BasicConf basicConf) {
        this.basicConf = basicConf;
    }

    public static final String GRAY = "gray";

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        List<Invoker<T>> list = new ArrayList<>(invokers);
        Map<String, String> map = RpcContext.getContext().getAttachments();
        String userId = map.get("userId");
        String userIp = map.get("userIp");
        String userIds = basicConf.getGrayPushUsers();
        // userIps和userIds一样，这里就不再重复演示了
        String userIps = basicConf.getGrayPushIps();
        List<Invoker<T>> grayList = new ArrayList<>();
        if (StringUtils.isNotBlank(userIds) && StringUtils.isNotBlank(userId)) {
            String[] uids = userIds.split(",");
            if (Arrays.asList(uids).contains(userId)) {
                Iterator<Invoker<T>> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Invoker<T> invoker = iterator.next();
                    String profile = invoker.getUrl().getParameter("profile", "prod");
                    if (GRAY.equals(profile)) {
                        grayList.add(invoker);
                    } else {
                        // 如果灰度用户没找到灰度服务那么就访问不到了
                        iterator.remove();
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(grayList)) {
            return this.randomSelect(grayList, url, invocation);
        }
        return this.randomSelect(list, url, invocation);
    }

    /**
     * 重写了一遍随机负载策略
     *
     * @param invokers
     * @param url
     * @param invocation
     * @param <T>
     * @return
     */
    private <T> Invoker<T> randomSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        if (CollectionUtils.isEmpty(invokers)) {
            throw new RpcException("找不到对应服务提供方,url:" + url.getServiceKey());
        }
        int length = invokers.size();
        boolean sameWeight = true;
        int[] weights = new int[length];
        int firstWeight = this.getWeight(invokers.get(0), invocation);
        weights[0] = firstWeight;
        int totalWeight = firstWeight;

        int offset;
        int i;
        for (offset = 1; offset < length; ++offset) {
            i = this.getWeight(invokers.get(offset), invocation);
            weights[offset] = i;
            totalWeight += i;
            if (sameWeight && i != firstWeight) {
                sameWeight = false;
            }
        }

        if (totalWeight > 0 && !sameWeight) {
            offset = ThreadLocalRandom.current().nextInt(totalWeight);

            for (i = 0; i < length; ++i) {
                offset -= weights[i];
                if (offset < 0) {
                    return invokers.get(i);
                }
            }
        }
        return invokers.get(ThreadLocalRandom.current().nextInt(length));
    }

}