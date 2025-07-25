package com.ycc.msgpush.service;


import com.ycc.msgpush.trace.TraceResponse;

/**
 * 链路查询接口
 *
 * @Author: sky
 * @Date: 2023/7/13 13:35
 * @Description: TraceService
 * @Version 1.0.0
 */
public interface TraceService {

    /**
     * 基于消息 ID 查询 链路结果
     *
     * @param messageId
     * @return
     */
    TraceResponse traceByMessageId(String messageId);
}
