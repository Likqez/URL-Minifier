package dev.dotspace.url.ctrl;

import dev.dotspace.url.conf.ApplicationConfiguration;
import dev.dotspace.url.response.GenerationResponse;
import dev.dotspace.url.response.ResponseStatus;
import dev.dotspace.url.storage.StorageManager;
import dev.dotspace.url.util.QRCodeGenerator;
import dev.dotspace.url.util.RandomStringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@RestController
public class URLController {

  //URL Matching Pattern https://mathiasbynens.be/demo/url-regex @diegoperini
  final Pattern urlPattern = Pattern.compile("^(?:(?:https?)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?$");

  @RequestMapping(method = RequestMethod.POST, path = "/api/v1/create", consumes = "application/json")
  public GenerationResponse createMinified(@RequestBody dev.dotspace.url.response.RequestBody content) {

    final AtomicReference<String> minifiedURL = new AtomicReference<>("");
    final AtomicReference<ResponseStatus> status = new AtomicReference<>(ResponseStatus.UNKNOWN);
    final AtomicReference<String> image = new AtomicReference<>("");

    /* Validate user input */
    if (!urlPattern.matcher(content.url()).matches())
      status.set(ResponseStatus.INVALID_URL);

    /* Try generations 5 times if unsuccessful */
    IntStream.range(0, 6).forEach(value -> {
      /* Immidiatly exit other tries after one SUCCESS */
      if (status.get() == ResponseStatus.SUCCESS) return;

      /* Identifier generation */
      var uid = RandomStringUtils.random();
      minifiedURL.set(ApplicationConfiguration.APPLICATION_WEB_PATH().concat(uid));

      /* QRcode generatio */
      var qrcode = QRCodeGenerator.getQRCodeBase64(minifiedURL.get(), 500, 500);
      image.set(qrcode.orElse(QRCodeGenerator.SAMPLE));

      /* Try inserting into Storage. If uid is duplicate -> try again */
      StorageManager.newMinified(
          uid,
          content.url(),
          image.get(),
          () -> status.set(ResponseStatus.SUCCESS),
          () -> status.set(ResponseStatus.UNKNOWN)
      );
    });

    return new GenerationResponse(status.get(), minifiedURL.get(), image.get());
  }


}
