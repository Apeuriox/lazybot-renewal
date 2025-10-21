package me.aloic.lazybot.util;

import me.aloic.lazybot.monitor.ResourceMonitor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BadgeLoader
{
    public static byte[] loadBadgeImage(Integer id) throws IOException
    {
        Path filePath = ResourceMonitor.getResourcePath().resolve("static/" + id + ".png");
        File image = filePath.toFile();
        if (!image.exists()) {
            return Files.readAllBytes(Paths.get(filePath.toUri()));
        }
        return null;
    }
}
