package es.codeurjc.gameweb.rest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
 
import org.apache.catalina.connector.Response;
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
@RequestMapping("/api/chats")
public class ChatControllerREST {
    @Autowired
    private GameService gamePostService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserService userService;
    interface ChatDetail extends Chat.chatBasic,Game.gameBasico{}
    @JsonView(ChatDetail.class)
    @GetMapping("/")
    private Collection<Chat> getAllChats(){
        return chatService.findAll();
    }
    @JsonView(ChatDetail.class)
    @GetMapping("/{id}")
    private ResponseEntity<Chat> getIndividualChat(@PathVariable long id){
        Chat chat= chatService.findById(id).get();
        if(chat!=null){
            return ResponseEntity.ok(chat);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
    @JsonView(ChatDetail.class)
    @PostMapping("/")
    private ResponseEntity<Chat> createChat(@RequestParam int gameID){
        Game game=gamePostService.findById(gameID).get();
        if(game!=null){
            Chat theChat=new Chat();
            URI location=fromCurrentRequest().path("/{id}").buildAndExpand(theChat.getID()).toUri();
            game.setChat(theChat);
            chatService.save(theChat);
            gamePostService.save(game);
            return ResponseEntity.created(location).body(theChat);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
    @JsonView(ChatDetail.class)
    @DeleteMapping("/{id}")
    private ResponseEntity<Chat> deleteChat(@PathVariable int id){
        Chat chat= chatService.findById(id).get();
        if(chat!=null){
            chatService.delete(id);
            return ResponseEntity.ok(chat);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
    @JsonView(ChatDetail.class)
    @PutMapping("/{id}")
    private ResponseEntity<Chat> addMessage(@PathVariable int id,@RequestParam String message,HttpServletRequest request){
        Game game=gamePostService.findById(id).get();
        if(game!=null){
            Chat chat=game.getChat();
            Principal principal = request.getUserPrincipal();
            User user =userService.findByName(principal.getName()).get();
            Message MyMessage = new Message(user.getInfo(), message,true);
            chat.getListMessages().add(MyMessage);
            chatService.save(chat);
            gamePostService.save(game);
            return ResponseEntity.ok(chat);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}