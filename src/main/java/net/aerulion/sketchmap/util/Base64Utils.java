package net.aerulion.sketchmap.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class for base 64 related methods.
 */
public final class Base64Utils {

  @Contract(pure = true)
  private Base64Utils() {
    super();
    throw new UnsupportedOperationException("This utility class cannot be instantiated!");
  }

  /**
   * Decodes the base 64 string to a buffered image.
   *
   * @param image the image string to decode
   * @return the buffered image
   * @throws IOException when an unexpected error occurs
   */
  public static @NotNull BufferedImage decodeImage(final String image) throws IOException {
    final BufferedImage bufferedImage;
    bufferedImage = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(image)));
    final int width = bufferedImage.getWidth();
    final int height = bufferedImage.getHeight();
    final int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);
    final BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    copy.setRGB(0, 0, width, height, pixels, 0, width);
    return copy;
  }

  /**
   * Encodes the image as a base 64 string.
   *
   * @param bufferedImage the image to encode
   * @param type          the image format
   * @return the encoded string
   * @throws IOException when an unexpected error occurs
   */
  public static String encodeImage(final BufferedImage bufferedImage, final String type)
      throws IOException {
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedImage, type, Base64.getEncoder().wrap(byteArrayOutputStream));
    return byteArrayOutputStream.toString(StandardCharsets.ISO_8859_1);
  }

}