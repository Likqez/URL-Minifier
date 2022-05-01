package dev.dotspace.url.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class for QR-Code generation
 *
 * @author Lukas Lanzner
 */
public class QRCodeGenerator {

  @SuppressWarnings("all")
  public static final String SAMPLE = "iVBORw0KGgoAAAANSUhEUgAAANwAAADcAQMAAAAhlF3CAAAABlBMVEX///" +
                                      "8AAABVwtN+AAAACXBIWXMAAA7EAAAOxAGVKw4bAAABlklEQVRYhd2YsZHE" +
                                      "MAhF0ThQ6BJUiks7leZSXILCDTRi+YB3fLfXACjYsfU24Qt9wMS6XrRfRH" +
                                      "T0H3lZm20OigWX7FF97Xw1bHSisraJzT0XLCJBFRGYLzqYiUSEKTKMqBBx" +
                                      "Xsepx0mJIVHDgXbSvA0KPTVHwx2UOP/L2wTQrQawnWI1xOXLhzLAxzJosn" +
                                      "ytAFCjrPhFnAyvKXObOOFUEI9Cx10F1VJRFpG3sWARk7SC3iwh5e88/TwT" +
                                      "wTtLvQp2iED3eSaCUjlmNRGuQ0Uwk1WjiQbn9tI45QpKdZAwfQt5mwlCBK" +
                                      "sEh5Z7VIf5VCgKhLG46ahratusd3KkgrLwvHt16Kw7LkIsuMwjtQcT9qkO" +
                                      "1RvRNFAi1olOll5BE4GqipAJkvoKxiAyq8HxW94GhFrWUB260HK3ZSMT9G" +
                                      "X3E09iqXpl40FrvGyikyG882dyJcoEfXLVCf30ntpTeaSC/lHFJlc0aM+k" +
                                      "DgmlafZPl8uqYFrY2HtNu51I3GDQUlNdUydX/jsG5YC31ZgI+BC2MLP/9q" +
                                      "H48A2o0D2Y1cCU2wAAAABJRU5ErkJggg==";

  /**
   * TODO add documentation
   *
   * @param text   the text within the QR-Code
   * @param width  the width of the image
   * @param height the height of the image
   * @return
   * @throws WriterException
   * @throws IOException
   */
  public static byte[] getQRCodeImage(String text, int width, int height, URL overlayURL) throws WriterException, IOException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();

    Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    hints.put(EncodeHintType.MARGIN, 2); // default = 4
    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

    /* Creation of qr code with content & dimensions */
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

    /* Load images as BuffImage */
    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
    BufferedImage overlayImage = null;

    if (overlayURL != null)
      overlayImage = ImageIO.read(overlayURL);


    /* Calculate height/width diff from qr code and overlay */
    int dHeight = qrImage.getHeight();
    int dWidth = qrImage.getWidth();
    if (overlayImage != null) {
      dHeight -= overlayImage.getHeight();
      dWidth -= overlayImage.getWidth();
    }

    /* Initialize combined image */
    BufferedImage combined = new BufferedImage(qrImage.getWidth(), qrImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) combined.getGraphics();

    /* Write logo into combined image */
    g.drawImage(qrImage, 0, 0, null);
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

    if (overlayImage != null)
      g.drawImage(overlayImage, dWidth / 2, dHeight / 2, null);
    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

    //Write combined image to output stream
    ImageIO.write(combined, "png", pngOutputStream);

    MatrixToImageConfig con = new MatrixToImageConfig(0xFF000002, 0xFFFFFFFF);
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
    return pngOutputStream.toByteArray();
  }

  /**
   * Uses {@link QRCodeGenerator#getQRCodeImage(String, int, int, URL)} to generate QR-Code
   * PNG image. Then encodes the image to base64 for easier displayability in the web.
   * If function succeeds, an optional containing the base64 encoded image is returned.
   * On error an empty optional is returned.
   *
   * @param text         the text within the QR-Code
   * @param width        the width of the image
   * @param height       the height of the image
   * @param overlayImage the (nullable) URL to an overlay image.
   * @return an optional
   */
  public static Optional<String> getQRCodeBase64(String text, int width, int height, String overlayImage) {
    try {
      var bytes = getQRCodeImage(text, width, height, overlayImage == null ? null : new URL(overlayImage));
      return Optional.of(Base64.getEncoder().encodeToString(bytes));
    } catch (WriterException | IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }


}
