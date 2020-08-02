package net.aerulion.sketchmap.util;

import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Utils {

    public static BufferedImage base64StringToImg(final String imageString) throws IOException {
        BufferedImage bImage;
        bImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(imageString)));
        int width = bImage.getWidth();
        int height = bImage.getHeight();
        int[] pixels = bImage.getRGB(0, 0, width, height, null, 0, width);
        BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        copy.setRGB(0, 0, width, height, pixels, 0, width);
        return copy;
    }

    public static BufferedImage base64StringToImgOLD(final String imageString) {
        BufferedImage image = null;
        try {
            final BASE64Decoder decoder = new BASE64Decoder();
            final byte[] imageByte = decoder.decodeBuffer(imageString);
            final ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public static String imgToBase64String(BufferedImage image, final String type) {
        String imageString = null;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, type, Base64.getEncoder().wrap(bos));
            imageString = bos.toString(StandardCharsets.ISO_8859_1.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
}
