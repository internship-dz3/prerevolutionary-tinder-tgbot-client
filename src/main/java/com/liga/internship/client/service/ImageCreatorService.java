package com.liga.internship.client.service;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;

@Service
@RequiredArgsConstructor
public class ImageCreatorService {

    //    private final TextService textService;
//
//    @Value("${message.background}")
//    private Resource resource;
//
//    public File getImageWithTextFile(String text) throws IOException {
//        BufferedImage bufferedImage = getImageWithText(text);
//        Files.createDirectories(Paths.get("temp"));
//        File trend = new File("temp/image.jpg");
//        ImageIO.write(bufferedImage, "jpg", trend);
//        return trend;
//    }
//
//    private BufferedImage getImageWithText(String text) {
//        BufferedImage image = null;
//        try {
//            File imageFile = resource.getFile();
//            image = ImageIO.read(imageFile);
//            Font baseFont = new Font("Old Standard TT", Font.BOLD, 18);
//            Graphics graphics = image.getGraphics();
//            //центруем
//            FontMetrics metrics = graphics.getFontMetrics(baseFont);
//            int positionX = 0;
//            int positionY = metrics.getAscent();
//            FontMetrics ruler = graphics.getFontMetrics(baseFont);
//            GlyphVector vector = baseFont.createGlyphVector(ruler.getFontRenderContext(), text);
//            Shape outline = vector.getOutline(0, 0);
//            double expectedWidth = outline.getBounds().getWidth();
//            double expectedHeight = outline.getBounds().getHeight();
//            boolean textFits = image.getWidth() >= expectedWidth && image.getHeight() >= expectedHeight;
//            if (!textFits) {
//                positionX = (image.getWidth() - metrics.stringWidth(text)) / 2;
//                positionY = (image.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
//                double widthBasedFontSize = (baseFont.getSize2D() * image.getWidth()) / expectedWidth;
//                double heightBasedFontSize = (baseFont.getSize2D() * image.getHeight()) / expectedHeight;
//                double newFontSize = widthBasedFontSize < heightBasedFontSize ? widthBasedFontSize : heightBasedFontSize;
//                baseFont = baseFont.deriveFont(baseFont.getStyle(), (float) newFontSize);
//            }
//
//            //рисуем
//            graphics.setFont(baseFont);
//            graphics.setColor(Color.BLACK);
//            graphics.drawString(text, positionX, positionY);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return image;
//    }

    public static void putTextOnImage(BufferedImage image, String text) throws IOException {
        Font font = new Font("Old Standard TT", Font.BOLD, 18);
        Graphics g = image.getGraphics();
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString(text, 100, 100);
        g.dispose();
        try {
            ImageIO.write(image, "png", new File("test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImagePlus resultGraphicsAdaptBasedOnImage = new ImagePlus("", signImageAdaptBasedOnImage(text, String.valueOf(image)));
        ImageIO.write(image, "png", new File("test.png"));

    }


    public static BufferedImage signImageAdaptBasedOnImage(String text, String path) throws IOException {

        BufferedImage image = ImageIO.read(new File(path));

        Font font = createFontToFit(new Font("Old Standard TT", Font.BOLD, 18), text, image);

        AttributedString attributedText = new AttributedString(text);
        attributedText.addAttribute(TextAttribute.FONT, font);
        attributedText.addAttribute(TextAttribute.FOREGROUND, Color.GREEN);

        Graphics g = image.getGraphics();

        FontMetrics metrics = g.getFontMetrics(font);
        int positionX = (image.getWidth() - metrics.stringWidth(text));
        int positionY = (image.getHeight() - metrics.getHeight()) + metrics.getAscent();

        g.drawString(attributedText.getIterator(), positionX, positionY);
        ImageIO.write(image, "png", new File("test.png"));

        return image;
    }

    public static Font createFontToFit(Font baseFont, String text, BufferedImage image) throws IOException {
        Font newFont = baseFont;

        FontMetrics ruler = image.getGraphics().getFontMetrics(baseFont);
        GlyphVector vector = baseFont.createGlyphVector(ruler.getFontRenderContext(), text);

        Shape outline = vector.getOutline(0, 0);

        double expectedWidth = outline.getBounds().getWidth();
        double expectedHeight = outline.getBounds().getHeight();

        boolean textFits = image.getWidth() >= expectedWidth && image.getHeight() >= expectedHeight;

        if (!textFits) {
            double widthBasedFontSize = (baseFont.getSize2D() * image.getWidth()) / expectedWidth;
            double heightBasedFontSize = (baseFont.getSize2D() * image.getHeight()) / expectedHeight;

            double newFontSize = Math.min(widthBasedFontSize, heightBasedFontSize);
            newFont = baseFont.deriveFont(baseFont.getStyle(), (float) newFontSize);
        }
        return newFont;
    }


    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(new File("G:/GitHub/prerevolutionary-tinder-tgbot-client/src/main/resources/static/background.jpg"));
        TextService textService = new TextService();
        String input = "Граф, 33 л., желает посредством брака сделать богатую невесту графиней. Затем согласен дать свободный вид на жительство";
        putTextOnImage(image, String.valueOf(textService.translateTextIntoSlavOld(input)));
    }


}
