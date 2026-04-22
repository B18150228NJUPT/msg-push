package com.ycc.collection;

import java.util.Properties;

public class Collections {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("1", "1");
        System.out.println(properties.get("1"));

        properties.entrySet().removeIf(x->{
            return x.getValue().toString().equals("1");
        });

        System.out.println(properties.get("1"));
    }
}
