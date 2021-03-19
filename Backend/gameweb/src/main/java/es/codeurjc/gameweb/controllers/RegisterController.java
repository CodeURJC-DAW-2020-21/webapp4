package es.codeurjc.gameweb.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.gameweb.models.User;
import es.codeurjc.gameweb.services.GamePostService;
import es.codeurjc.gameweb.services.UserService;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private CommonFunctions commonFunctions;

    @Autowired
	private GamePostService gamePostService;

    @PostMapping("/IndexLogged")
    public String newUser(Model model, User user, @RequestParam String password, @RequestParam String password1) throws IOException {
        if(password.equals(password1)){
            user.setAdmin(false);
            user.setRoles();
            userService.save(user);
            user.setLogged(true);
            commonFunctions.setU(user);
            commonFunctions.getSession(model);
        model.addAttribute("games", gamePostService.findAll());
        model.addAttribute("whatList", "Recomendados");
        model.addAttribute("nextPage", 1);
        return "index";
        }
        commonFunctions.getSession(model);
        return "RegisterPage";
    }

}
