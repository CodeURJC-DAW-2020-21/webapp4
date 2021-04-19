package es.codeurjc.gameweb.controllers;
 
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
 
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
 
import es.codeurjc.gameweb.models.Game;
import es.codeurjc.gameweb.models.User;
import es.codeurjc.gameweb.services.GameService;
import es.codeurjc.gameweb.services.UserService;
 
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
 
 
 
@Controller
public class ProfileController {
 
    @Autowired
    private UserService userService;
 
    @Autowired
    private GameService gamePostService;
 
    @Autowired
	private PasswordEncoder passwordEncoder;
 
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
 
    @PostMapping("/profile/changeName")
    public String changeName(Model model, @RequestParam String name, HttpServletRequest request) throws ServletException {
        Principal principal = request.getUserPrincipal();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get();
        List<User> users = userService.findAll();
        boolean encontrado= false;
        for(int i=0; i<users.size(); i++){
            if(users.get(i).getInfo().equals(name)){
                encontrado= true;
            }
        }
        if(!encontrado){
            user.setInfo(name);
            userService.save(user);
            model.addAttribute("user", user);
            request.logout();
            model.addAttribute("customMessage", "Se cerrará sesión para evitar errores");
            return "successPage";
        }
        model.addAttribute("customMessage", "Ya existe ese nombre de usuario");
        return "successPage";
    }
 
    @RequestMapping("/profile/") 
    public String showProfile(Model model, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get();
        model.addAttribute("user", user);
        return "profile";
    }
 
    @GetMapping("/profile/subscriptions/")
    public String showSubscriptions(Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get(); 
        ArrayList<Game> myGames = new ArrayList<>();
        if(user.getMyGames()==null){
            myGames = null;
        }
        else{
            for(int i=0; i<user.getMyGames().size(); i++){
                myGames.add(gamePostService.findById(user.getMyGames().get(i)).get());
            }
        }
        model.addAttribute("listaSubs",myGames);
        if(user.getMyGames().isEmpty()){
            model.addAttribute("noSubs", "No tienes ninguna subscripcion");
        }
        else{
            model.addAttribute("noSubs", "");
        }
        return "subscriptions";
    }
 
    @PostMapping("/profile/changePass")
    public String changePass(Model model, @RequestParam String password, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get();
        user.setPassword(passwordEncoder.encode(password));
        userService.save(user);
        model.addAttribute("user", user);
        return "profile";
    }
 
    @PostMapping("/profile/changeProfilePhoto")
	public String newFotoUser(Model model, MultipartFile image, HttpServletRequest request) throws IOException, SQLException {
        Principal principal = request.getUserPrincipal();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get();
        updateImage(user, true, image);
        userService.save(user);
        model.addAttribute("user", user);
		return "profile";
	}
 
    @RequestMapping("/profile/deleteSubscription/{id}")
    public String eliminarSubs(Model model, @PathVariable Long id,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get();
        ArrayList<Game> myGames = new ArrayList<>();
        for(int i=0; i<user.getMyGames().size(); i++){
            if(user.getMyGames().get(i).equals(id)){
                user.getMyGames().remove(user.getMyGames().get(i));
                userService.save(user);
            }
            else{
                myGames.add(gamePostService.findById(user.getMyGames().get(i)).get());
            }
        }
        model.addAttribute("listaSubs", myGames);
        if(user.getMyGames().isEmpty()){
            model.addAttribute("noSubs", "No tienes ninguna subscripcion");
        }
        else{
            model.addAttribute("noSubs", "");
        }
        return "subscriptions";
    } 
 
    @GetMapping("/profile/image")
	public ResponseEntity<Object> downloadUserImage(HttpServletRequest request) throws SQLException {
        Principal principal = request.getUserPrincipal();
        Optional<User> myUser= userService.findByName(principal.getName());
        User user =myUser.get();
		if (user.getImageFile() != null) {
 
			Resource file = new InputStreamResource(user.getImageFile().getBinaryStream());
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(user.getImageFile().length()).body(file);
 
		} else {
			return ResponseEntity.notFound().build();
		}
	}
 
    private void updateImage(User user, boolean removeImage, MultipartFile imageField) throws IOException, SQLException {
		if (!imageField.isEmpty()) {
			user.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
			user.setImage(true);
		} else {
			if (removeImage) {
				user.setImageFile(null);
				user.setImage(false);
			} else {
				User dbUser = userService.findById(user.getId()).orElseThrow();
				if (dbUser.isImage()) {
					user.setImageFile(BlobProxy.generateProxy(dbUser.getImageFile().getBinaryStream(),
							dbUser.getImageFile().length()));
					user.setImage(true);
				}
			}
		}
	}
 
}