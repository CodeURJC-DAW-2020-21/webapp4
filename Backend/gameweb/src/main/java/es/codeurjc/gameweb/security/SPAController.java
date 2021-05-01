package es.codeurjc.gameweb.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SPAController {
    @GetMapping({ "/new/**/{path:[^\\.]*}", "/{path:spa[^\\.]*}" })
    public String redirect() {
        return "forward:/new/index.html";
    }
}
