package es.codeurjc.gameweb.controllers;

import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.gameweb.models.*;
import es.codeurjc.gameweb.models.Game;
import es.codeurjc.gameweb.services.ChatService;
import es.codeurjc.gameweb.services.GamePostService;
import es.codeurjc.gameweb.services.ImageService;



@Controller
public class NavigationController implements ErrorController{
    @Autowired
    private CommonFunctions commonFunctions;
    @Autowired
	private GamePostService gamePostService;
    @Autowired
	private ImageService imagePostService;
    @Autowired
	private ChatService chatService;
    private static final String IMAGES = "images";
    private Game myGame;
    @GetMapping("/")
    public String showIndex(Model model) {
        commonFunctions.getSession(model);
        model.addAttribute("games", gamePostService.findAll());
        return "index";
    }
    @GetMapping("/adminUpdates")
    public String showAdminGamesPage(Model model) {
        commonFunctions.getSession(model);
        model.addAttribute("games", gamePostService.findAll());

		return "adminUpdates";
	}
    @GetMapping("/game/{id}/image")	
	public ResponseEntity<Object> downloadImage(@PathVariable int id) throws MalformedURLException {
		return imagePostService.createResponseFromImage(IMAGES, id);		
	}
    @GetMapping("/RegisterPage") 
        public String showRegister() {
        return "RegisterPage";
    }
    @GetMapping("/addGame")
    public String addGame(Model model) {
        commonFunctions.getSession(model);
        return "newGame";
    }
    @RequestMapping("/GamePage/{name}") 
    public String showGame(Model model, @PathVariable String name) {
         
        //Find the game we need to show
        for (Integer i=0;i<=gamePostService.findAll().size()-1;i++){
            if (gamePostService.findById(i).getGameTitle().equals(name))
                myGame = gamePostService.findById(i);
        }
        model.addAttribute("name", name);
        model.addAttribute("description", myGame.getDescription());
        //save the ID of the game to connect it to a chat
        long IDgame = myGame.getId();
        
        //iterate the chat messages to allign them to the right or to the left
        for (Integer i=0;i<=chatService.findById(IDgame).getListMessages().size()-1;i++){
            if(commonFunctions.getU().getInfo().equals(chatService.findById(IDgame).getListMessages().get(i).getNameUser())) 
            chatService.findById(IDgame).getListMessages().get(i).setMessageWriter(true);
            else
            chatService.findById(IDgame).getListMessages().get(i).setMessageWriter(false);
        }
        model.addAttribute("Messages", chatService.findById(IDgame).getListMessages());
        commonFunctions.getSession(model);
        //model.addAttribute("/games/{{id}}/image", imagePostService.);
        
        return "GamePage";
    }
    @RequestMapping("/Profile") 
    public String showProfile(Model model) {
        model.addAttribute("name", commonFunctions.getU().getInfo());
        model.addAttribute("password", commonFunctions.getU().getPassword());
        commonFunctions.getSession(model);
        return "Profile";
    }

    @RequestMapping("/Profile/{name}/Suscripciones") 
    public String showSuscriptions(Model model, @PathVariable String name) {
        model.addAttribute("name", name);
        commonFunctions.getSession(model);
        return "Suscripciones";
    }

    @GetMapping("/Sesion+Cerrada")
    public String SignOff(Model model) {
        commonFunctions.getU().setLogged(false);
        commonFunctions.getSession(model);
        return "index";
    }
    @GetMapping("/LogInPage") 
    public String showLogIn() {
        return "LogInPage";
    }
    @RequestMapping("/listPosts/{name}")
    public String showListPost(Model model,@PathVariable String name) {
        ArrayList<UpdatePost> myPosts= new ArrayList<UpdatePost>();
        myPosts.add(new UpdatePost("Primero", null, null, null, null,"este es el primer texto"));
        myPosts.add(new UpdatePost("Segundo", null, null, null, null,"este es el sec texto"));
        myPosts.add(new UpdatePost("Tercero", null, null, null, null,"este es el third texto"));
        model.addAttribute("name",name);
        model.addAttribute("lista", myPosts);
        commonFunctions.getSession(model);
        return "listPosts";
    }
    @RequestMapping("/expandedPost/{titlePost}")
    public String showExpandedPost(Model model,@PathVariable String titlePost) {
        model.addAttribute("titlePost",titlePost);
        commonFunctions.getSession(model);
        return "expandedPost";
    }
    @GetMapping("/Juegos")
    public String showListGames(Model model) {
        commonFunctions.getSession(model);
        model.addAttribute("games", gamePostService.findAll());
        return "gameList";
    }
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    
    if (status != null) {
        Integer statusCode = Integer.valueOf(status.toString());
    
        if(statusCode == HttpStatus.NOT_FOUND.value()) {
            return "error-404";
        }
        else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return "error-500";
        }
    }
    return "error";
}
    @Override
    public String getErrorPath() {
        return null;
    }
    
}
