package com.ycc.nacos;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.NacosConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class NacosConfig implements CommandLineRunner {

//    @Autowired
//    NacosConfigService nacosConfigService;
//    @Autowired
//    NacosConfigManager nacosConfigManager;


    public void publish() throws NacosException {
        Properties properties = new Properties();
        properties.put("test.value", "2");
        String content = convertPropertiesToYamlStringWithComments(properties);
        System.out.println(content);

        NacosConfigService nacosConfigService = (NacosConfigService) ConfigFactory.createConfigService("localhost:8848");

        String defaultGroup = nacosConfigService.getConfig("test-conf.yml", "DEFAULT_GROUP", 500);

        nacosConfigService.publishConfig("test-conf.yml", "DEFAULT_GROUP", content);
    }

    public static void main(String[] args) throws NacosException {
        Yaml yaml = new Yaml();
        Map<String, Object> map  = yaml.load("test:\n" +
                "    value: 1\n");
        System.out.println(map);

        ConversionService conversionService = new DefaultConversionService();

        Properties properties = new Properties();
        properties.put("test.value", conversionService.convert("1-切割,2", String.class));
        properties.put("test.default", conversionService.convert("1-切割,2", String.class));
//        properties.put("test.default.name", conversionService.convert("1-切割,2", String.class));
//        properties.put("test", conversionService.convert("1-切割,2", String.class));
        String content = convertPropertiesToYamlStringWithComments(properties);
        System.out.println(content);

        NacosConfigService nacosConfigService = (NacosConfigService) ConfigFactory.createConfigService("localhost:8848");

        String defaultGroup = nacosConfigService.getConfig("test-conf.yml", "DEFAULT_GROUP", 500);

        nacosConfigService.publishConfig("test-conf", "DEFAULT_GROUP", content);
    }

    @Override
    public void run(String... args) throws Exception {
        publish();
    }

    public static String convertPropertiesToYamlStringWithComments(Properties properties) {
        // 使用 LinkedHashMap 保持顺序
        Map<String, Object> map = new LinkedHashMap<>();
        Map<String, String> comments = new LinkedHashMap<>();

        // 处理属性和注释
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("#")) {
                // 处理注释
                String realKey = key.substring(1);
                comments.put(realKey, properties.getProperty(key));
            } else {
                setNestedMap(map, key, properties.getProperty(key));
            }
        }

        // 配置 YAML 输出选项
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        // 转换为 YAML 字符串
        StringBuilder yamlString = new StringBuilder();
        dumpMapWithComments(map, comments, "", yamlString);

        return yamlString.toString();
    }

    private static void setNestedMap(Map<String, Object> map, String key, String value) {
        String[] parts = key.split("\\.");
        Map<String, Object> current = map;

        for (int i = 0; i < parts.length - 1; i++) {
            current = (Map<String, Object>) current.computeIfAbsent(
                    parts[i],
                    k -> new LinkedHashMap<String, Object>()
            );
        }

        current.put(parts[parts.length - 1], value);
    }

    private static void dumpMapWithComments(Map<String, Object> map,
                                            Map<String, String> comments,
                                            String indent,
                                            StringBuilder result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();

            // 添加注释（如果存在）
            String comment = comments.get(key);
            if (comment != null) {
                result.append(indent).append(comment);
            }

            // 添加键值对
            result.append(indent).append(key).append(":");

            if (entry.getValue() instanceof Map) {
                result.append("\n");
                dumpMapWithComments((Map<String, Object>) entry.getValue(),
                        comments,
                        indent + "  ",
                        result);
            } else {
                result.append(" ").append(entry.getValue()).append("\n");
            }
        }
    }
}
