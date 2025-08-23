package me.aloic.lazybot.graphics.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageFilterUtil
{
    // Gameboy 调色板
    private static final Color[] GAMEBOY_PALETTE = {
            new Color(15, 56, 15),    // 深绿
            new Color(48, 98, 48),    // 中绿
            new Color(139, 172, 15),  // 浅绿
            new Color(155, 188, 15)   // 更浅
    };

    public static BufferedImage applyGameboyFilter(BufferedImage src, int pixelSize) {
        int width = src.getWidth();
        int height = src.getHeight();

        // 像素化
        BufferedImage scaledDown = new BufferedImage(width / pixelSize, height / pixelSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaledDown.createGraphics();
        g2d.drawImage(src, 0, 0, scaledDown.getWidth(), scaledDown.getHeight(), null);
        g2d.dispose();

        BufferedImage pixelated = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = pixelated.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.drawImage(scaledDown, 0, 0, width, height, null);
        g2d.dispose();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = pixelated.getRGB(x, y);
                Color c = new Color(rgb);

                int gray = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                Color nearest = findNearestPalette(gray);
                pixelated.setRGB(x, y, nearest.getRGB());
            }
        }

        return pixelated;
    }

    private static Color findNearestPalette(int gray) {
        if (gray < 64) return GAMEBOY_PALETTE[0];
        else if (gray < 128) return GAMEBOY_PALETTE[1];
        else if (gray < 192) return GAMEBOY_PALETTE[2];
        else return GAMEBOY_PALETTE[3];
    }

    public static BufferedImage applySimpleCRT(BufferedImage src, int rgbShift, int lineSpacing) {
        int width = src.getWidth();
        int height = src.getHeight();

        BufferedImage crt = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // 拆分 RGB 通道并偏移
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = src.getRGB(x, y);

                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                // 偏移: R 向左，B 向右
                int rX = Math.max(0, x - rgbShift);
                int bX = Math.min(width - 1, x + rgbShift);

                int gX = x; // G 不变

                int rPix = (src.getRGB(rX, y) >> 16) & 0xFF;
                int gPix = (src.getRGB(gX, y) >> 8) & 0xFF;
                int bPix = (src.getRGB(bX, y)) & 0xFF;

                int newRgb = (rPix << 16) | (gPix << 8) | bPix;
                crt.setRGB(x, y, newRgb);
            }
        }

        // 扫描线
        Graphics2D g2d = crt.createGraphics();
        g2d.setColor(new Color(0, 0, 0, 60)); // 半透明黑
        for (int y = 0; y < height; y += lineSpacing) {
            g2d.drawLine(0, y, width, y);
        }
        g2d.dispose();

        return crt;
    }
    public static String toBase64DataUrl(BufferedImage image, String formatName) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        return "data:image/" + formatName + ";base64," + base64;
    }

}
