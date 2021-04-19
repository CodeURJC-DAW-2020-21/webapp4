package es.codeurjc.gameweb.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.sql.Blob;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
public class Game {

    public interface gameBasico{}

    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(gameBasico.class)
    private Long id;

    @Lob
    @JsonIgnore
	private Blob imageFile;
    @JsonIgnore
	private boolean image;

    @JsonView(gameBasico.class)
    private String gameTitle;
    @JsonView(gameBasico.class)
    private Genres genre;
    @Column(columnDefinition = "LONGBLOB")
    HashMap<Long, Integer> mapScores = new HashMap<Long, Integer>();
    @JsonView(gameBasico.class)
    private float averageScore;
    
    @Column(columnDefinition = "TEXT")
    @JsonView(gameBasico.class)
    private String description;
    @OneToOne(cascade=CascadeType.ALL)
    private Chat chat;
    @OneToMany(mappedBy = "fromGame",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Post> thePosts=new ArrayList<>();

    @JsonView(gameBasico.class)
    private String imagePath;
 
    public String getImagePath() {
        return this.imagePath;
    }
 
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Game(){}

    public Game(String gameTitle, Genres genre, String description) {
        super();
        this.gameTitle = gameTitle;
        this.genre = genre;
        this.description = description;
        this.mapScores.put((long) 0, 0);
    }
    public List<Post> getThePosts() {
        return this.thePosts;
    }
    public void addPost(Post p){
        thePosts.add(p);
        p.setFromGame(this);
    }
    public void removePost(Post p){
        thePosts.remove(p);
        p.setFromGame(null);
    }
    public void setThePosts(List<Post> thePosts) {
        this.thePosts = thePosts;
    }
    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Genres getGenre() {
        return genre;
    }

    public void setGenre(Genres genre) {
        this.genre = genre;
    }

    public float getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
    
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Blob getImageFile() {
        return imageFile;
    }

    public void setImageFile(Blob blob) {
        this.imageFile = blob;
    }

    public boolean isImage() {
        return this.image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public HashMap<Long, Integer> getMapScores() {
        return mapScores;
    }

    public void setMapScores(HashMap<Long, Integer> mapScores) {
        this.mapScores = mapScores;
    }

}
