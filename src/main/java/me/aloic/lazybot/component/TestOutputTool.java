package me.aloic.lazybot.component;

import com.mikuac.shiro.common.utils.MsgUtils;
import me.aloic.lazybot.entity.message.LazybotMessageWithImage;
import me.aloic.lazybot.util.CommonTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

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

    public void saveImageAndTextToLocal(byte[] imageByteArray, String content) {
        saveImageToLocal(imageByteArray, testPath, "lazybot-test-image.png");
        writeStringToFile(content, testPath, "lazybot-test-text.txt");
    }
    public void saveImageAndTextToLocal(LazybotMessageWithImage result) {
        if (result.getImage()==null)
        {
            writeStringToFile(result.getMessage());
            return;
        }
        saveImageAndTextToLocal(result.getImage(),result.getMessage());
    }
    public void saveImageAndTextToLocal(List<LazybotMessageWithImage> result) {
        if (!CommonTool.isEmpty(result))
        {
            StringBuilder builder = new StringBuilder();
            int i=0;
            for(LazybotMessageWithImage message:result)
            {
                builder.append(message.getMessage());
                if (message.getImage()!=null)
                {
                    saveImageToLocal(message.getImage(), testPath, "lazybot-test-image.png"+i);
                }
                i++;
            }
            writeStringToFile(builder.toString());
        }

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
