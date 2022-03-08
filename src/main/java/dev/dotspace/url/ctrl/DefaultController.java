package dev.dotspace.url.ctrl;

import dev.dotspace.url.UrlMinifierApplication;
import dev.dotspace.url.response.PageClick;
import dev.dotspace.url.storage.StorageManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    if (StorageManager.queryUrl(uid).isEmpty())
      return "homepage";

    var allClicks = StorageManager.retrieveAnalytics(uid);
    model.addAttribute("pageClicks", allClicks);

    var uniqueClicks = allClicks.stream().map(PageClick::address).distinct().count();
    model.addAttribute("uniqueClicks", uniqueClicks);

    allClicks.stream()
        .map(PageClick::region)
        .filter(s -> !s.equals("N/A"))
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByValue())
        .ifPresentOrElse(
            s -> model.addAttribute("topRegion", s.getKey()),
            () -> model.addAttribute("topRegion", "N/A"));

    model.addAttribute("topDay", "Jan. 1. 2022");

    return "analytics";
  }

  @GetMapping("/{uid}")
  public RedirectView handleRedirect(@PathVariable String uid, HttpServletRequest request) {
    final var userAgent = request.getHeader("User-Agent");
    final var remoteAddr = request.getHeader("cf-connecting-ip") != null ? request.getHeader("cf-connecting-ip") : request.getRemoteAddr();
    final var region = request.getHeader("cf-ipcountry") != null ? request.getHeader("cf-ipcountry") : "N/A";

    Optional<String> url = StorageManager.queryUrl(uid);

    StorageManager.registerClick(uid, remoteAddr, userAgent, region);

    return url.map(RedirectView::new).orElseGet(() -> new RedirectView("/"));
  }

}
