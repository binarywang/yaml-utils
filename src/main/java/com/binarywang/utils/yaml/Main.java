package com.binarywang.utils.yaml;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

/**
 * 入口.
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 * @date 2020-11-05
 */
@Slf4j
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        if (args.length < 3) {
            log.error("参数个数不对，请核实");
            return;
        }

        String command = args[0];
        switch (command.toLowerCase()) {
            case "read": {
                final Object value = YamlUtils.readProperty(args[2], new FileInputStream(args[1]));
                if (value == null) {
                    log.error("没有在文件中找到该属性");
                    break;
                }
                log.info(value.toString());
                break;
            }
            case "add": {
                if (args.length < 4) {
                    log.error("ADD时的参数个数不对！");
                    return;
                }

                final File file = new File(args[1]);
                final String result = YamlUtils.addProperty(args[2], args[3], new FileInputStream(file));
                FileUtils.write(file, result, StandardCharsets.UTF_8);
                log.info("新属性添加成功！");
                break;
            }

            default:
        }
    }
}
