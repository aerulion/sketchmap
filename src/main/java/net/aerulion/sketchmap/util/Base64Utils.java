package net.aerulion.sketchmap.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Utils {

    public static BufferedImage decodeImage(String image) throws IOException {
        BufferedImage bufferedImage;
        bufferedImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(image)));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
        BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        copy.setRGB(0, 0, width, height, pixels, 0, width);
        return copy;
    }

    public static String encodeImage(BufferedImage bufferedImage, final String type) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, type, Base64.getEncoder().wrap(byteArrayOutputStream));
        return byteArrayOutputStream.toString(StandardCharsets.ISO_8859_1.name());
    }
}