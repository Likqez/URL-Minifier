package dev.dotspace.url.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

/**
 * Utility class for QR-Code generation
 *
 * @author Lukas Lanzner
 */
public class QRCodeGenerator {


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
  public static byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
    MatrixToImageConfig con = new MatrixToImageConfig(0xFF000002, 0xFFFFFFFF);

    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream, con);
    return pngOutputStream.toByteArray();
  }

  /**
   * Uses {@link QRCodeGenerator#getQRCodeImage(String, int, int)} to generate QR-Code
   * PNG image. Then encodes the image to base64 for easier displayability in the web.
   * If function succeeds, an optional containing the base64 encoded image is returned.
   * On error an empty optional is returned.
   *
   * @param text   the text within the QR-Code
   * @param width  the width of the image
   * @param height the height of the image
   * @return an optional
   */
  public static Optional<String> getQRCodeBase64(String text, int width, int height) {
    try {
      var bytes = getQRCodeImage(text, width, height);
      return Optional.of(Base64.getEncoder().encodeToString(bytes));
    } catch (WriterException | IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }


}
