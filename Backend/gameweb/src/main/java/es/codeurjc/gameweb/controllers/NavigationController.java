package es.codeurjc.gameweb.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;

import es.codeurjc.gameweb.models.*;

import org.springframework.http.HttpHeaders;

import es.codeurjc.gameweb.services.ChatService;
import es.codeurjc.gameweb.services.GamePostService;
import es.codeurjc.gameweb.services.PostService;
import es.codeurjc.gameweb.services.UserService;

@Controller
public class NavigationController implements ErrorController {
    @Autowired
    private CommonFunctions commonFunctions;
    @Autowired
    private ChatService chatService;
    @Autowired
    private GamePostService gamePostService;
    @Autowired
    private PostService pService;
    private Genres genres;
    @Autowired
    private UserService userService;
    
    public Map.Entry<Genres,Integer> recommendedAlgorithm(){
        HashMap<Genres,Integer> amountOfGamesWithGenre=new HashMap<Genres,Integer>();
        for(Genres g : Genres.values()){
            amountOfGamesWithGenre.put(g, 0);
        }
        Map.Entry<Genres,Integer> maxEntry=null;
        for (Long game : commonFunctions.getU().getMyGames()) {
            amountOfGamesWithGenre.put(gamePostService.findById(game).get().getGenre(),amountOfGamesWithGenre.get(gamePostService.findById(game).get().getGenre())+1);
        }
        
        for(Map.Entry<Genres,Integer> entry : amountOfGamesWithGenre.entrySet()){
            if(maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0){
                maxEntry=entry;
            }           
        }
        return maxEntry;
    }

    @GetMapping("/")
    public String showIndex(Model model) {
        commonFunctions.getSession(model);      
        if(commonFunctions.getU().isLogged()){   
            try {
                ArrayList<Game> toShow=gamePostService.findGamesOfGenre(recommendedAlgorithm().getKey());       
            
                model.addAttribute("selectedList",toShow);
                model.addAttribute("whatList", "Recomendados");
            } catch (Exception e) { 
            
                model.addAttribute("selectedList",null);
                model.addAttribute("whatList", "");
            } 
            
        }
        else{
            try {
                model.addAttribute("selectedList", gamePostService.findBestRatedGames());
                model.addAttribute("whatList", "Juegos mejor valorados");
            } catch (Exception e) {
                model.addAttribute("selectedList", null);
                model.addAttribute("whatList", "");
            }
            
        }       
        model.addAttribute("games", gamePostService.findAll(PageRequest.of(0, 8)));
        model.addAttribute("nextPage", 1);
        return "index";
    }
    @GetMapping("/showMoreGames/{nextPage}")
    public String showMoreGames(Model model,@PathVariable int nextPage){
        model.addAttribute("games", gamePostService.findAll(PageRequest.of(nextPage, 8)));
        return "showMoreGames";
    }

    @GetMapping("/games/{id}/image")
    public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

        Optional<Game> game = gamePostService.findById(id);
        if (game.isPresent() && game.get().getImageFile() != null) {

            Resource file = new InputStreamResource(game.get().getImageFile().getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(game.get().getImageFile().length()).body(file);

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/Profile/image")
	public ResponseEntity<Object> downloadUserImage() throws SQLException {
        Long id = commonFunctions.getU().getId();
		Optional<User> user = userService.findById(id);
		if (user.isPresent() && user.get().getImageFile() != null) {

			Resource file = new InputStreamResource(user.get().getImageFile().getBinaryStream());
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(user.get().getImageFile().length()).body(file);

		} else {
			return ResponseEntity.notFound().build();
		}
	}

    @GetMapping("/adminUpdates")
    public String showAdminGamesPage(Model model) {
        commonFunctions.getSession(model);
        model.addAttribute("games", gamePostService.findAll());

        return "adminUpdates";
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

    @RequestMapping("/GamePage/{id}")
    public String showGame(Model model, @PathVariable Long id) {

        Optional<Game> myGame = gamePostService.findById(id);
        Optional<Chat> myChat = chatService.findById(id+1);
        Game game = myGame.get();
        Chat chat;
        chat = myChat.get();
        model.addAttribute("game", game);
        for (Integer i = 0; i <= chat.getListMessages().size() - 1; i++) {
            if (commonFunctions.getU().getInfo().equals(chat.getListMessages().get(i).getNameUser()))
                chat.getListMessages().get(i).setMessageWriter(true);
            else
                chat.getListMessages().get(i).setMessageWriter(false);
        }
        if(commonFunctions.getU().isLogged()){
            model.addAttribute("canSub", !commonFunctions.getU().getMyGames().contains(game.getId()));
            model.addAttribute("Messages", chat.getListMessages());
        }    
        commonFunctions.getSession(model);
        return "GamePage";
    }
    @RequestMapping("/morePosts") 
    public String showMorePosts(Model model) {
        
        return "morePosts";
    }
    @RequestMapping("/showMoreGames") 
    public String showMoreGames(Model model) {
        
        return "showMoreGames";
    }

    
    @RequestMapping("/listPosts/{id}/{theType}")
    public String showListPost(Model model,@PathVariable Long id,@PathVariable String theType){       
        Optional<Game> myGame = gamePostService.findById(id);
        Game game =myGame.get();       
        model.addAttribute("name",game.getGameTitle());
        model.addAttribute("postType", theType);
        PostType ty=null;
        switch(theType){
            case "Guias":
                ty=PostType.Guides;
                break;
            case "Noticias":
                ty=PostType.News;
                break;
            case "Actualizaciones":
                ty=PostType.Updates;
                break;
            default:
            System.out.println("PROBLEMOSN");
                break;
        }
        System.out.println(ty.name());
        ArrayList<Post> toShow=pService.findPostOfType(pService.findPostOfGame(game), ty);
        model.addAttribute("lista", toShow);
        commonFunctions.getSession(model);
        return "listPosts";
    }
    @RequestMapping("/Profile") 
    public String showProfile(Model model) {
        model.addAttribute("user", commonFunctions.getU());
        commonFunctions.getSession(model);
        return "Profile";
    }

    @RequestMapping("/Profile/{name}/Suscripciones")
    public String showSuscriptions(Model model, @PathVariable String name) {
        model.addAttribute("name", name);
        commonFunctions.getSession(model);
        return "Suscripciones";
    }

    @GetMapping("/Index")
    public String SignOff(Model model) {
        commonFunctions.getU().setLogged(false);
        commonFunctions.getSession(model);
        model.addAttribute("games", gamePostService.findBestRatedGames());
        model.addAttribute("whatList", "Mejor valorados");
        model.addAttribute("nextPage", 1);
        return "index";
    }

    @GetMapping("/LogInPage")
    public String showLogIn() {
        return "LogInPage";
    }

    

    @RequestMapping("/expandedPost/{id}")
    public String showExpandedPost(Model model, @PathVariable long id) {
        Optional<Post> p=pService.findById(id);
        model.addAttribute("post", p.get());
        commonFunctions.getSession(model);
        return "expandedPost";
    }

    @GetMapping("/Juegos")
    public String showListGames(Model model) {
        commonFunctions.getSession(model);
        model.addAttribute("games", gamePostService.findAll());
        model.addAttribute("genres", genres);
        return "gameList";
    }
    @RequestMapping("/createPostPage/{id}")
    public String showCreatePostPage(Model model, @PathVariable long id) {    
        commonFunctions.getSession(model);
        Optional<Game> game = gamePostService.findById(id);
		if (game.isPresent()) {
			model.addAttribute("game", game.get());
			return "createPostPage";
		} else {
            model.addAttribute("whatList", "Recomendados");
			return "index";
		}
    }
    @GetMapping("/EditarJuego/{id}")
    public String editGame(Model model, @PathVariable long id) {
        commonFunctions.getSession(model);
        Optional<Game> game = gamePostService.findById(id);
		if (game.isPresent()) {
			model.addAttribute("game", game.get());
			return "editGame";
		} else {
            model.addAttribute("whatList", "Recomendados");
			return "index";
		}
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error-500";
            }
        }
        return "error";
    }

    @Override
    public String getErrorPath() {
        return null;
    }

    public Integer doAverageRatio(ArrayList<Integer> MyScores, Integer index){
        Integer aux = 0;
        Integer numberofindexinthearray = 0;
        for(int i=0;i<=MyScores.size()-1;i++){
            if (MyScores.get(i).equals(index))
                numberofindexinthearray++;
        }
        aux = (numberofindexinthearray*100)/(MyScores.size());
        return aux;
    }

    public Integer doAverageRatio(HashMap<Long,Integer> MyScores, Integer index){
        Integer aux = 0;
        Integer numberofindexinthearray = 0;
        for (Integer value : MyScores.values()) {
            if (value.equals(index))
            numberofindexinthearray++;
        }
        aux = (numberofindexinthearray*100)/(MyScores.size());
        return aux;
    }

    @RequestMapping("/Estadisticas/{id}")
    public String showGameStats(Model model, @PathVariable Long id) {

        Optional<Game> myGame = gamePostService.findById(id);
        
        Game game = myGame.get();
        model.addAttribute("game", game);
        Integer int1=doAverageRatio(game.getMapScores(),1);
        Integer int2=doAverageRatio(game.getMapScores(),2);
        Integer int3=doAverageRatio(game.getMapScores(),3);
        Integer int4=doAverageRatio(game.getMapScores(),4);
        Integer int5=doAverageRatio(game.getMapScores(),5);
        model.addAttribute("gamestars1", int1);
        model.addAttribute("gamestars2", int2);
        model.addAttribute("gamestars3", int3);
        model.addAttribute("gamestars4", int4);
        model.addAttribute("gamestars5", int5);
        commonFunctions.getSession(model);

        return "gamestadistics";
    }

}
