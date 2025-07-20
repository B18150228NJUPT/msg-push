package com.ycc.route;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author chengcheng.yan@youhualin.com
 * @Description
 * @createTime 2025/7/9
 * @Copyright 悠桦林信息科技（上海）有限公司
 * @Version 1.0
 */
public class Route {

    public static String selectColor(String[] colorArr, int[] weightArr) {
        int length = colorArr.length;
        boolean sameWeight = true;
        int totalWeight = 0;
        for (int i = 0; i < length; i++) {
            int weight = weightArr[i];
            totalWeight += weight;
            if (sameWeight && totalWeight != weight * (i + 1)) {
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            System.out.println("offset:" + offset);
            for (int i = 0; i < length; i++) {
                if (offset < weightArr[i]) {
                    return colorArr[i];
                }
            }
        }
        return colorArr[ThreadLocalRandom.current().nextInt(length)];
    }

    //测试代码
    public static void main(String[] args) {
        String[] colorArr = new String[]{"GREEN", "BLUE"};
        for (int i = 0; i < 10; i++) {
            System.out.println(selectColor(colorArr, new int[]{1, 9}));
        }
    }
}
