package com.ycc.msgpush.channelHandler;

import com.ycc.msgpush.msg.vo.TaskInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockHandler extends BaseHandler {
    @Override
    public boolean handler(TaskInfo taskInfo) {
        log.info("MockHandler#handler :{}", taskInfo);
        return true;
    }

    @Override
    Integer getChannelCode() {
        return 30;
    }
}
