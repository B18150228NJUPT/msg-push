package com.ycc.spi;

import java.util.List;
import java.util.ServiceLoader;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
public abstract class InstrumentationModule {
    public abstract List<String> typeInstrumentations();

    public static void main(String[] args) {
        ServiceLoader<InstrumentationModule> load = ServiceLoader.load(InstrumentationModule.class);
        for (InstrumentationModule instrumentationModule : load) {
            System.out.println(instrumentationModule.typeInstrumentations());
        }
    }
}
