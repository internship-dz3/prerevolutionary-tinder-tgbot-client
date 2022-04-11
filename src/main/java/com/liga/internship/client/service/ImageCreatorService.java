package com.liga.internship.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.file.Path;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Сервис создания изображения профиля пользователя, с указанным переведенным текстом
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCreatorService {
    private final TextService textService;

    @Value("${message.background}")
    private Resource backgroundImageResourse;
    //папка хранящее изображения сообщений пользователя
    @Value("${temp.folder}")
    private Path tempUserImagesFolderPath;

    /**
     * @param text   -текст наносимый на картинку
     * @param userId - id профиля пользователя
     * @return File с картинкой
     */
    public File getImageWithTextFile(String text, long userId) {
        File trend = null;
        try {
            String translatedText = textService.translateTextIntoSlavOld(text);
            BufferedImage bufferedImage = getImageWithText(translatedText);
            Files.createDirectories(tempUserImagesFolderPath);
            trend = new File(String.format("%s/image%d.jpg", tempUserImagesFolderPath.toString(), userId));
            ImageIO.write(bufferedImage, "jpg", trend);
            log.debug("getImageWithTextFile image created");
        } catch (IOException e) {
            log.error("getImageWithTextFile error", e);
        }
        return trend;
    }

    private void drawLines(Rectangle rect, Graphics2D g2, float x, List<TextLayout> lines, float textHeight) {
        float yAxis = (rect.height - textHeight) / 2;
        for (TextLayout line : lines) {
            line.draw(g2, x, yAxis + line.getAscent());
            yAxis += line.getAscent() + line.getDescent() + line.getLeading();
        }
    }

    private List<AttributedString> getAttributedStrings(String[] formatted) {
        Font headerFont = new Font("Old Standard TT", Font.BOLD, 55);
        Font descriptionFont = new Font("Old Standard TT", Font.PLAIN, 24);
        List<AttributedString> attributedStrings = new ArrayList<>();
        for (int i = 0; i < formatted.length; i++) {
            AttributedString attributedStringLine = new AttributedString(formatted[i]);
            if (i == 0) {
                attributedStringLine = new AttributedString(formatted[i].toUpperCase(Locale.ROOT));
                attributedStringLine.addAttribute(TextAttribute.FONT, headerFont);
            } else {
                attributedStringLine.addAttribute(TextAttribute.FONT, descriptionFont);
            }
            attributedStrings.add(attributedStringLine);
        }
        return attributedStrings;
    }

    private Graphics2D getGraphics2D(BufferedImage image) {
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setColor(Color.BLACK);
        return g2;
    }

    private BufferedImage getImageWithText(String text) {
        String[] formatted = getTransformedTextWithNewLine(text);
        BufferedImage image = null;
        try {
            image = ImageIO.read(backgroundImageResourse.getInputStream());
            Rectangle rect = new Rectangle(image.getWidth(), image.getHeight());
            List<AttributedString> attributedStrings = getAttributedStrings(formatted);
            if (!attributedStrings.isEmpty()) {
                Graphics2D g2 = getGraphics2D(image);
                float xAxis = 50;
                List<TextLayout> lines = getTextLayouts(formatted, rect, attributedStrings, g2, xAxis);
                float textHeight = 0;
                textHeight = getTextHeight(lines, textHeight);
                drawLines(rect, g2, xAxis, lines, textHeight);
            }
        } catch (IOException e) {
            log.error("getImageWithText error", e);
        }
        return image;
    }

    private float getTextHeight(List<TextLayout> lines, float textHeight) {
        for (TextLayout line : lines) {
            textHeight += line.getAscent() + line.getDescent() + line.getLeading();
        }
        return textHeight;
    }

    private List<TextLayout> getTextLayouts(String[] formatted, Rectangle rect, List<AttributedString> attributedStrings, Graphics2D g2, float x) {
        List<TextLayout> lines = new ArrayList<>();
        for (int i = 0; i < attributedStrings.size(); i++) {
            AttributedCharacterIterator iterator = attributedStrings.get(i).getIterator();
            LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, g2.getFontRenderContext());
            while (measurer.getPosition() < formatted[i].length()) {
                lines.add(measurer.nextLayout((rect.width - x * 2)));
            }
        }
        return lines;
    }

    private String[] getTransformedTextWithNewLine(String text) {
        String translatedText = textService.translateTextIntoSlavOld(text);
        return translatedText.replace("\n", "\\s")
                .replaceFirst("\\s+", "\n").split("\n");
    }
}
