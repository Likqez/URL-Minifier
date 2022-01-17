package dev.dotspace.url.ctrl;

import dev.dotspace.url.UrlMinifierApplication;
import dev.dotspace.url.storage.StorageManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class DefautController {

  @SuppressWarnings("SameReturnValue")
  @GetMapping("/")
  public String homepage(Model model) {
    model.addAttribute("title", UrlMinifierApplication.TITLE.formatted("Dashboard"));
    return "homepage";
  }

  @GetMapping("/{uid}")
  public RedirectView handleRedirect(
      @PathVariable String uid,
      @RequestHeader(value = "User-Agent") String userAgent,
      HttpServletRequest request) {

    Optional<String> url = StorageManager.queryUrl(uid);
    //TODO Analytics

    return url.map(RedirectView::new).orElseGet(() -> new RedirectView("/"));
  }

}
