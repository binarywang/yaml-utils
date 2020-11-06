package com.binarywang.utils.yaml;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Yaml工具类.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2020-11-05
 */
@Slf4j
@UtilityClass
public class YamlUtils {
    private static final String DOT = ".";
    private static final String DOT_REGEX = "[.]";

    /**
     * 读取配置值
     *
     * @param key         要读取值的key
     * @param inputStream 对应的yaml文本或文件输入流
     */
    public static <T> T readProperty(String key, InputStream inputStream) {
        Yaml yaml = new Yaml();
        final Map<String, Object> rootMap = yaml.load(inputStream);
        if (rootMap == null) {
            throw new IllegalArgumentException("文件内容为空，请核实！");
        }

        if (!key.contains(DOT)) {
            return (T) (rootMap.get(key));
        }

        Object current = rootMap;
        for (String k : key.split(DOT_REGEX)) {
            if (current instanceof Map) {
                if (k.endsWith("]")) {
                    String listName = StringUtils.substringBefore(k, "[");
                    int index = Integer.parseInt(StringUtils.substringAfter(k, "[").replace("]", ""));
                    current = ((List<Object>) ((Map<?, ?>) current).get(listName)).get(index);
                } else {
                    current = ((Map<?, ?>) current).get(k);
                }
            }
        }

        return (T) current;
    }

    /**
     * 增加新的配置
     *
     * @param key        键
     * @param value      值
     * @param yamlString 原始yaml内容字符串
     * @return 增加配置后的结果
     */
    public static String addProperty(String key, Object value, String yamlString) {
        return addProperty(key, value, new ByteArrayInputStream(yamlString.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 增加新的配置
     *
     * @param key         键
     * @param value       值
     * @param inputStream 输入流
     * @return 增加配置后的结果
     */
    public static String addProperty(String key, Object value, InputStream inputStream) {
        Yaml yaml = new Yaml();
        final Object loadObj = yaml.load(inputStream);
        if (loadObj instanceof String) {
            throw new IllegalArgumentException("yaml文件格式 有问题，请检查");
        }

        Map<String, Object> map = (Map<String, Object>) loadObj;

        if (map == null) {
            map = new LinkedHashMap<>(8);
        }

        if (!key.contains(DOT)) {
            map.put(key, value);
        } else {
            Object current = map;
            String[] split = key.split(DOT_REGEX);
            for (int i = 0, splitLength = split.length; i < splitLength; i++) {
                String k = split[i];
                if (i == splitLength - 1) {
                    ((Map) current).put(k, value);
                    break;
                }

                if (k.endsWith("]")) {
                    String listName = StringUtils.substringBefore(k, "[");
                    int index = Integer.parseInt(StringUtils.substringAfter(k, "[").replace("]", ""));
                    final Map<String, Object> parent = (Map<String, Object>) current;
                    List<Object> list = (List<Object>) parent.get(listName);
                    if (list == null) {
                        // list 不存在
                        list = new ArrayList<>(index + 1);
                        current = new LinkedHashMap<String, Object>(2);
                        list.add(current);
                        parent.put(listName, list);
                        continue;
                    }

                    if (list.size() < index + 1) {
                        // 列表长度不够，仅增加一个，忽略可能差很多的情况
                        current = new LinkedHashMap<String, Object>(2);
                        list.add(current);
                        continue;
                    }

                    current = list.get(index);
                } else {
                    current = ((Map<String, Object>) current).computeIfAbsent(k, k1 -> new LinkedHashMap<>(8));
                }
            }
        }

        final String result = yaml.dumpAsMap(map);
        // log.info("增加新属性后的yaml完整字符串：\n{}", result);
        return result;
    }

}
