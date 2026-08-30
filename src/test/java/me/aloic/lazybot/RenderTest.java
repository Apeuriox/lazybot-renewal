//package me.aloic.lazybot;
//
////import io.github.humbleui.skija.*;
////import io.github.humbleui.skija.svg.SVGDOM;
////import me.lazychildren.util.CommonTool;
//
//import me.aloic.ResvgJNI;
//import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
//import org.apache.batik.transcoder.*;
//import org.apache.batik.transcoder.image.PNGTranscoder;
//import org.apache.batik.util.XMLResourceDescriptor;
//import org.junit.jupiter.api.Test;
//import org.w3c.dom.Document;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.net.URI;
//
// this file is for documentation purpose
//public class RenderTest
//{
//    /*
//     * This Class aims to optimize the rendering speed of the SVG to PNG process
//     * Tested on: 2024-11 by Aloic
//     * SVG File ViewBox: 1920x1000
//     * Render Size: 1920x1000
//     *
//     * Result:
//     *  Apache Batik: 1.Simple Background: Around 1800ms w/ Blur filter
//     *                                     Around 1400ms w/o Blur filter
//     *                2.Complex Background: Around 4800ms w/ Blur filter
//     *                                      Around 2000ms w/o Blur filter
//     *
//     *   TwelveMonkeys ImageIO extension: Failed (Blocking external resources)
//     *   Skija: Failed (Only supports a few SVG elements)
//     *
//     *   JSVG: 1.Simple Background: Around 900ms w/ Blur filter (Barely Usable)
//     *                              (quality was fucked while Fonts were not properly loaded and rendered)
//     *         2.Complex Background: NOT TESTED
//     *
//     *   Resvg: 1.Simple Background: Around 1800ms w/ Blur filter
//     *                              (It's written in Rust, I'm using Java ProcessBuilder to call it which led to a massive performance drop)
//     *                              (according to Zh_Jk, it only requires avg 350ms to render w/ Blur filter. Calling with Java took 900ms. I'll investigate later)
//     *          2.Complex Background: Around 1800ms w/ Blur filter (Yes it's almost the same while other lib took significantly longer)
//     *                                (After further investigation, it seems that the performance drop is due to 'SVG Parse' process.)
//     *                                (I've discussed with Zh_jk. 'SVG Parse' includes Font file loading, I highly doubt the performance drop on my
//     * The computer was caused by CPU L3 cache. Currently, I'm using 5800H with 16MB of L3 cache, while Zh_jk is using 3600X with 32MB of L3 cache)
//     *
//     *                                (We've made some tests around it, it's most likely not caused by CPU L3 cache. Detailed info can be found down below)
//     *
//     *                               Note: Resvg.exe commands:
//     *                                     Usage: resvg.exe [OPTIONS] --working-dir <WORKING_DIR> --font-dir <FONT_DIR> [INPUT] [OUTPUT]
//     *                                     Options:
//     *                                          -w, --working-dir <WORKING_DIR> working directory
//     *                                          -f, --font-dir <FONT_DIR> font directory
//     *                                          -l, --load-system-fonts  load system fonts
//     *                                          -s, --scale <SCALE> scale factor [default: 1]
//     *                                          -h, --help print help
//     *
//     *                                     Call it via Java ProcessBuilder.
//     *                                     ATTENTION: After you call resvg.exe, you must at least read process.getInputStream() once, or it will get completely stuck.
//     *
//     *
//     *
//     *          EXTRA: L3 Cache Test
//     *                1.AMD R7-5800H (16MB L3 Cache): 270ms
//     *                2.AMD R5-5600X (32MB L3 Cache): 140ms
//     *                3.Intel i5-12600KF (20MB L3 Cache): 140ms
//     *                4.AMD R5-3600X (32MB L3 Cache): 200ms
//     *
//     *   other solutions:
//     *   Web browser (Not implemented yet)
//     *
//     * */
//
//    //ALL FUCKED
//    @Test
//    public void testRenderBatik() throws IOException, TranscoderException
//    {
//        URI inputUri = new File("src/main/resources/static/scorePanelDarkmode.svg").toURI();
//        Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
//        long startingTime = System.currentTimeMillis();
//        TranscoderInput input = new TranscoderInput(doc);
//        PNGTranscoder transcoder = new PNGTranscoder();
//        try
//        {
//            TranscoderOutput output = new TranscoderOutput(new ByteArrayOutputStream());
//            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_ALLOW_EXTERNAL_RESOURCES, Boolean.TRUE);
//            transcoder.transcode(input, output);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("Batik: Render cost:" + (System.currentTimeMillis() - startingTime) + "ms");
//    }
//    @Test
//    public void testRenderJavaImageIO() throws IOException, TranscoderException
//    {
//        System.setProperty("org.apache.batik.transcoder.TranscodingHints.KEY_XML_PARSER_VALIDATING", "false");
//
//        // Set the system property for KEY_EXTG_STATE
//        System.setProperty("org.apache.batik.transcoder.TranscodingHints.KEY_EXTG_STATE", "true");
//
//        long startingTime = System.currentTimeMillis();
//        BufferedImage input_image = ImageIO.read(new File("src/main/resources/static/scorePanelDarkmode.svg"));
//        File outputfile = new File("imageio_png_output.png");
//        ImageIO.write(input_image, "PNG", outputfile);
//
//        System.out.println("Render cost(dark mode):" + (System.currentTimeMillis() - startingTime) + "ms");
//    }
//
////    @Test
////    public void testRenderSkija() throws IOException
////    {
////        String svgFilePath = "src/main/resources/static/scorePanelDarkmode.svg"; // 替换为你的 SVG 文件路径
////        String outputImagePath = "output.png"; // 输出的 PNG 文件路径
////
////        // 加载 SVG 文件
////        try (Data data = Data.makeFromFileName(svgFilePath)) {
////            SVGDOM svgDom = new SVGDOM(data);
////
////
////            Surface surface = Surface.makeRasterN32Premul(1920, 1000);
////            Canvas canvas = surface.getCanvas();
////            canvas.clear(0xFFFFFF00); // 设置白色背景
////
////            // 渲染 SVG
////            svgDom.render(canvas);
////
////            // 保存渲染结果为 PNG
////            try (Image image = surface.makeImageSnapshot()) {
////                Data pngData = image.encodeToData(EncodedImageFormat.PNG);
////                if (pngData != null) {
////                    Files.write(java.nio.file.Paths.get(outputImagePath), pngData.getBytes());
////                    System.out.println("SVG 渲染成功，已保存到: " + outputImagePath);
////                }
////            }
////        } catch (IOException e) {
////            System.err.println("读取或保存文件时出错: " + e.getMessage());
////        } catch (Exception e) {
////            System.err.println("渲染 SVG 时出错: " + e.getMessage());
////        }
////    }
////    @Test
////    public void testRenderSkija2() throws IOException
////    {
////        Surface surface = Surface.makeNull(1920, 1080);
////        byte[] data;
////        try (surface) {
////            Canvas canvas = surface.getCanvas();
////            SVGDOM svg = new SVGDOM(Data.makeFromBytes(Files.readAllBytes(Paths.get("src/main/resources/static/scorePanelDarkmode.svg"))));
////            svg.render(canvas);
////        }
////        try (Image image = surface.makeImageSnapshot()) {
////            data = EncoderPNG.encode(image).getBytes();
////        }
////        Files.write(Paths.get("output.png"), data);
////    }
//
//
//    //		<dependency>
//    //			<groupId>com.github.weisj</groupId>
//    //			<artifactId>jsvg</artifactId>
//    //			<version>1.6.1</version>
//    //		</dependency>
//
////    @Test
////    public void testRenderJSVG() throws Exception
////    {
////        //注意这里是URI的svg，因为使用这个库，其中的xlink:href需要使用绝对路径的URI，也就是必须file:/开头的绝对路径，而Batik是支持相对路径的
////        URL svgUrl = new File("X:/lazybot-spring/src/main/resources/static/scorePanelDarkmode-URI.svg").toURI().toURL();
////        System.out.println(svgUrl);
////        try {
////            long startingTime = System.currentTimeMillis();
////            SVGLoader svgLoader=new SVGLoader();
////            SVGDocument svgDocument = svgLoader.load(svgUrl);
////
////            BufferedImage image = new BufferedImage(
////                    1920,
////                    1080,
////                    BufferedImage.TYPE_INT_ARGB
////            );
////
////            Graphics2D g = image.createGraphics();
////            svgDocument.render(null,g);
////            g.dispose();
////
////            ImageIO.write(image, "PNG", new File("output.png"));
////            System.out.println("Render cost:" + (System.currentTimeMillis() - startingTime) + "ms");
////        } catch (IOException e) {
////            System.err.println(e.getMessage());
////        }
////    }
//    @Test
//    public void testRenderResvg() throws Exception
//    {
//        ProcessBuilder processBuilder=new ProcessBuilder("X:\\lazybot-spring\\src\\main\\resources\\resvg-test-with-stopwatch.exe",
//                "-w","X:\\lazybot-spring\\src\\main\\resources\\static",
//                "-f","X:\\lazybot-spring\\src\\main\\resources\\static/fonts",
//                "X:\\lazybot-spring\\src\\main\\resources\\static/scorePaneLDarkmode.svg",
//                "X:\\lazybot-spring\\src\\main\\resources\\output.png");
//        processBuilder.redirectErrorStream(true);
//        try{
//            long startingTime = System.currentTimeMillis();
//            Process process = processBuilder.start();
//            int exitCode=process.waitFor();
//            System.out.println("Render cost:" + (System.currentTimeMillis() - startingTime) + "ms");
//            System.out.println("Exit code:"+exitCode);
//            byte[] err = process.getErrorStream().readAllBytes();
//            System.out.println(new String(err));
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void testRenderResvgWithFileStream() throws Exception {
//        ProcessBuilder processBuilder = new ProcessBuilder(
//                "X:\\lazybot-spring\\src\\main\\resources\\resvg-test-with-stopwatch.exe",
//                "-w", "X:\\lazybot-spring\\src\\main\\resources\\static",
//                "-f", "X:\\lazybot-spring\\src\\main\\resources\\static/fonts"
//        );
//        processBuilder.redirectErrorStream(true);
//
//        try {
//            long startingTime = System.currentTimeMillis();
//            Process process = processBuilder.start();
//
//            try (OutputStream os = process.getOutputStream();
//                 OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
//                 FileReader fr = new FileReader("X:\\lazybot-spring\\src\\main\\resources\\static/scorePaneLDarkmode.svg")) {
//
//                try (BufferedReader br = new BufferedReader(fr)) {
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        writer.write(line);
//                        writer.write("\n");
//                    }
//                }
//                writer.flush();
//            }
//            System.out.println("Loading cost: " + (System.currentTimeMillis() - startingTime) + "ms");
//            long startingTime2 = System.currentTimeMillis();
//
//            try (InputStream is = process.getInputStream();
//                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//
//                }
//            }
//
//            // 等待子进程结束
//            int exitCode = process.waitFor();
//            System.out.println("Render cost: " + (System.currentTimeMillis() - startingTime2) + "ms");
//            System.out.println("Exit code:"+exitCode);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void testRenderResvgZHJK() throws Exception
//    {
//        String inputFilePath = "X:\\lazybot-spring\\src\\main\\resources\\static/scorePanelDarkmode.svg";
//        String outputFilePath = "output.png";
//        try {
//
//            long start = System.currentTimeMillis();
//            ProcessBuilder processBuilder = new ProcessBuilder("X:\\lazybot-spring\\src\\main\\resources\\resvg-test-with-stopwatch.exe",
//                    "-w", "X:\\lazybot-spring\\src\\main\\resources\\static",
//                    "-f", "X:\\lazybot-spring\\src\\main\\resources\\static/fonts");
//            // 启动进程
//            Process process = processBuilder.start();
//            try (FileInputStream fis = new FileInputStream(inputFilePath);
//                 OutputStream os = process.getOutputStream()) {
//
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                // 读取文件内容并写入进程的标准输入流
//                while ((bytesRead = fis.read(buffer)) != -1) {
//                    os.write(buffer, 0, bytesRead);
//                }
//                os.flush(); // 确保数据写入进程
//            }
//
//            byte[] result = process.getInputStream().readAllBytes();
//            // write file
//            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
//                fos.write(result);
//            }
//
//            int exitCode = process.waitFor();
//            System.out.println("Process exited with code: " + exitCode);
//
//            long end = System.currentTimeMillis();
//            System.out.println("Time cost: " + (end - start) + " ms");
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void testRenderResvgZHJK2() throws Exception
//    {
//        String inputFilePath = "src\\main\\resources\\static/scorePanelDarkmode.svg";
//        String outputFilePath = "output.png";
//
//        try {
//            long start = System.currentTimeMillis();
//            ProcessBuilder processBuilder = new ProcessBuilder("src\\main\\resources\\resvg-test-with-stopwatch.exe",
//                    "-w", "src\\main\\resources\\static",
//                    "-f", "src\\main\\resources\\static/fonts");
//            Process process = processBuilder.start();
//            try (FileInputStream fis = new FileInputStream(inputFilePath);
//                 OutputStream os = process.getOutputStream()) {
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                // 读取文件内容并写入进程的标准输入流
//                while ((bytesRead = fis.read(buffer)) != -1) {
//                    os.write(buffer, 0, bytesRead);
//                }
//                os.flush(); // 确保数据写入进程
//            }
//
//            byte[] result = process.getInputStream().readAllBytes();
//            byte[] err = process.getErrorStream().readAllBytes();
//
//            // 确保进程执行完毕
//            int exitCode = process.waitFor();
//            if (exitCode != 0) {
//                System.out.println("Process exited with code: " + exitCode);
//            }
//            long end = System.currentTimeMillis();
//            System.out.println("Total Time cost: " + (end - start) + " ms");
//            System.out.println(new String(err));
//            try (FileOutputStream fos = new FileOutputStream(new File(outputFilePath))) {
//                fos.write(result);
//                fos.flush();
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void testRenderViaDocumentInputStream() throws Exception
//    {
//        String outputFilePath = "output.png";
//        URI inputUri = new File("src/main/resources/static/scorePanelDarkmode.svg").toURI();
//        Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
//        InputStream result = SVGRenderCLIUtil.renderReaderToPNGbyResvg(doc);
//        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
//            fos.write(result.readAllBytes());
//            fos.flush();
//        }
//    }
//
//
//    @Test
//    public void testRenderReSVGJNI() throws Exception
//    {
//        ResvgJNI.RenderOptions options = new ResvgJNI.RenderOptions("X:\\lazybot-spring\\src\\main\\resources\\static");
//        options.LoadFontsDir("X:\\lazybot-spring\\src\\main\\resources\\static\\fonts");
//        ResvgJNI.Renderer renderer = new ResvgJNI.Renderer(options);
//
//        URI inputUri = new File("src/main/resources/static/scorePanelDarkmode.svg").toURI();
//        Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
//        String outputFilePath = "output.png"; // 存储外部进程输出的文件路径
//
//        long start = System.currentTimeMillis();
//
//        try {
//            byte[] data = renderer.RenderPng(SvgUtil.documentToString(doc));
//            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
//                fos.write(data);
//                fos.flush();
//            }
//        } catch (Exception e)
//        {
//            System.out.println(e);
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("Total Time cost: " + (end - start) + " ms");
//    }
//
//    @Test
//    public void testRenderResvgWithString() throws Exception {
//        ProcessBuilder processBuilder = new ProcessBuilder(
//                "X:\\lazybot-spring\\src\\main\\resources\\resvg-test-with-stopwatch.exe",
//                "-w", "X:\\lazybot-spring\\src\\main\\resources\\static",
//                "-f", "X:\\lazybot-spring\\src\\main\\resources\\static/fonts"
//        );
//        URI inputUri = new File("src/main/resources/static/scorePanelDarkmode.svg").toURI();
//        Document doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createDocument(inputUri.toString());
//        try {
//            long startingTime = System.currentTimeMillis();
//            Process process = processBuilder.start();
//            try (OutputStream os = process.getOutputStream();
//                 OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
//                 Reader reader = new StringReader(SvgUtil.documentToString(doc))) {
//
//                try (BufferedReader br = new BufferedReader(reader)) {
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        writer.write(line);
//                        writer.write("\n");
//                    }
//                }
//                writer.flush();
//            }
//            System.out.println("Loading cost: " + (System.currentTimeMillis() - startingTime) + "ms");
//            long startingTime2 = System.currentTimeMillis();
//
//            byte[] result = process.getInputStream().readAllBytes();
//            int exitCode = process.waitFor();
//            System.out.println("Render cost: " + (System.currentTimeMillis() - startingTime2) + "ms");
//            System.out.println("Exit code:"+exitCode);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
