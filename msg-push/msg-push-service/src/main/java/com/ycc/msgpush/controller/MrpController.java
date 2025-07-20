package com.ycc.msgpush.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/14
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
@RestController
@RequestMapping("/mrp")
public class MrpController {

    @RequestMapping("/calculate")
    public String calculate() {
        return "get";
    }
}
