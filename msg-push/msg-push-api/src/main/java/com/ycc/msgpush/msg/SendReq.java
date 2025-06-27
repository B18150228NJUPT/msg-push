package com.ycc.msgpush.msg;

import com.ycc.msgpush.msg.vo.MessageParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendReq {
    Long messageTemplateId;
    MessageParam messageParam;
    MessageTemplate messageTemplate;
}