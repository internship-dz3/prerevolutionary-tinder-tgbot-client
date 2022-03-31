package com.liga.internship.client.service;

import com.liga.internship.client.domain.UserProfile;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.AttributedString;

@Service
@RequiredArgsConstructor
public class ImageCreatorService {
    private final TextService textService;

    @Value("${message.background}")
    private Resource resource;

    public File getImageWithTextFile(String text, String chatId) {
        BufferedImage bufferedImage = getImageWithText(text);
        File trend = null;
        try {
            Files.createDirectories(Paths.get("temp"));
            trend = new File(String.format("temp/image%s.jpg",chatId));
            ImageIO.write(bufferedImage, "jpg", trend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trend;
    }

    private BufferedImage getImageWithText(String text) {
        String formatted = textService.translateTextIntoSlavOld(text);
        BufferedImage image = null;
        try {
            File imageFile = resource.getFile();
            image = ImageIO.read(imageFile);
            Font baseFont = new Font("Old Standard TT", Font.BOLD, 25);

            AttributedString attributedText = new AttributedString(formatted);
            attributedText.addAttribute(TextAttribute.FONT, baseFont);

            Graphics graphics = image.getGraphics();
            //центруем
            FontMetrics metrics = graphics.getFontMetrics(baseFont);
            int positionX = 0;
            int positionY = metrics.getAscent();
            FontMetrics ruler = graphics.getFontMetrics(baseFont);
            GlyphVector vector = baseFont.createGlyphVector(ruler.getFontRenderContext(), formatted);
            Shape outline = vector.getOutline(0, 0);

            double expectedWidth = outline.getBounds().getWidth();
            double expectedHeight = outline.getBounds().getHeight();
            boolean textFits = image.getWidth() >= expectedWidth && image.getHeight() >= expectedHeight;
            if (textFits) {
                positionX = (image.getWidth() - metrics.stringWidth(text)) / 2;
                positionY = (image.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
                double widthBasedFontSize = (baseFont.getSize2D() * image.getWidth()) / expectedWidth;
                double heightBasedFontSize = (baseFont.getSize2D() * image.getHeight()) / expectedHeight;
                double newFontSize = Math.min(widthBasedFontSize, heightBasedFontSize);
                baseFont = baseFont.deriveFont(baseFont.getStyle(), (float) newFontSize);
            }

            //рисуем
            graphics.setFont(baseFont);
            graphics.setColor(Color.BLACK);
            graphics.drawString(attributedText.getIterator(), positionX, positionY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public File getImageWithTextFile(UserProfile userProfile, long userId) {
        File trend = null;
        try {
            BufferedImage bufferedImage = getImageWithText2(userProfile.getDescription());
            Files.createDirectories(Paths.get("temp"));
            trend = new File(String.format("temp/image%d.jpg",userId));
            ImageIO.write(bufferedImage, "jpg", trend);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return trend;
    }

    private BufferedImage getImageWithText2(String text) throws IOException {
        String formatted = textService.translateTextIntoSlavOld(text);
        Font baseFont = new Font("Old Standard TT", Font.BOLD, 25);
        ImagePlus image = IJ.openImage(resource.getFile().getPath());
        ImageProcessor ip = image.getProcessor();
        ip.setFont(baseFont);
        ip.drawString(formatted, 0, 20);
        return ip.getBufferedImage();
    }


}
