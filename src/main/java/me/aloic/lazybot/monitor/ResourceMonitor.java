package me.aloic.lazybot.monitor;

import me.aloic.lazybot.discord.config.DiscordBotRunner;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ResourceMonitor
{
    private static Path resourcePath;

    private static final Logger logger = LoggerFactory.getLogger(ResourceMonitor.class);

    public static void initResources()
    {
        logger.info("正在初始化静态资源");
        try{
            String workingDir = System.getenv("LAZYBOT_DIR");
            if (workingDir == null || workingDir.isEmpty()) {
                workingDir = String.valueOf(Files.createTempDirectory("lazybot_working_dir"));
            }
            File targetDir = new File(workingDir);
            if (!targetDir.exists() && !targetDir.mkdirs()) {
                throw new IOException("无法创建目标目录：" + workingDir);
            }
            resourcePath = Paths.get(targetDir.getAbsolutePath());
            createOsuDirectories(resourcePath);

            // 释放静态资源
            extractResources("static", resourcePath.resolve("static").toFile());
            logger.info("资源释放完成，路径：{}", targetDir.getAbsolutePath());
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("释放静态资源时出错: " + e.getMessage());
        }
    }
    /**
     * 从 JAR 包中提取资源到目标目录
     *
     * @param resourceDir 静态资源的 JAR 内路径
     * @param targetDir   目标释放目录
     * @throws IOException 如果提取失败
     */
    private static void extractResources(String resourceDir, File targetDir) throws IOException {
        ApplicationHome home = new ApplicationHome(DiscordBotRunner.class);
        File source = home.getSource();
        if (source != null && source.isFile() && source.getName().endsWith(".jar")) {
            logger.info("正在从 JAR 文件中提取资源");
            try (JarFile jar = new JarFile(source)) {
                Enumeration<JarEntry> entries = jar.entries();
                resourceDir = "BOOT-INF/classes/" + resourceDir;
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(resourceDir + "/")) {
                        String relativePath = entry.getName().substring(resourceDir.length() + 1);
                        File targetFile = new File(targetDir, relativePath);
                        if (entry.isDirectory()) {
                            if (!targetFile.exists() && !targetFile.mkdirs()) {
                                throw new IOException("无法创建目录：" + targetFile.getAbsolutePath());
                            }
                        } else {
                            logger.info("正在提取资源：{}", targetFile.getAbsolutePath());
                            copyResourceFromJar(jar, entry, targetFile);
                        }
                    }
                    else {
                        logger.info("跳过资源：{}", entry.getName());
                    }
                }
            }
        } else {
            // 如果不是 JAR 文件，尝试从 classpath 中获取资源目录
            logger.info("正在从 classpath 中提取资源");
            URL resourceUrl = DiscordBotRunner.class.getClassLoader().getResource(resourceDir);
            if (resourceUrl != null) {
                File resourceFolder = new File(resourceUrl.getFile());
                copyResourceFolder(resourceFolder, targetDir);
            }
            else {
                logger.warn("未能在 classpath 中找到资源目录：{}", resourceDir);
            }
        }
    }

    /**
     * 从 JAR 中复制单个资源文件
     *
     * @param jar    JAR 文件对象
     * @param entry      资源文件的 JAR Entry
     * @param targetFile 目标文件路径
     * @throws IOException 如果复制失败
     */
    private static void copyResourceFromJar(JarFile jar, JarEntry entry, File targetFile) throws IOException {
        // 确保目标文件的父目录存在
        File parentDir = targetFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("无法创建目标目录：" + parentDir.getAbsolutePath());
        }

        // 从 JAR 文件中读取资源并写入目标文件
        try (InputStream in = jar.getInputStream(entry);
             OutputStream out = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        logger.info("资源已提取：{}", targetFile.getAbsolutePath());
    }

    /**
     * 从文件系统复制资源文件夹
     *
     * @param sourceFolder 源文件夹
     * @param targetFolder 目标文件夹
     * @throws IOException 如果复制失败
     */
    private static void copyResourceFolder(File sourceFolder, File targetFolder) throws IOException {
        if (!sourceFolder.isDirectory()) {
            throw new IllegalArgumentException("源目录无效：" + sourceFolder.getAbsolutePath());
        }
        for (File file : Objects.requireNonNull(sourceFolder.listFiles())) {
            File targetFile = new File(targetFolder, file.getName());
            if (file.isDirectory()) {
                copyResourceFolder(file, targetFile);
            } else {
                File parentDir = targetFile.getParentFile();
                if (!parentDir.exists() && !parentDir.mkdirs()) {
                    throw new IOException("无法创建目标目录：" + parentDir.getAbsolutePath());
                }
                Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
    /**
     * 获取工作目录
     *
     * @return 工作目录的路径
     */
    public static Path getResourcePath() {
        if (resourcePath == null) {
            throw new IllegalStateException("工作目录未初始化！");
        }
        return resourcePath;
    }

    /**
     * 在工作目录下创建 osuFiles 目录及其子目录 playerAvatar 和 mapBG
     *
     * @param workingDir 工作目录
     * @throws IOException 如果目录创建失败
     */
    public static void createOsuDirectories(Path workingDir) throws IOException {
        Path osuFilesDir = workingDir.resolve("osuFiles");
        Path playerAvatarDir = osuFilesDir.resolve("playerAvatar");
        Path mapBGDir = osuFilesDir.resolve("mapBG");
        Path playerCustomizationDir = osuFilesDir.resolve("playerCustomization");
        Path profileDir = playerCustomizationDir.resolve("profile");
        Path staticDir = workingDir.resolve("static");
        Path assetsDir = staticDir.resolve("assets");
        Path osuResDir = assetsDir.resolve("osuResources");
        Path fonts = staticDir.resolve("fonts");

        createDirectoryIfNotExists(osuFilesDir);
        createDirectoryIfNotExists(playerAvatarDir);
        createDirectoryIfNotExists(mapBGDir);
        createDirectoryIfNotExists(staticDir);
        createDirectoryIfNotExists(playerCustomizationDir);
        createDirectoryIfNotExists(profileDir);
        createDirectoryIfNotExists(assetsDir);
        createDirectoryIfNotExists(osuResDir);
        createDirectoryIfNotExists(fonts);
    }

    /**
     * 检查并创建目录（如果不存在）
     *
     * @param dirPath 要检查和创建的目录路径
     * @throws IOException 如果目录创建失败
     */
    private static void createDirectoryIfNotExists(Path dirPath) throws IOException {
        File dir = dirPath.toFile();
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new IOException("无法创建目录：" + dir.getAbsolutePath());
            }
            logger.info("目录已创建：{}", dir.getAbsolutePath());
        } else {
            logger.info("目录已存在：{}", dir.getAbsolutePath());
        }
    }



    //for spring 2.x using, cuz Spring 3.x added nested:// protocol
    private static File jarPathVerifier(String jarPath)
    {
        try {
            // 解码 URL 路径
            String decodedPath = URLDecoder.decode(jarPath, "UTF-8");

            // 去掉 file: 前缀
            if (decodedPath.startsWith("file:/")) {
                decodedPath = decodedPath.substring("file:".length());
            }

            if (File.separatorChar == '\\' && decodedPath.startsWith("/")) {
                decodedPath = decodedPath.substring(1);
            }

            // 去掉 JAR 内部路径部分（如果存在）
            int jarSeparatorIndex = decodedPath.indexOf("!");
            if (jarSeparatorIndex > 0) {
                decodedPath = decodedPath.substring(0, jarSeparatorIndex);
            }

            // 转换为 File 对象
            File jarFile = new File(decodedPath);

            // 检查文件是否存在
            if (jarFile.isFile() && jarFile.getName().endsWith(".jar")) {
                logger.info("JAR 文件路径：{}", jarFile.getAbsolutePath());
            } else {
                logger.info("不是有效的 JAR 文件路径：{}", decodedPath);
            }
            return jarFile;
        }
        catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("Decode URL failed", e);
        }
    }
}
