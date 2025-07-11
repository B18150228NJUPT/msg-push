package com.ycc;

import com.alibaba.fastjson.JSON;
import com.java3y.austin.common.domain.AnchorInfo;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * @author 3y
 * @date 2022/2/22
 * process 处理
 */
public class AustinFlatMapFunction implements FlatMapFunction<String, AnchorInfo> {

    @Override
    public void flatMap(String value, Collector<AnchorInfo> collector) throws Exception {
        AnchorInfo anchorInfo = null;
        try {
            anchorInfo = JSON.parseObject(value, AnchorInfo.class);
        } catch (Exception e) {
            System.out.println("err value" + value);
            return;
        }
        collector.collect(anchorInfo);
    }
}
