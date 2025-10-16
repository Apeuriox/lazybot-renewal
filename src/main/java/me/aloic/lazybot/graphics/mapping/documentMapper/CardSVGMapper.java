package me.aloic.lazybot.graphics.mapping.documentMapper;

import me.aloic.lazybot.entity.vo.RetroGamerCardStats;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.graphics.mapping.LazybotSVGMapper;
import me.aloic.lazybot.graphics.template.SVGTemplateLoader;
import me.aloic.lazybot.graphics.util.ImageFilterUtil;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.io.File;

public class CardSVGMapper extends LazybotSVGMapper
{
    public static Document mapRetroStatsToGameboy(RetroGamerCardStats stats)
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("card/Gameboy");

        document.getElementById("name").setTextContent(stats.getName());
        try{
            document.getElementById("avatar").setAttributeNS(xlinkns,"xlink:href", ImageFilterUtil.toBase64DataUrl(
                    ImageFilterUtil.applyGameboyFilter(
                            ImageIO.read(new File(stats.getAvatarUrl())),2), "jpg"));
        }
        catch (Exception e){
            throw new LazybotRuntimeException("为图片施加像素化归一滤镜时失败");
        }
        return document;
    }
    public static Document mapRetroStatsToGameGadget(RetroGamerCardStats stats)
    {
        Document document = SVGTemplateLoader.loadSVGTemplate("card/GameGadget");
        document.getElementById("name").setTextContent(stats.getName());
        try{
            document.getElementById("avatar").setAttributeNS(xlinkns,"xlink:href", ImageFilterUtil.toBase64DataUrl(
                    ImageFilterUtil.applySimpleCRT(
                            ImageIO.read(new File(stats.getAvatarUrl())),2,2), "jpg"));
        }
        catch (Exception e){
            throw new LazybotRuntimeException("为图片施加CRT滤镜时失败");
        }
        return document;
    }
}
