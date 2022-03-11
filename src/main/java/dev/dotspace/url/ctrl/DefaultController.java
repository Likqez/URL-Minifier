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

  /**
   * Handles incoming GET requests on global path.
   *
   * @param model the autoinvoced model to pass values to thymleaf
   * @return the html template "homepage"
   */
  @GetMapping("/")
  public String homepage(Model model) {
    model.addAttribute("title", UrlMinifierApplication.TITLE.formatted("Dashboard"));
    return "homepage";
  }

  /**
   * Handles incoming GET requests on the "/a/xxxxxx" path.
   * This function will retrieve all analytic data from the specified uid
   * to analyse its usage. The total and unique Clicks, aswell as the most
   * connected region and the most active day will be added to the output.
   *
   * @param model the autoinvoced model to pass values to thymleaf
   * @param uid   the uid to analyse
   * @return the "analytics" template or the "homepage", if something failed
   */
  @GetMapping("/a/{uid}")
  public String analytics(Model model, @PathVariable String uid) {
    model.addAttribute("title", UrlMinifierApplication.TITLE.formatted("Analytics"));

    /* Checks wether uid exists, if not display homepage */
    if (StorageManager.queryUrl(uid).isEmpty())
      return "homepage";

    /* Collect all PageClicks from analytic database */
    var allClicks = StorageManager.retrieveAnalytics(uid);
    model.addAttribute("pageClicks", allClicks);

    /* Filter for unique clicks */
    var uniqueClicks = allClicks.stream().map(PageClick::address).distinct().count();
    model.addAttribute("uniqueClicks", uniqueClicks);

    /* Filter for most common region */
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

  /**
   * Handles all GET requests on the "/xxxxxx" path.
   * Retrieves data from the connecting browser for logging purposes.
   * Then redirects user to orginal url gathered from storage.
   *
   * @param uid     the uid
   * @param request the autoinvoked serverletrequest
   * @return a redirectView pointing to an url
   */
  @GetMapping("/{uid}")
  public RedirectView handleRedirect(@PathVariable String uid, HttpServletRequest request) {
    final var userAgent = request.getHeader("User-Agent");
    /* Retrieve address from cloudflare header, if specified. Else from servelet request */
    final var remoteAddr = request.getHeader("cf-connecting-ip") != null ? request.getHeader("cf-connecting-ip") : request.getRemoteAddr();
    /* Retrieve region from cloudflare header, if specified. Else tell "Not/Available" */
    final var region = request.getHeader("cf-ipcountry") != null ? request.getHeader("cf-ipcountry") : "N/A";

    /* Searched for uid in storage */
    Optional<String> url = StorageManager.queryUrl(uid);

    /* Save analytic data */
    StorageManager.registerClick(uid, remoteAddr, userAgent, region);

    /* if found, redirect user to url, else to the homepage */
    return url.map(RedirectView::new).orElseGet(() -> new RedirectView("/"));
  }

}
