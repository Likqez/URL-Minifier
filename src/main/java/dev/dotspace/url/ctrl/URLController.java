package dev.dotspace.url.ctrl;

import dev.dotspace.url.conf.ApplicationConfiguration;
import dev.dotspace.url.response.CreationRequestBody;
import dev.dotspace.url.response.GenerationResponse;
import dev.dotspace.url.storage.StorageManager;
import dev.dotspace.url.util.QRCodeGenerator;
import dev.dotspace.url.util.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class URLController {

  @RequestMapping(method = RequestMethod.POST, path = "/api/v1/create", consumes = "application/json")
  public GenerationResponse createMinified(@Validated @RequestBody CreationRequestBody content) {

    String minifiedURL = "";
    String image = "";
    final AtomicBoolean success = new AtomicBoolean(false);

    /* Try generations 5 times if unsuccessful */
    for (int i = 0; i <= 5; i++) {
      /* Immediately exit other tries after one SUCCESS */
      if (success.get()) break;

      /* Identifier generation */
      var uid = RandomStringUtils.random();
      minifiedURL = ApplicationConfiguration.APPLICATION_WEB_PATH().concat(uid);

      /* QR-Code generation */
      var qrcode = QRCodeGenerator.getQRCodeBase64(minifiedURL + "?q", 500, 500, null);
      image = qrcode.orElse(QRCodeGenerator.SAMPLE);

      /* Try inserting into Storage. If uid is duplicate -> try again */
      StorageManager.newMinified(
          uid,
          content.url(),
          image,
          () -> success.set(true)
      );
    }

    return new GenerationResponse(minifiedURL, image);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }

}
