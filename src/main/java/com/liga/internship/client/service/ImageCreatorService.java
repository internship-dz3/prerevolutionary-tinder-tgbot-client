package com.liga.internship.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageCreatorService {
    private final TextService textService;

    @Value("${message.background}")
    private Resource resource;

    public File getImageWithTextFile(String text) throws IOException {
        BufferedImage bufferedImage = getImageWithText(text);
        Files.createDirectories(Paths.get("temp"));
        File trend = new File("temp/image.jpg");
        ImageIO.write(bufferedImage, "jpg", trend);
        return trend;
    }

    private BufferedImage getImageWithText(String text) {
        BufferedImage image = null;
        try {
            File imageFile = resource.getFile();
            image = ImageIO.read(imageFile);
            Font baseFont = new Font("Old Standard TT", Font.BOLD, 18);
            Graphics graphics = image.getGraphics();
            //центруем
            FontMetrics metrics = graphics.getFontMetrics(baseFont);
            int positionX = 0;
            int positionY = metrics.getAscent();
            FontMetrics ruler = graphics.getFontMetrics(baseFont);
            GlyphVector vector = baseFont.createGlyphVector(ruler.getFontRenderContext(), text);
            Shape outline = vector.getOutline(0, 0);
            double expectedWidth = outline.getBounds().getWidth();
            double expectedHeight = outline.getBounds().getHeight();
            boolean textFits = image.getWidth() >= expectedWidth && image.getHeight() >= expectedHeight;
            if (!textFits) {
                positionX = (image.getWidth() - metrics.stringWidth(text)) / 2;
                positionY = (image.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                double widthBasedFontSize = (baseFont.getSize2D() * image.getWidth()) / expectedWidth;
                double heightBasedFontSize = (baseFont.getSize2D() * image.getHeight()) / expectedHeight;
                double newFontSize = widthBasedFontSize < heightBasedFontSize ? widthBasedFontSize : heightBasedFontSize;
                baseFont = baseFont.deriveFont(baseFont.getStyle(), (float) newFontSize);
            }

            //рисуем
            graphics.setFont(baseFont);
            graphics.setColor(Color.BLACK);
            graphics.drawString(text, positionX, positionY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


}
