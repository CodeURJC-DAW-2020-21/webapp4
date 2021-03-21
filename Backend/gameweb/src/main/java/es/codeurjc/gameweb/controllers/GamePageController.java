package es.codeurjc.gameweb.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.gameweb.models.Game;
import es.codeurjc.gameweb.models.Message;
import es.codeurjc.gameweb.models.User;
import es.codeurjc.gameweb.services.GamePostService;
import es.codeurjc.gameweb.services.UserService;

@Controller
public class GamePageController {  
    @Autowired
	private GamePostService gamePostService;
    
    @Autowired
	private UserService userService;


    @ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("logged", false);
		}
	}

    @RequestMapping("/GamePage/{id}/subButton")
    public String subButton(Model model,@PathVariable Long id, HttpServletRequest request){  
        Principal principal = request.getUserPrincipal();
        Optional<Game> myGame=gamePostService.findById(id);
        Game game=myGame.get();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get();
        if(!user.getMyGames().contains(id)){
            user.addElementToGameList(game.getId());
            myGame = gamePostService.findById(id);
            userService.save(user);
            model.addAttribute("game", myGame);
            model.addAttribute("customMessage", "Suscripción realizada con éxito");
            return "savedGame";
        }
        else{
            model.addAttribute("game", myGame);
            model.addAttribute("customMessage", "Ya te has subscrito a ese juego");
            return "savedGame";
        }
    }  
    @RequestMapping("/GamePage/{id}/unsubButton")
    public String unsubButton(Model model, @PathVariable Long id,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get();
        String gameTitle=gamePostService.findById(id).get().getGameTitle();
        for(int i=0; i<user.getMyGames().size(); i++){
            if(gamePostService.findById(user.getMyGames().get(i)).get().getGameTitle().equals(gameTitle)){
                user.getMyGames().remove(user.getMyGames().get(i));
                userService.save(user);
            }
        }
        model.addAttribute("customMessage", "Desuscripción realizada con éxito");        
        return "savedGame";
    } 
    public float doAverageScore(HashMap<Long, Integer> MyScores){
        float aux = 0;
        for (Integer value : MyScores.values()) {
            aux= aux + value;
        }
        
        aux = aux/(MyScores.size());
        aux = aux*10;
        aux = Math.round(aux);
        aux = aux/10;
        return aux;
    }

    @RequestMapping("/valorar/{id}")
    public String ValorarGame(Model model, @PathVariable Long id, @RequestParam Integer stars,HttpServletRequest request) {

        Optional<Game> myGame = gamePostService.findById(id);
        Game game = myGame.get();
        
        //we check if the array have the initialized value to change it in case it have it or just add the new one in other case
        if (game.getMapScores().containsValue(0)){
            game.getMapScores().clear();
            game.getMapScores().put(commonFunctions.getU().getId(), stars);
        }
        else 
            game.getMapScores().put(commonFunctions.getU().getId(), stars);

        //call to the method to do the average and set it on the game parameters
        float myAverage= doAverageScore(game.getMapScores());
        game.setAverageScore(myAverage);
        gamePostService.save(game);
        commonFunctions.getSession(model);
        model.addAttribute("customMessage", "Juego valorado con un " + stars + " con éxito");

        return "savedGame";
    }
    @PostMapping("/AgregarChat/{id}")
    public String newChat(Model model, @PathVariable Long id, @RequestParam String sentChat) {
        Optional<Game> game = gamePostService.findById(id);
        Game myGame = game.get();
        model.addAttribute("game", myGame);
        //iterate the chat messages to allign them to the right or to the left
        for (Integer i=0;i<=myGame.getChat().getListMessages().size()-1;i++){
            if(commonFunctions.getU().getInfo().equals(myGame.getChat().getListMessages().get(i).getNameUser())) 
            myGame.getChat().getListMessages().get(i).setMessageWriter(true);
            else
            myGame.getChat().getListMessages().get(i).setMessageWriter(false);
        }
        model.addAttribute("Messages", myGame.getChat().getListMessages());
        commonFunctions.getSession(model);
        
        //Create the message and add it to the chat of the game
        Message MyMessage = new Message(commonFunctions.getU().getInfo(), sentChat,true);
        myGame.getChat().getListMessages().add(MyMessage);
        model.addAttribute("Messages", myGame.getChat().getListMessages());
        commonFunctions.getSession(model);
        gamePostService.save(myGame);
        if(commonFunctions.getU().isLogged()){
            model.addAttribute("canSub", !commonFunctions.getU().getMyGames().contains(myGame.getId()));
            //model.addAttribute("Messages", chat.getListMessages());
        }   
        return "GamePage";
    }
    
}
