package com.ycc.msgpush.dao;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/29
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public interface TrainTicketMapper {
    String selectTicket(String date, String from, String to);
}
