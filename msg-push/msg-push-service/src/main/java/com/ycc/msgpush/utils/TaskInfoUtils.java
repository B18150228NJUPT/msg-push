package com.ycc.msgpush.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;

import java.util.Date;


public class TaskInfoUtils {
    private static final int TYPE_FLAG = 1000000;
    private static final String CODE = "track_code_bid";

    private TaskInfoUtils() {
    }

    /**
     * 生成任务唯一Id
     *
     * @return
     */
    public static String generateMessageId() {
        return IdUtil.nanoId();
    }

    /**
     * 生成BusinessId
     * 模板类型+模板ID+当天日期
     * (固定16位)
     */
    public static Long generateBusinessId(Long templateId, Integer templateType) {
        Integer today = Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN));
        return Long.valueOf(String.format("%d%s", templateType * TYPE_FLAG + templateId, today));
    }

    public static void main(String[] args) {
        // 8 位
        Integer today = Integer.valueOf(DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN));

        // templateType 10/20
        // 8 位 +
        int templateId = 100;
        int tempType = 10;
        Long.valueOf(String.format("%d%s", tempType * TYPE_FLAG + templateId, today));
    }

    /**
     * 第二到8位为MessageTemplateId 切割出模板ID
     */
    public static Long getMessageTemplateIdFromBusinessId(Long businessId) {
        return Long.valueOf(String.valueOf(businessId).substring(1, 8));
    }

    /**
     * 从businessId切割出日期
     */
    public static Long getDateFromBusinessId(Long businessId) {
        return Long.valueOf(String.valueOf(businessId).substring(8));
    }


    /**
     * 对url添加平台参数（用于追踪数据)
     */
    public static String generateUrl(String url, Long templateId, Integer templateType) {
        url = url.trim();
        Long businessId = generateBusinessId(templateId, templateType);
        if (url.indexOf(CommonConstant.QM) == -1) {
            return url + CommonConstant.QM_STRING + CODE + CommonConstant.EQUAL_STRING + businessId;
        } else {
            return url + CommonConstant.AND_STRING + CODE + CommonConstant.EQUAL_STRING + businessId;
        }
    }

}
