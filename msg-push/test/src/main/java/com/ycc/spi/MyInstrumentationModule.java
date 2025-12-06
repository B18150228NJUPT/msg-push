package com.ycc.spi;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
@AutoService(InstrumentationModule.class)
public class MyInstrumentationModule extends InstrumentationModule {
    @Override
    public List<String> typeInstrumentations() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("1");
        return strings;
    }
}
