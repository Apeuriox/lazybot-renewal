package me.aloic.lazybot.osu.utils;

import me.aloic.lazybot.exception.LazybotRuntimeException;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;

/**
 * Svg操作类
 * 说实话不知道怎么简化，而且看着是真头疼，
 * 很可惜只有Apache Batik可以修改/转化成png，
 * JFreeSvg有调查，的确是比Batik更高效，但是他不包含修改功能只有生成svg，
 * 所以只能用这个了，
 * 但是实际看看，根据score来修改svg实际上也只需要100ms左右，
 * 大部分时间消耗在带宽和，转码上面
 * **/

/* 2025/07/25
 * 哈哈先全部拆出去再说
 * */

public class SvgUtil
{
    private static final PNGTranscoder transcoder = new PNGTranscoder();
    private static  Transformer transformer;
    private static final Logger logger = LoggerFactory.getLogger(SvgUtil.class);

    static{
         transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_ALLOW_EXTERNAL_RESOURCES, Boolean.TRUE);
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e)
        {
            logger.error(e.getMessage());
            throw new LazybotRuntimeException("[Lazybot] Batik转换器初始化失败");
        }
    }


    public static String documentToString(Document doc) throws Exception {
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    public static InputStream documentToInputStream(Document doc) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(byteArrayOutputStream));
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    public static OutputStream documentToOutputStream(TranscoderInput input) {
        try {
            TranscoderOutput output = new TranscoderOutput(new ByteArrayOutputStream());
            transcoder.transcode(input, output);
            return output.getOutputStream();
        } catch (Exception e) {
            logger.error("error in batik transcoder: {}", e.getMessage());
        }
        return null;
    }

    public static void documentToExternalFile(TranscoderInput input, File outFile) throws IOException, TranscoderException {
        try (OutputStream os = Files.newOutputStream(outFile.toPath())) {
            TranscoderOutput output = new TranscoderOutput(os);
            transcoder.transcode(input, output);
        }
    }

    public static void documentToExternalFileResized(TranscoderInput input, File outFile) {
        transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 1200f);
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 11440f);
        try (OutputStream os = Files.newOutputStream(outFile.toPath())) {
            TranscoderOutput output = new TranscoderOutput(os);
            transcoder.transcode(input, output);
        }
        catch (Exception e) {
            logger.error("error in external file: {}", e.getMessage());
        }
        finally {
            transcoder.removeTranscodingHint(PNGTranscoder.KEY_WIDTH);
            transcoder.removeTranscodingHint(PNGTranscoder.KEY_HEIGHT);
        }
    }



}
