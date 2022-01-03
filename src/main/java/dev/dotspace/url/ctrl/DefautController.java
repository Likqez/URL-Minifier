package dev.dotspace.url.ctrl;

import dev.dotspace.url.UrlMinifierApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DefautController {

  @GetMapping("/")
  public String homepage(Model model) {
    model.addAttribute("title", UrlMinifierApplication.TITLE.formatted("Dashboard"));
    return "homepage";
  }

}
