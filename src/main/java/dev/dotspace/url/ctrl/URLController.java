package dev.dotspace.url.ctrl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.dotspace.url.UrlMinifierApplication;
import dev.dotspace.url.response.GenerationResponse;
import dev.dotspace.url.response.ResponseStatus;
import dev.dotspace.url.util.QRCodeGenerator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
public class URLController {

  private static final Gson gson = new Gson();

  //URL Matching Pattern https://mathiasbynens.be/demo/url-regex @diegoperini
  final Pattern urlPattern = Pattern.compile("^(?:(?:https?)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?$");

  @RequestMapping(method = RequestMethod.POST, path = "/api/v1/create", consumes = "application/json")
  public GenerationResponse createMinified(@RequestBody String content) {
    var responseStatus = ResponseStatus.SUCCESS;
    String minifiedURL = "", image = "";

    try {
      var requestBody = gson.fromJson(content, JsonObject.class); // Convert Json String to JsonObject
      var url = requestBody.get("url").getAsString(); // Retrive 'url' from requestBody

      if (!urlPattern.matcher(url).matches())
        responseStatus = ResponseStatus.INVALID_URL;


      //TODO Sequenz generieren usw.
      minifiedURL = UrlMinifierApplication.webPath.concat("/AABB69");

      var qrcode = QRCodeGenerator.getQRCodeBase64(minifiedURL, 500, 500); //Generate QR-Code with url
      if (qrcode.isPresent()) image = qrcode.get();


    } catch (JsonSyntaxException ignore) {
      responseStatus = ResponseStatus.INVALID_JSON;
    } catch (NullPointerException ignore) {
      responseStatus = ResponseStatus.INVALID_ARGUMENTS;
    }

    return new GenerationResponse(responseStatus, minifiedURL, image);

  }

}
