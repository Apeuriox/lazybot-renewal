package me.aloic.lazybot.graphics.render;

import me.aloic.ResvgJNI;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.utils.SvgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class SVGRenderer
{
    private static final ResvgJNI.RenderOptions options;
    private static final ResvgJNI.Renderer renderer;
    private static final Logger logger = LoggerFactory.getLogger(SVGRenderer.class);

    static{
        options = new ResvgJNI.RenderOptions(ResourceMonitor.getResourcePath().toAbsolutePath()+"/static");
        options.LoadFontsDir(ResourceMonitor.getResourcePath().toAbsolutePath()+"/static/fonts");
        renderer = new ResvgJNI.Renderer(options);
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
            throw new LazybotRuntimeException("[Lazybot] 渲染成绩图时出错");
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
            result = renderer.RenderJpg(SvgUtil.documentToString(document),scale);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("[Lazybot] 渲染成绩图时出错");
        }
        logger.info("Render cost:{}ms", System.currentTimeMillis() - startingTime);
//        try{
//            System.out.println(SvgUtil.documentToString(document));
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        return result;
    }
    private static OutputStream convertByteArrayToOutputStream(byte[] byteArray) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            outputStream.write(byteArray);
        }
        catch (Exception e)
        {
            throw new LazybotRuntimeException("[Lazybot] Error convert Byte Array into Output Stream");
        }
        return outputStream;
    }
}
