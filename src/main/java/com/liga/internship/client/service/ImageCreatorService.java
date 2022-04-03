package com.liga.internship.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ImageCreatorService {
    private final TextService textService;

    @Value("${message.background}")
    private Resource resource;

    public File getImageWithTextFile(String description, long userId) {
        File trend = null;
        try {
            BufferedImage bufferedImage = getImageWithText(description);
            Files.createDirectories(Paths.get("temp"));
            trend = new File(String.format("temp/image%d.jpg", userId));
            ImageIO.write(bufferedImage, "jpg", trend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trend;
    }

    private BufferedImage getImageWithText(String text) {
        String[] formatted = getTransformedTextWithNewLine(text);
        BufferedImage image = null;
        try {
            File imageFile = resource.getFile();
            image = ImageIO.read(imageFile);
            Font headerFont = new Font("Old Standard TT", Font.BOLD, 55);
            Font descriptionFont = new Font("Old Standard TT", Font.PLAIN, 24);
            Rectangle rect = new Rectangle(image.getWidth(), image.getHeight());

            String headerText = formatted[0].toUpperCase(Locale.ROOT);
            String descriptionText = formatted[1] == null ? "" : formatted[1];

            AttributedString header = new AttributedString(headerText);
            header.addAttribute(TextAttribute.FONT, headerFont);

            AttributedString another = new AttributedString(descriptionText);
            another.addAttribute(TextAttribute.FONT, descriptionFont);

            AttributedCharacterIterator headerIterator = header.getIterator();
            AttributedCharacterIterator descriptionIterator = another.getIterator();

            Graphics2D g2 = (Graphics2D) image.getGraphics();
            g2.setColor(Color.BLACK);

            LineBreakMeasurer measurer1 = new LineBreakMeasurer(headerIterator, g2.getFontRenderContext());
            LineBreakMeasurer measurer2 = new LineBreakMeasurer(descriptionIterator, g2.getFontRenderContext());
            float textHeight = 0;
            float y = (rect.height - textHeight) / 2;
            float x = 50;

            List<TextLayout> lines = new ArrayList<>();
            while (measurer1.getPosition() < headerText.length()) {
                lines.add(measurer1.nextLayout((rect.width - x * 2)));
            }

            while (measurer2.getPosition() < descriptionText.length()) {
                lines.add(measurer2.nextLayout((rect.width - x * 2)));
            }

            for (TextLayout line : lines) {
                textHeight += line.getAscent() + line.getDescent() + line.getLeading();
            }

            for (TextLayout line : lines) {
                line.draw(g2, x, y + line.getAscent());
                y += line.getAscent() + line.getDescent() + line.getLeading();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private String[] getTransformedTextWithNewLine(String text) {
        String translatedText = textService.translateTextIntoSlavOld(text);
        return translatedText.replaceFirst("\\s", " \n").split("\n");
    }
}
