package me.aloic.lazybot.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class TestOutputTool
{
    @Value("${lazybot.test.enabled}")
    private Boolean testEnabled;
    @Value("${lazybot.test.path}")
    private String testPath;
    private static final Logger logger = LoggerFactory.getLogger(TestOutputTool.class);


    public void saveImageToLocal(byte[] imageByteArray) {
        saveImageToLocal(imageByteArray, testPath, "lazybot-test-image.png");
    }

    public void saveImageToLocal(byte[] imageByteArray,String filePath, String fileName) {
        if (!testEnabled) return;
        try {
            Path path = Paths.get(filePath, fileName);
            Files.write(path, imageByteArray);
            logger.info("成功保存图片到{}.", path);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    public void writeStringToFile(String content) {
        writeStringToFile(content, testPath, "lazybot-test-text.txt");
    }

    public void writeStringToFile(String content, String filePath, String fileName) {
        if (!testEnabled) return;
        try {
            Path path = Paths.get(filePath,fileName);
            Files.writeString(path, content);
            logger.info("成功写入字符串到{}.", path);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


}
