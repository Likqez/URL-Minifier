package dev.dotspace.url.ctrl;

import dev.dotspace.url.UrlMinifierApplication;
import dev.dotspace.url.storage.StorageManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class DefaultController {

  @SuppressWarnings("SameReturnValue")
  @GetMapping("/")
  public String homepage(Model model) {
    model.addAttribute("title", UrlMinifierApplication.TITLE.formatted("Dashboard"));
    return "homepage";
  }

  @GetMapping("/a/{uid}")
  public String analytics(Model model, @PathVariable String uid) {
    model.addAttribute("title", UrlMinifierApplication.TITLE.formatted("Analytics"));
    model.addAttribute("totalClicks", 10);
    model.addAttribute("uniqueClicks",3);
    model.addAttribute("topRegion","DE");
    model.addAttribute("topDay","Jan. 1. 2022");


    return "analytics";
  }

  @GetMapping("/{uid}")
  public RedirectView handleRedirect(@PathVariable String uid, HttpServletRequest request) {
    final var userAgent = request.getHeader("User-Agent");
    final var remoteAddr = request.getHeader("cf-connecting-ip") != null ? request.getHeader("cf-connecting-ip") : request.getRemoteAddr();

    Optional<String> url = StorageManager.queryUrl(uid);

    StorageManager.registerClick(uid, remoteAddr, userAgent, request.getHeader("cf-ipcountry"));

    return url.map(RedirectView::new).orElseGet(() -> new RedirectView("/"));
  }

}
