package com.ycc.msgpush.flowcontrol;

import com.google.common.util.concurrent.RateLimiter;
import com.ycc.msgpush.msg.vo.TaskInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @date 2022/4/18
 * <p>
 * 流量控制所需要的参数
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowControlParam {

    /**
     * 限流器
     * 子类初始化的时候指定
     */
    protected RateLimiter rateLimiter;

    /**
     * 限流器初始限流大小
     * 子类初始化的时候指定
     */
    protected Double rateInitValue;

    public void flowControl(TaskInfo taskInfo) {
        //todo
    }
}
