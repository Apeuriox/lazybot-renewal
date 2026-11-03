package me.aloic.lazybot.graphics.render;

import me.aloic.ResvgJNI;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.utils.SvgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SVGRenderer
{
    private static final ResvgJNI.RenderOptions options;
    private static final ResvgJNI.Renderer renderer;
    private static final Logger logger = LoggerFactory.getLogger(SVGRenderer.class);
    // JNI 会把虚拟线程钉在载体上。渲染改到固定数量的平台线程，命令线程只等待。
    private static final ExecutorService RENDER_POOL = Executors.newFixedThreadPool(
            Math.max(1, Runtime.getRuntime().availableProcessors()),
            Thread.ofPlatform().name("lazybot-render-", 0).factory());

    static{
        options = new ResvgJNI.RenderOptions(ResourceMonitor.getResourcePath().toAbsolutePath()+"/static");
        options.LoadFontsDir(ResourceMonitor.getResourcePath().toAbsolutePath()+"/static/fonts");
        renderer = new ResvgJNI.Renderer(options);
        Runtime.getRuntime().addShutdownHook(new Thread(RENDER_POOL::shutdown, "lazybot-render-shutdown"));
    }


    public static OutputStream renderSVGToOutputstream(Document document)
    {
        long startingTime = System.currentTimeMillis();
        byte[] result;
        try{
            result=renderSVGDocumentToByteArray(document);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("渲染成绩图时出错");
        }
        logger.info("Render cost:{}ms", System.currentTimeMillis() - startingTime);
        return convertByteArrayToOutputStream(result);
    }
    public static byte[] renderSVGDocumentToByteArray(Document document)
    {
       return renderSVGDocumentToByteArray(document, 1);
    }
    public static byte[] renderSVGDocumentToByteArray(Document document, float scale)
    {
        long startingTime = System.currentTimeMillis();
        byte[] result;
        try{
            result = renderJpg(SvgUtil.documentToString(document), scale);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("渲染成绩图时出错: "+e.getMessage());
        }
        logger.info("Render cost:{}ms", System.currentTimeMillis() - startingTime);

//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("X:\\lazybot-output\\test.svg"))) {
//            writer.write(SvgUtil.documentToString(document));
//            logger.info("成功写入字符串到{}.", "X:\\lazybot-output\\test.svg");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        return result;
    }
    public static byte[] renderSVGDocumentToByteArray(String document)
    {
        return renderSVGDocumentToByteArray(document,1f);
    }
    public static byte[] renderSVGDocumentToByteArrayPNG(String document)
    {
        return renderSVGDocumentToByteArrayPNG(document,1f);
    }

    public static byte[] renderSVGDocumentToByteArray(String document, float scale)
    {
        long startingTime = System.currentTimeMillis();
        byte[] result;
        try{
            result = renderJpg(document, scale);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("渲染成绩图时出错: "+e.getMessage());
        }
//        logger.info("Render to JPEG cost:{}ms", System.currentTimeMillis() - startingTime);
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("X:\\lazybot-output\\test.svg"))) {
//            writer.write(document);
//            logger.info("成功写入字符串到{}.", "X:\\lazybot-output\\test.svg");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        return result;
    }
    public static byte[] renderSVGDocumentToByteArrayPNG(String document, float scale)
    {
        long startingTime = System.currentTimeMillis();
        byte[] result;
        try{
            result = renderPng(document, scale);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("渲染成绩图时出错: "+e.getMessage());
        }
        logger.info("Render to JPEG cost:{}ms", System.currentTimeMillis() - startingTime);


        return result;
    }
    public static byte[] renderSVGDocumentToByteArrayPNG(Document document, float scale)
    {
        long startingTime = System.currentTimeMillis();
        byte[] result;
        try{
            result = renderPng(SvgUtil.documentToString(document), scale);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("渲染成绩图时出错");
        }
        logger.info("Render cost:{}ms", System.currentTimeMillis() - startingTime);

//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("X:\\lazybot-output\\test.svg"))) {
//            writer.write(SvgUtil.documentToString(document));
//            logger.info("成功写入字符串到{}.", "X:\\lazybot-output\\test.svg");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }

        return result;
    }
    private static byte[] renderJpg(String svg, float scale)
    {
        return renderOnPool(() -> renderer.RenderJpg(svg, scale));
    }

    private static byte[] renderPng(String svg, float scale)
    {
        return renderOnPool(() -> renderer.RenderPng(svg, scale));
    }

    private static byte[] renderOnPool(Callable<byte[]> task)
    {
        try {
            return RENDER_POOL.submit(task).get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LazybotRuntimeException("渲染成绩图时出错: interrupted");
        }
        catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new LazybotRuntimeException(cause == null ? e.getMessage() : cause.getMessage());
        }
    }

    private static OutputStream convertByteArrayToOutputStream(byte[] byteArray) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            outputStream.write(byteArray);
        }
        catch (Exception e)
        {
            throw new LazybotRuntimeException("将Byte Array转换成Output Stream时失败");
        }
        return outputStream;
    }
}
