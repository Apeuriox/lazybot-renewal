package me.aloic.lazybot.graphics.template;

import lombok.extern.slf4j.Slf4j;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import me.aloic.lazybot.monitor.ResourceMonitor;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;

import java.nio.file.Path;

@Slf4j
public class SVGTemplateLoader
{
    public static Document loadSVGTemplate(String templateName)
    {
        try{
            Path filePath = ResourceMonitor.getResourcePath().resolve("static/" + templateName + ".svg");
           return new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(filePath.toFile().toURI().toString());
        }
        catch (Exception e)
        {
            log.error("SVG模板载入失败", e);
            throw new LazybotRuntimeException("[Lazybot] SVG模板载入失败");
        }
    }
}
