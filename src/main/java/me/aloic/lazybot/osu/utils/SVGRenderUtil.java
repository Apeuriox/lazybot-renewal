package me.aloic.lazybot.osu.utils;

import me.aloic.ResvgJNI;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import me.aloic.lazybot.osu.dao.entity.vo.ScoreVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class SVGRenderUtil
{
    private static final ResvgJNI.RenderOptions options;
    private static final ResvgJNI.Renderer renderer;
    private static final Logger logger = LoggerFactory.getLogger(SVGRenderUtil.class);

    static{
        options = new ResvgJNI.RenderOptions(ResourceMonitor.getResourcePath().toAbsolutePath()+"/static");
        options.LoadFontsDir(ResourceMonitor.getResourcePath().toAbsolutePath()+"/static/fonts");
        renderer = new ResvgJNI.Renderer(options);
    }


    public static OutputStream renderScoreToImage(ScoreVO targetScore, int version, int[] primaryColor)
    {

        Document doc;
        if (version==0)
            doc = SvgUtil.getScorePanelDarkModeDoc(targetScore,primaryColor);
        else if (version==1)
            doc = SvgUtil.getScorePanelWhiteModeDoc(targetScore);
        else if (version==2)
            doc = SvgUtil.getScorePanelMaterialDesign(targetScore,primaryColor);
        else throw new LazybotRuntimeException("不支持的面板版本: " + version);
        return renderSVGToOutputstream(doc);
    }
    public static byte[] renderScoreToByteArray(ScoreVO targetScore, int version,int[] primaryColor)
    {
        Document doc;
        if (version==0)
            doc = SvgUtil.getScorePanelDarkModeDoc(targetScore,primaryColor);
        else if (version==1)
            doc = SvgUtil.getScorePanelWhiteModeDoc(targetScore);
        else if (version==2)
            doc = SvgUtil.getScorePanelMaterialDesign(targetScore,primaryColor);
        else throw new LazybotRuntimeException("不支持的面板版本: " + version);
        return renderSVGDocumentToByteArray(doc);
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
    public static byte[] renderSVGDocumentToByteArray(Document document, Integer scale)
    {
        long startingTime = System.currentTimeMillis();
        byte[] result;
        try{
            result = renderer.RenderJpg(SvgUtil.documentToString(document),scale);
        }
        catch (Exception e){
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("渲染成绩图时出错");
        }
        logger.info("Render cost:{}ms", System.currentTimeMillis() - startingTime);
        return result;
    }
    private static OutputStream convertByteArrayToOutputStream(byte[] byteArray) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try{
            outputStream.write(byteArray);
        }
        catch (Exception e)
        {
            throw new LazybotRuntimeException("Error convert Byte Array into Output Stream");
        }
        return outputStream;
    }
}
