package es.codeurjc.gameweb.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;

import es.codeurjc.gameweb.models.*;
import es.codeurjc.gameweb.repositories.PostRepository;
import es.codeurjc.gameweb.services.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PostsController {

    @Autowired
    private PostService pService;    
    @Autowired
    private GameService gamePostService;
    @Autowired
    private AlgorithmService algorithm;
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
    
    @RequestMapping("/adminUpdates/editPost/{id}")
    public String getEditPostPage(Model model,@PathVariable Long id){
        Post theUpdatedOne=pService.findById(id).get();
        model.addAttribute("post", theUpdatedOne);
        return "editPostPage";  
    }
    @PostMapping("/adminUpdates/editPost/{id}")
    public String editPost(Model model,@PathVariable Long id, @RequestParam String newTitle,@RequestParam String newType,@RequestParam String author,@RequestParam String newPostText,MultipartFile imageField,boolean removeImage)throws IOException, SQLException{
        
        Post theUpdatedOne=pService.findById(id).get();
        theUpdatedOne.setUpdateDate(getCurrentDate());
        theUpdatedOne.setTitle(newTitle);
        theUpdatedOne.setTheType(parseType(newType));
        theUpdatedOne.setAuthor(author);
        theUpdatedOne.setPostText(newPostText);
        updateImage(theUpdatedOne, removeImage, imageField);
        pService.save(theUpdatedOne);
        model.addAttribute("customMessage", "Post editado con éxito");
        return "successPage";  
    }

    @RequestMapping("/expandedPost/{id}")
    public String showExpandedPost(Model model, @PathVariable long id, HttpServletRequest request) {
        ArrayList<Object> gamesToShow;
        model.addAttribute("theID", id);
        gamesToShow=algorithm.setSomeList(request);
        model.addAttribute("selectedList",gamesToShow.get(1));
        if (gamesToShow.get(0).equals(0))
            model.addAttribute("whatList", "Recomendados");
        else{
            model.addAttribute("whatList", "Mejor valorados");
        }       
              
        try {       
            Principal principal = request.getUserPrincipal();
            Optional<User> myUser= userService.findByName(principal.getName());
            User user =myUser.get(); 
            if(user.getRoles().contains("ADMIN")){
                model.addAttribute("isAdmin", true);

            }
            else{
                model.addAttribute("isAdmin", false);
            }
        } catch (Exception e) {
            model.addAttribute("isAdmin", false);
        }
        
        return "expandedPost";
    }

    @RequestMapping("/listPosts/{id}/{theType}/{numPage}")
    public String showListPost(Model model,@PathVariable Long id,@PathVariable String theType,@PathVariable int numPage, HttpServletRequest request){  
        ArrayList<Object> gamesToShow;
        Optional<Game> myGame = gamePostService.findById(id);
        Game game =myGame.get();       
        model.addAttribute("theGameID", id);
        model.addAttribute("name",game.getGameTitle());
        model.addAttribute("postType", theType);
        gamesToShow=algorithm.setSomeList(request);
        model.addAttribute("selectedList",gamesToShow.get(1));
        Principal principal = request.getUserPrincipal();
        
        
        if(principal != null){
            Optional<User> myUser= userService.findByName(principal.getName());
            User user =myUser.get(); 
            model.addAttribute("whatList", "Recomendados");
        }           
        else
        {
            model.addAttribute("whatList", "Mejor valorados");
        }
            
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
        int quantity=pService.findPostOfType(pService.findPostOfGame(game), ty).size();
        try {
            
            ArrayList<Post> toShow=pService.findPostOfType(pService.findPostOfGamePage(game, PageRequest.of(numPage, 8)), ty);
            model.addAttribute("lista", toShow);
            if(toShow.size()>=quantity){
                model.addAttribute("canLoadMore",false) ;
            } else{
                model.addAttribute("canLoadMore",true) ;
            }
            
        } catch (Exception e) {
            model.addAttribute("lista", null);
        }
        
        
        model.addAttribute("maximo", quantity/8);
        model.addAttribute("numPage", numPage);
        return "listPosts";
    }

    @RequestMapping("/createPostPage/{id}")
    public String showCreatePostPage(Model model, @PathVariable long id,HttpServletRequest request) {    
        Principal principal = request.getUserPrincipal();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get(); 
        model.addAttribute("gameID", id);
        return "createPostPage";
		/*if (game.isPresent()) {
			model.addAttribute("gameID", id);
            
			return "createPostPage";
		} else {
            model.addAttribute("whatList", "Recomendados");
			return "index";
		}*/
    }

    @GetMapping("/posts/{id}/image")
    public ResponseEntity<Object> downloadPostImage(@PathVariable long id) throws SQLException {

        Optional<Post> post = pService.findById(id);
        if (post.isPresent() && post.get().getImageFile() != null) {

            Resource file = new InputStreamResource(post.get().getImageFile().getBinaryStream());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .contentLength(post.get().getImageFile().length()).body(file);

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping("/adminUpdates/editPostPage/{id}")
    public String showEditPost(Model model, @PathVariable long id) {
        Optional<Post> p=pService.findById(id);
        model.addAttribute("post", p.get());
        return "editPostPage";
    }

    private String getCurrentDate(){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yy");
        LocalDateTime now=LocalDateTime.now();
        String newDate=formatter.format(now);
        return newDate;
    }

    private PostType parseType(String theType){
        PostType ty=null;
        switch(theType){
            case "Guías":
                ty=PostType.Guides;
                break;
            case "Noticias":
                ty=PostType.News;
                break;
            case "Actualizaciones":
                ty=PostType.Updates;
                break;
        }
        return ty;
    }
    
    private void updateImage(Post post, boolean removeImage, MultipartFile imageField) throws IOException, SQLException {
		if (!imageField.isEmpty()) {
			post.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
			post.setImage(true);
		} else {
			if (removeImage) {
				post.setImageFile(null);
				post.setImage(false);
			} else {
				// Maintain the same image loading it before updating the book
				Post dbPost = pService.findById(post.getId()).orElseThrow();
				if (dbPost.isImage()) {
					post.setImageFile(BlobProxy.generateProxy(dbPost.getImageFile().getBinaryStream(),
                        dbPost.getImageFile().length()));
					post.setImage(true);
				}
			}
		}
	}
}
