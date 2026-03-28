package com.ycc.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
public class DNS {
    public static void main(String[] args) throws UnknownHostException {
        InetAddress[] allByName = InetAddress.getAllByName("www.baidu.com");
        for (InetAddress inetAddress : allByName) {
            System.out.println(inetAddress);
        }
    }
}
