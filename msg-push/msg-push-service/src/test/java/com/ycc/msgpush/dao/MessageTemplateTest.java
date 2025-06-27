package com.ycc.msgpush.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import com.ycc.msgpush.msg.MessageTemplate;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.ArrayList;
import java.util.Properties;


class MessageTemplateTest {

    @Test
    void getId() {
        System.out.println(StrPool.COMMA);
        String msgContent = "任务执行完成，请查看 ${code}";
        MessageTemplate messageTemplate = new MessageTemplate();
        messageTemplate.setId(1L).setName("name").setMsgContent(msgContent);

        Properties properties = new Properties();
        properties.put("code", "200");

        PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");
        System.out.println(helper.replacePlaceholders(msgContent, properties));

        ArrayList<MessageTemplate> messageTemplates = Lists.newArrayList(messageTemplate);

        System.out.println(CollUtil.getFirst(messageTemplates.iterator()));
    }
}