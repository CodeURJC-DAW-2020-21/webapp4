package es.codeurjc.gameweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.gameweb.models.User;
import es.codeurjc.gameweb.services.ImageService;
import es.codeurjc.gameweb.services.UserService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Controller
public class ProfileController {
    
    @Autowired
    private CommonFunctions commonFunctions;
    @Autowired
    private UserService userService;
    @Autowired
	private ImageService imageUserService;
	private static final String User = "User_Images";

    @PostMapping("/Profile")
    public String changeName(Model model, @RequestParam String name) {
        model.addAttribute("name", name);
        model.addAttribute("password", commonFunctions.getU().getPassword());
        commonFunctions.getU().setInfo(name);
        
        userService.save(commonFunctions.getU());
        commonFunctions.getSession(model);
        return "Profile";
    }

    @RequestMapping("/Subscriptions")
    public String showSubscriptions(Model model){
        Optional<User> myUser=userService.findById(commonFunctions.getU().getId());
        User User = myUser.get();
        model.addAttribute("listaSubs",User.getMyGames());
        commonFunctions.getSession(model);
        return "Subscriptions";
    }

    @PostMapping("/CambiarContraseña")
    public String changePass(Model model, @RequestParam String password) {
        model.addAttribute("name", commonFunctions.getU().getInfo());
        model.addAttribute("password", password);
        commonFunctions.getU().setPassword(password);
        commonFunctions.getSession(model);
        return "Profile";
    }

    @PostMapping("/CambiarFoto")
	public String newFotoUser(Model model, MultipartFile image) throws IOException {
        model.addAttribute("name", commonFunctions.getU().getInfo());
        model.addAttribute("password", commonFunctions.getU().getPassword());
        imageUserService.saveImage(User, commonFunctions.getU().getId(), image);
        commonFunctions.getSession(model);	
		return "Profile";
	}

    @GetMapping("/Profile/{id}/image")	
	public ResponseEntity<Object> downloadImage(@PathVariable int id) throws MalformedURLException {
        return imageUserService.createResponseFromImage(User, id);		
	}

}