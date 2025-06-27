package com.ycc.msgpush.channelHandler;


import com.ycc.msgpush.msg.vo.TaskInfo;

/**
 * @author 3y
 * 消息处理器
 */
public interface Handler {

    /**
     * 处理器
     *
     * @param taskInfo
     */
    void doHandler(TaskInfo taskInfo);

}
