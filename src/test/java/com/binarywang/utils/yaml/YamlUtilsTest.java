package com.binarywang.utils.yaml;

import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * 单元测试.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2020-11-05
 */
public class YamlUtilsTest {
    private final String filePath = "test.yml";

    @Before
    public void init() {

    }

    @After
    public void tearDown() {
    }

    @SneakyThrows
    @Test
    public void readProperties() {
        assertThat(YamlUtils.<String>readProperty("single",
                ClassLoader.getSystemResourceAsStream(filePath))).isEqualTo("single");

        assertThat(YamlUtils.<String>readProperty("spring.datasource.username",
                ClassLoader.getSystemResourceAsStream(filePath))).isEqualTo("username");

        assertThat(YamlUtils.<String>readProperty("spring.datasource.password",
                ClassLoader.getSystemResourceAsStream(filePath))).isEqualTo("1234");

        assertThat(YamlUtils.<String>readProperty("wx.mp.configs[0].appid",
                ClassLoader.getSystemResourceAsStream(filePath))).isEqualTo("11");

        assertThat(YamlUtils.<String>readProperty("wx.mp.configs[1].appsecret",
                ClassLoader.getSystemResourceAsStream(filePath))).isEqualTo("secret");

        assertThat(YamlUtils.<Integer>readProperty("wx.mp.configs[1].index",
                ClassLoader.getSystemResourceAsStream(filePath))).isEqualTo(1);

        assertThat(YamlUtils.<Boolean>readProperty("wx.mp.configs[1].b",
                ClassLoader.getSystemResourceAsStream(filePath))).isTrue();
    }

    @SneakyThrows
    @Test
    public void addProperties() {
        assertThat(YamlUtils.addProperty("key", "value", new ByteArrayInputStream("key: originalValue".getBytes())))
                .contains("key:").contains("value");

        assertThat(YamlUtils.addProperty("key", "value", "key: originalValue"))
                .contains("key:").contains("value");

        assertThat(YamlUtils.addProperty("abc.def.hij", true, ""))
                .contains("abc:").contains("def:").contains("hij:").contains("true");

        assertThat(YamlUtils.addProperty("first.secondArray[0].key", true, ""))
                .contains("first:").contains("secondArray:").contains("- key:").contains("true");

         assertThat(YamlUtils.addProperty("abc.configs[1].appid", "appid", ""))
                .contains("abc:").contains("configs:").contains("- appid:").contains("appid");
    }

}
