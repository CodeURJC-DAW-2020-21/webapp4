package es.codeurjc.gameweb.models;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Entity
@DynamicUpdate
@Table(name="users")
public class User {

    public interface userBasico{}

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(userBasico.class)
    private Long id;

    @Lob
    @JsonIgnore
	private Blob imageFile;
    @JsonIgnore
	private boolean image;
    @JsonView(userBasico.class)
    private String info;
    @JsonIgnore
    private String password;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @JsonView(userBasico.class)
	private List<String> roles;
    @Column(columnDefinition = "LONGBLOB")
    @JsonView(userBasico.class)
    private ArrayList<Long> myGames;

    @JsonView(userBasico.class)
    private String imagePath;
 
    public String getImagePath() {
        return this.imagePath;
    }
 
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public User(){}

    public User(String info, String password, ArrayList<Long> myGames,String... roles) {
        super();
        this.info = info;
        this.password = password;
        this.myGames= myGames;
        this.roles = List.of(roles);
    }
    

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public ArrayList<Long> getMyGames(){
        return this.myGames;
    }
    public void setMyGames(ArrayList<Long> myGames){
        this.myGames=myGames;
    }
    
    public void addElementToGameList(Long long1){
        if(this.myGames==null){
            this.myGames=new ArrayList<Long>();
            this.myGames.add(long1);
        }
        else{
            this.myGames.add(long1);
        }
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String contraseña) {
        this.password = contraseña;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Blob getImageFile() {
        return this.imageFile;
    }

    public void setImageFile(Blob imageFile) {
        this.imageFile = imageFile;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}