package com.ycc.msgpush.controller;

import com.ycc.msgpush.trace.DataParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * 获取数据接口（全链路追踪)
 */
@Slf4j
//@AustinAspect
//@AustinResult
@RestController
@RequestMapping("/trace")
@Api("获取数据接口（全链路追踪)")
public class DataController {
//    @Autowired
//    private DataService dataService;

    @PostMapping("/message/{id}/test")
    @ApiOperation("/获取【72小时】发送消息的全链路数据")
    public Object getMessageData(@RequestBody DataParam dataParam, @PathVariable("id") String id) throws InterruptedException {
//        return dataService.getTraceMessageInfo(dataParam.getMessageId());
        TimeUnit.SECONDS.sleep(1);
        return null;
    }

    /*@PostMapping("/user")
    @ApiOperation("/获取【当天】用户接收消息的全链路数据")
    public UserTimeLineVo getUserData(@RequestBody DataParam dataParam) {
        if (Objects.isNull(dataParam) || CharSequenceUtil.isBlank(dataParam.getReceiver())) {
            return UserTimeLineVo.builder().items(new ArrayList<>()).build();
        }
        return dataService.getTraceUserInfo(dataParam.getReceiver());
    }

    @PostMapping("/messageTemplate")
    @ApiOperation("/获取消息模板全链路数据")
    public EchartsVo getMessageTemplateData(@RequestBody DataParam dataParam) {
        EchartsVo echartsVo = EchartsVo.builder().build();
        if (CharSequenceUtil.isNotBlank(dataParam.getBusinessId())) {
            echartsVo = dataService.getTraceMessageTemplateInfo(dataParam.getBusinessId());
        }
        return echartsVo;
    }

    @PostMapping("/sms")
    @ApiOperation("/获取短信下发数据")
    public SmsTimeLineVo getSmsData(@RequestBody DataParam dataParam) {
        if (Objects.isNull(dataParam) || Objects.isNull(dataParam.getDateTime()) || CharSequenceUtil.isBlank(dataParam.getReceiver())) {
            return SmsTimeLineVo.builder().items(Lists.newArrayList()).build();
        }
        return dataService.getTraceSmsInfo(dataParam);
    }*/

}
