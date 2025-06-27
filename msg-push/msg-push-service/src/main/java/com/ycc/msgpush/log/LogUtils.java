package com.ycc.msgpush.log;

import cn.monitor4all.logRecord.annotation.OperationLog;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.ycc.msgpush.controller.SendMqService;
import com.ycc.msgpush.msg.vo.AnchorInfo;
import com.ycc.msgpush.msg.vo.LogParam;
import com.ycc.msgpush.msg.vo.SendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogUtils {

    @Autowired
    private SendMqService sendMqService;


    @Autowired
    private KafkaTemplate kafkaTemplate;

    @OperationLog(bizType = "SendService#send", bizId = "#sendRequest.messageTemplateId", msg = "#sendRequest")
    public void printMsg(SendRequest sendRequest) {
        log.info("execute printMsg");
    }

    /**
     * 记录当前对象信息
     */
    public void print(LogParam logParam) {
        logParam.setTimestamp(System.currentTimeMillis());
        log.info(JSON.toJSONString(logParam));
    }

    public static final String topicName = "austinTraceLog";

    /**
     * 记录打点信息
     */
    public void print(AnchorInfo anchorInfo) {
        anchorInfo.setLogTimestamp(System.currentTimeMillis());
        String message = JSON.toJSONString(anchorInfo);
        log.info(message);

        try {
            kafkaTemplate.send(topicName, message);
        } catch (Exception e) {
            log.error("LogUtils#print send mq fail! e:{},params:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(anchorInfo));
        }
    }
}
