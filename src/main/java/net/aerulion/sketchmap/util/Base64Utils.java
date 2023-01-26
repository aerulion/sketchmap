package net.aerulion.sketchmap.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.imageio.ImageIO;

public class Base64Utils {

  public static BufferedImage decodeImage(final String image) throws IOException {
    final BufferedImage bufferedImage;
    bufferedImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(image)));
    final int width = bufferedImage.getWidth();
    final int height = bufferedImage.getHeight();
    final int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
    final BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    copy.setRGB(0, 0, width, height, pixels, 0, width);
    return copy;
  }

  public static String encodeImage(final BufferedImage bufferedImage, final String type)
      throws IOException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, type, Base64.getEncoder().wrap(byteArrayOutputStream));
    return byteArrayOutputStream.toString(StandardCharsets.ISO_8859_1.name());
  }
}