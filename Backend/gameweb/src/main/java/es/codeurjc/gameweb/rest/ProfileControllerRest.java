package es.codeurjc.gameweb.rest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;

import javax.imageio.IIOException;

import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import es.codeurjc.gameweb.models.User;
import es.codeurjc.gameweb.models.User.userBasico;
import es.codeurjc.gameweb.services.ImageService;
import es.codeurjc.gameweb.services.UserService;

@RestController
@RequestMapping("/api")
public class ProfileControllerRest {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    private static final String POSTS_FOLDER = "usersPics";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @JsonView(userBasico.class)
    @GetMapping("/profiles/")
    public Collection<User> getAllUsers(){
 
        return userService.findAll();
    }

    @JsonView(userBasico.class)
    @GetMapping("/profiles/{id}")
    public ResponseEntity<User> getUser(@PathVariable long id){

        Optional<User> user = userService.findById(id);

        if(user.get() != null){
            return ResponseEntity.ok(user.get());
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @JsonView(userBasico.class)
    @PostMapping("/profiles/")
    public ResponseEntity<User> createUser(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
 
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
 
        return ResponseEntity.created(location).body(user);
    }

    /*@DeleteMapping("/profile/{id}/subscriptions/")
    public ResponseEntity<User> deleteUser(@PathVariable long id){

        Optional<User> user = userService.findById(id);

        if(user.get() != null){
            userService.delete(id);
            return ResponseEntity.ok(user.get());
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }*/

    @JsonView(userBasico.class)
    @PutMapping("/profiles/{id}")
    public ResponseEntity<User> editUser(@PathVariable long id, @RequestBody User newUser){
        Optional<User> user = userService.findById(id);
 
        if( user.get() != null){
 
            newUser.setId(id);
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            userService.save(newUser);
 
            return ResponseEntity.ok(user.get());
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profiles/{id}/image")
	public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
        User u=userService.findById(id).get();
        if(u!=null){
            URI location = fromCurrentRequest().build().toUri();
 
            u.setImagePath(location.toString());
            userService.save(u);
 
            imageService.saveImage(POSTS_FOLDER, u.getId(), imageFile);
            return ResponseEntity.created(location).build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
 
	}
    @GetMapping("/profiles/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws MalformedURLException {
 
		return this.imageService.createResponseFromImage(POSTS_FOLDER, id);
    }

}
