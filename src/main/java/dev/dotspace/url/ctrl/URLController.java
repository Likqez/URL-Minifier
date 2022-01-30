package dev.dotspace.url.ctrl;

import dev.dotspace.url.conf.ApplicationConfiguration;
import dev.dotspace.url.response.GenerationResponse;
import dev.dotspace.url.storage.StorageManager;
import dev.dotspace.url.util.QRCodeGenerator;
import dev.dotspace.url.util.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

@RestController
public class URLController {

  //URL Matching Pattern https://mathiasbynens.be/demo/url-regex @diegoperini
  final Pattern urlPattern = Pattern.compile("^(?:(?:https?)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?$");

  @RequestMapping(method = RequestMethod.POST, path = "/api/v1/create", consumes = "application/json")
  public GenerationResponse createMinified(@Validated @RequestBody dev.dotspace.url.response.RequestBody content) {

    String minifiedURL = "";
    String image = "";
    final AtomicBoolean success = new AtomicBoolean(false);

    /* Try generations 5 times if unsuccessful */
    for (int i = 0; i <= 5; i++) {
      /* Immidiatly exit other tries after one SUCCESS */
      if (success.get()) break;

      /* Identifier generation */
      var uid = RandomStringUtils.random();
      minifiedURL = ApplicationConfiguration.APPLICATION_WEB_PATH().concat(uid);

      /* QRcode generatio */
      var qrcode = QRCodeGenerator.getQRCodeBase64(minifiedURL, 500, 500);
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
