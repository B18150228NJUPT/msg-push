package com.ycc.nio;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 *
 *
 * @author <a href="03452136@yto.net.cn">chengcheng.yan</a>
 * @version 1.0.0
 */
public class Inet4Address {
    public static void main(String[] args) throws UnknownHostException, IllegalAccessException {
        String ip = new String("139.226.66.4");
        // bytes :  [ 49, 51, 57, 46, 50, 50, 54, 46, 54, 54, 46, 52 ]
        // . -> 46
        // 1 -> 49
        // 3 -> 51
        // ASCII -> Unicode -> UTF-8
        byte[] bytes = ip.getBytes(StandardCharsets.UTF_8);
        byte[] bytes1 = {(byte) 139, (byte) 226, (byte) 66, (byte) 4};
        InetAddress byAddress = InetAddress.getByAddress(bytes1);

        for (Field field : byAddress.getClass().getSuperclass().getDeclaredFields()) {
            if (field.getName().equals("holder")) {
                field.setAccessible(true);
                Object holder = field.get(byAddress);
                for (Field field1 : holder.getClass().getDeclaredFields()) {
                    if (field1.getName().equals("address")) {
                        field1.setAccessible(true);
                        System.out.println(field1.get(holder));
                        // address = -1948106236
                    }
                }
            }
        }

        System.out.println(byAddress);
    }
}
