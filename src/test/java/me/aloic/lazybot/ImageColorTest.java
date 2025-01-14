package me.aloic.lazybot;

import me.aloic.lazybot.util.CommonTool;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class ImageColorTest
{
    @Test
    public void calcColor() throws IOException
    {
        System.out.println(CommonTool.getDominantHSLWithBins(new File("X:\\Lazybot_working_dir\\osuFiles\\mapBG\\748836.jpg"),12));
    }
}
