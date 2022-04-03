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
            List<AttributedString> attributedStrings = new ArrayList<>();
            for (int i = 0; i < formatted.length; i++) {
                AttributedString attributedStringLine = new AttributedString(formatted[i]);
                if (i == 0) {
                    attributedStringLine.addAttribute(TextAttribute.FONT, headerFont);
                } else {
                    attributedStringLine.addAttribute(TextAttribute.FONT, descriptionFont);
                }
                attributedStrings.add(attributedStringLine);
            }
            if (!attributedStrings.isEmpty()) {
                Graphics2D g2 = (Graphics2D) image.getGraphics();
                g2.setColor(Color.BLACK);
                float x = 50;
                List<TextLayout> lines = new ArrayList<>();
                for (AttributedString attributedString : attributedStrings) {
                    AttributedCharacterIterator iterator = attributedString.getIterator();
                    LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, g2.getFontRenderContext());
                    while (measurer.getPosition() < headerText.length()) {
                        lines.add(measurer.nextLayout((rect.width - x * 2)));
                    }
                }
                float textHeight = 0;
                for (TextLayout line : lines) {
                    textHeight += line.getAscent() + line.getDescent() + line.getLeading();
                }
                float y = (rect.height - textHeight) / 2;
                for (TextLayout line : lines) {
                    line.draw(g2, x, y + line.getAscent());
                    y += line.getAscent() + line.getDescent() + line.getLeading();
                }
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
