package es.codeurjc.gameweb.rest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
 
import es.codeurjc.gameweb.models.*;
import es.codeurjc.gameweb.repositories.PostRepository;
import es.codeurjc.gameweb.services.*;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;
 
import javax.servlet.http.HttpServletRequest;
 
import com.fasterxml.jackson.annotation.JsonView;
@RestController
@RequestMapping("/api/posts")
public class PostsControllerREST {
    private static final String POSTS_FOLDER = "posts";
    @Autowired
    private PostService pService;    
    @Autowired
    private GameService gamePostService;
    @Autowired
    private PostRepository postRepo;
    @Autowired
    private ImageService imageService;
    interface PostDetail extends Post.postBasic,Post.games,Game.gameBasico{}
    @JsonView(PostDetail.class)
    @GetMapping("/")
    public Collection<Post> getPosts(){
        return pService.findAll();
    }
    @JsonView(PostDetail.class)
    @GetMapping("/game")
    public Collection<Post> getPostsOfGame(@RequestParam int gameID){
        Game myGame=gamePostService.findById(gameID).get();
        return pService.findPostOfGame(myGame);
 
    }
    @JsonView(PostDetail.class)
    @GetMapping("/type")
    public Collection<Post> getPostsOfType(@RequestParam String theType){
        PostType type=null;
        switch(theType){
            case "News":
                type=PostType.News;
                break;
            case "Updates":
                type=PostType.Updates;
                break;
            case "Guides":
                type=PostType.Guides;
                break;
        }
        ArrayList<Post> aux=new ArrayList<Post>();
        List<Post> thePosts=pService.findAll();
        for(Post p : thePosts){
            aux.add(p);
        }
        return pService.findPostOfType(aux,type);
 
    }
    @JsonView(PostDetail.class)
    @GetMapping("/{id}")
    public ResponseEntity<Post> getIndividualPost(@PathVariable long id){
        Post post=pService.findById(id).get();
        if(post!=null){
            return ResponseEntity.ok(post);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/")
    public ResponseEntity<Post> createPost(@RequestBody Post post){
 
        pService.save(post);
        URI location=fromCurrentRequest().path("/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).body(post);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Post> deletePost(@PathVariable long id){
        Post post=pService.findById(id).get();
        if(post!=null){
            pService.deleteById(id);
            return ResponseEntity.ok(post);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{id}/image")
    public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException {
        Post post=pService.findById(id).get();
        if(post!=null){
            URI location = fromCurrentRequest().build().toUri();
 
            post.setImagePath(location.toString());
            pService.save(post);
 
            imageService.saveImage(POSTS_FOLDER, post.getId(), imageFile);
            return ResponseEntity.created(location).build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
        
    }
    @PutMapping("/{id}")
    public ResponseEntity<Post> editPost(@PathVariable long id, @RequestBody Post newPost) {
        Post p = pService.findById(id).get();
 
        if (p != null) {
 
            newPost.setId(id);
            pService.save(newPost);
 
            return ResponseEntity.ok(p);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	@GetMapping("/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws MalformedURLException {
 
		return this.imageService.createResponseFromImage(POSTS_FOLDER, id);
	}
 
}