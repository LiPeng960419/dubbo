package com.lipeng.consumerdemo.config;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;
import com.lipeng.common.utils.IpTraceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: lipeng
 * @Date: 2021/03/11 18:17
 */
@Slf4j
public class GrayLoadBalance extends AbstractLoadBalance {

    private BasicConf basicConf;

    public void setBasicConf(BasicConf basicConf) {
        this.basicConf = basicConf;
    }

    /**
     * 必须有多个服务提供者才能 选择负载均衡  否则默认get(0) 不会执行这里逻辑
     *
     * @param invokers
     * @param url
     * @param invocation
     * @param <T>
     * @return
     */
    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        List<Invoker<T>> list = new ArrayList<>(invokers);
        // 可以通过RpcContext attachments 或者通过filter传递参数
        Map<String, String> map = RpcContext.getContext().getAttachments();
        String userId = map.get("userId");
        String userIds = basicConf.getGrayPushUsers();
        List<Invoker<T>> grayList = new ArrayList<>();
        boolean isGray = false;
        if (StringUtils.isNotBlank(userIds) && StringUtils.isNotBlank(userId)) {
            HashSet<String> users = new HashSet<>(Arrays.asList(userIds.split(",")));
            if (users.contains(userId)) {
                isGray = true;
                Iterator<Invoker<T>> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Invoker<T> invoker = iterator.next();
                    String profile = invoker.getUrl().getParameter(ProfileEnum.PROFILE, ProfileEnum.PROD.getCode());
                    if (ProfileEnum.GRAY.getCode().equals(profile)) {
                        grayList.add(invoker);
                    } else {
                        // 如果灰度用户没找到灰度服务那么就访问不到了
                        iterator.remove();
                    }
                }
            }
        }

        // 如果是 user是灰度 且灰度服务列表不为空 那么不走ip灰度校验了
        boolean checkIp = true;
        if (isGray && !CollectionUtils.isEmpty(grayList)) {
            checkIp = false;
        }

        // 如果userid不是灰度，那根据ip判断灰度
        if (checkIp) {
            HashSet<String> ips = new HashSet<>(Arrays.asList(basicConf.getGrayPushIps().split(",")));
            if (!CollectionUtils.isEmpty(ips) && ips.contains(IpTraceUtils.getIp())) {
                isGray = true;
                Iterator<Invoker<T>> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Invoker<T> invoker = iterator.next();
                    String profile = invoker.getUrl().getParameter(ProfileEnum.PROFILE, ProfileEnum.PROD.getCode());
                    if (ProfileEnum.GRAY.getCode().equals(profile)) {
                        grayList.add(invoker);
                    } else {
                        // 如果灰度用户没找到灰度服务那么就访问不到了
                        iterator.remove();
                    }
                }
            }
        }

        if (isGray) {
            if (CollectionUtils.isEmpty(grayList)) {
                log.warn("未找到灰度服务,当前用户id:{}", userId);
                throw new RpcException("未找到灰度服务,当前用户id:" + userId);
            } else {
                log.info("当前用户:{}正在走灰度服务", userId);
                return this.randomSelect(grayList, url, invocation);
            }
        }
        // 不是灰度用户 排除灰度服务 走正式服务
        List<Invoker<T>> seversExcludeGray = new ArrayList<>(list);
        Iterator<Invoker<T>> iterator = seversExcludeGray.iterator();
        while (iterator.hasNext()) {
            Invoker<T> invoker = iterator.next();
            String profile = invoker.getUrl().getParameter(ProfileEnum.PROFILE, ProfileEnum.PROD.getCode());
            if (ProfileEnum.GRAY.getCode().equals(profile)) {
                iterator.remove();
            }
        }
        log.info("当前用户:{}正在走正式服务", userId);
        return this.randomSelect(seversExcludeGray, url, invocation);
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
        if (invokers.size() == 1) {
            return invokers.get(0);
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