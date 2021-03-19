package es.codeurjc.gameweb.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import es.codeurjc.gameweb.models.Chat;
import es.codeurjc.gameweb.models.Game;
import es.codeurjc.gameweb.models.Genres;
import es.codeurjc.gameweb.models.Post;
import es.codeurjc.gameweb.models.PostType;
import es.codeurjc.gameweb.models.User;
import es.codeurjc.gameweb.repositories.GameRepository;
import es.codeurjc.gameweb.repositories.PostRepository;
import es.codeurjc.gameweb.repositories.UserRepository;

@Service
public class DBInitializer {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @PostConstruct
	public void init() throws IOException, URISyntaxException {
        //sample games of 4dgames

        Game g1 =new Game("Stardew Valley", Genres.RPG,"¡Stardew Valley es un RPG de vida en el campo! Hereda la vieja granja de tu abuelo en Stardew Valley. Armado con herramientas de segunda mano y algo de dinero, te dispones a empezar tu nueva vida. ¿Podrás aprender a vivir de la tierra y a convertir esos campos de malezas en un hogar próspero? No va a ser fácil. Desde que Joja Corporation llegó a la ciudad, las antiguas formas de vivir han desaparecido. El centro comunitario, en su momento el centro de actividad más activo de la ciudad, se encuentra ahora en ruinas. Pero el valle se encuentra lleno de oportunidades. ¡Con un poco de dedicación, podrás ser uno de los que devuelva Stardew Valley a la gloria!");
		setGameImage(g1, "/sample_images/image-0.jpg");
        g1.setChat(new Chat());
        gameRepository.save(g1);

        Game g2 =new Game("Skyrim",Genres.RPG,"La agitación se extiende por el Imperio de Tamriel. El soberano de Skyrim ha sido asesinado. A medida que surgen aspirantes al trono, se van formando alianzas. En mitad de este conflicto, resurge un antiguo mal mucho más peligroso: los dragones, olvidados hace tiempo entre oscuros pasajes de los antiguos pergaminos, han regresado a Tamriel. El futuro de Skyrim, incluso el del propio Imperio, pende de un hilo, mientras aguardan la llegada vaticinada del Sangre de dragón, un héroe dotado del poder de La Voz, el único que puede hacer frente a los dragones.");
		setGameImage(g2, "/sample_images/image-1.jpg");
        g2.setChat(new Chat());
        gameRepository.save(g2);

        Game g3 =new Game("Hollow knight",Genres.Action,"Bajo la deteriorada ciudad de Petrópolis yace un antiguo reino en ruinas. A muchos les atrae la vida bajo la superficie y van en busca de riquezas, gloria o respuestas a viejos enigmas. Hollow Knight es una aventura de acción clásica en 2D ambientada en un vasto mundo interconectado. Explora cavernas tortuosas, ciudades antiguas y páramos mortales. Combate contra criaturas corrompidas, haz amistad con extraños insectos y resuelve los antiguos misterios que yacen en el corazón de reino.");
		setGameImage(g3, "/sample_images/image-2.jpg");
        Chat chat3 = new Chat();
        g3.setChat(chat3);
        gameRepository.save(g3);

        Game g4 =new Game("Dead by daylight",Genres.Horror,"Dead by Daylight es un juego de horror de multijugador (4 contra 1) en el que un jugador representa el rol del asesino despiadado y los 4 restantes juegan como supervivientes que intentan escapar de él para evitar ser capturados, torturados y asesinados.Los supervivientes juegan en tercera persona y tienen la ventaja de contar con una mejor percepción del entorno. El asesino juega en primera persona y está más enfocado en su presa.");
        setGameImage(g4, "/sample_images/image-3.jpg");
        Chat chat4 = new Chat();
        g4.setChat(chat4);
        gameRepository.save(g4);

        Game g5 =new Game("Call of duty black opss",Genres.Shooter,"Alex Mason, acompañado del sargento Frank Woods y del suboficial jefe segundo Joseph Bowman (dos amigos en común y compañeros de grupo de Mason) apoyados además por los grupos rebeldes cubanos, intentan asesinar a Fidel Castro en Bahía de Cochinos, durante la invasión a Cuba, como parte de la Operación 40. ​Usando la invasión como distracción para infiltrarse en la mansión de Castro, Mason y su equipo efectúan aparentemente con éxito el asesinato, sin embargo, la extracción sale mal, y este debe permanecer en el punto de extracción para lograr que el avión escape.");
        setGameImage(g5, "/sample_images/image-4.jpg");
        Chat chat5 = new Chat();
        g5.setChat(chat5);
        gameRepository.save(g5);
        
        Game g6 =new Game("Rachet and Clanck",Genres.Action,"El Presidente Drek está planeando crear un planeta para su raza, los Blarg, mediante la ruptura de otros planetas circundantes para conseguir material parta crear el nuevo planeta Blarg, sin importarle los seres vivos de esos planetas ahora condenados. Para detenerlo, Ratchet tratará de contactar con el Capitán Qwark, el héroe mas grande que haya conocido la galaxia.");
        setGameImage(g6, "/sample_images/image-5.jpg");
        Chat chat6 = new Chat();
        g6.setChat(chat6);
        gameRepository.save(g6);

        Game g7 =new Game("Phoenix Wright Ace Attorney",Genres.Narrative,"Phoenix Wright Ace Attorney es un juego perteneciente al género de aventura conversacional y la novela visual, creado por Shu Takumi2​ y publicado por Capcom, en el que los jugadores asumirán el papel de un abogado defensor en un tribunal de justicia ficticio, que se basa en el sistema legal japonés, el cual debe hacer que sus clientes sean declarados no culpables por medio de la investigación, la recogida de pruebas y los interrogatorios. ¿Estáis listos para el veredicto final?");
        setGameImage(g7, "/sample_images/image-6.jpg");
        Chat chat7 = new Chat();
        g7.setChat(chat7);
        gameRepository.save(g7);

        Game g8 =new Game("Final Fantasy XII",Genres.Action,"En Final Fantasy XII viajarás el gran mundo de Ivalice, donde reina la magia y los barcos voladores surcan los cielos. El reino de Dalmasca se halla en guerra, sumido en ruinas e incertidumbre. La princesa Ashe, la única heredera al trono, lucha en la resistencia para poner fin a la ocupación de su país. En compañía de Vaan, un joven que ha perdido a su familia en la guerra, estos dos inesperados aliados y sus compañeros se embarcan en una aventura heroica para recobrar su patria.");
        setGameImage(g8, "/sample_images/image-7.jpg");
        Chat chat8 = new Chat();
        g8.setChat(chat8);
        gameRepository.save(g8);

        Game g9 =new Game("Ark survival evolved",Genres.RPG,"Despiertas en la orilla de una isla misteriosa en la que debes aprender a sobrevivir. Usa tu inteligencia para matar o domar a las criaturas primitivas que vagan por el lugar. Encuentra otros jugadores para sobrevivir, ejercer tu dominio... ¡y escapar!");
        setGameImage(g9, "/sample_images/image-8.jpg");
        Chat chat9 = new Chat();
        g9.setChat(chat9);
        gameRepository.save(g9);

        Game g10 =new Game("Resident Evil 7",Genres.Horror,"Cuando Ethan abrió su correo, lo último que se esperaba encontrar era un mensaje de su mujer, desaparecida hace más de tres años. El mensaje era muy simple: Ven a buscarme, con una dirección adjunta que apuntaba a Luisiana. Este mensaje arrastró a Ethan hasta una decrépita mansión en el corazón de la ciénaga, un sitio realmente espeluznante, pero Ethan continuó su búsqueda hasta que un extraño lo atacó por sorpresa. Ethan no tiene ni idea de lo que le espera al despertar. Ahora es la presa de la familia Baker.");
        setGameImage(g10, "/sample_images/image-9.jpg");
        Chat chat10 = new Chat();
        g10.setChat(chat10);
        gameRepository.save(g10);

        Game g11 =new Game("The last of us 2",Genres.Horror,"En las afueras de Jackson (Wyoming), Joel relata los eventos de Salt Lake City, el escape de St. Mary's Hospital y la mentira que le dijo a Ellie para protegerla de la verdad; a su hermano Tommy. De regreso en Jackson, Joel visita a Ellie y le regala una guitarra, cumpliendo su promesa de enseñarle a tocar dicho instrumento. Cinco años después, Joel y Ellie se han asentado en una comunidad en Jackson. Allí, Ellie tiene un grupo de amigos e incluso un primer amor. Pero, un día la tranquilidad se desvanece y la joven inicia un viaje con dos objetivos: Vengar la muerte de un ser querido y la tranquilidad que le arrebataron.");
        setGameImage(g11, "/sample_images/image-10.jpg");
        Chat chat11 = new Chat();
        g11.setChat(chat11);
        gameRepository.save(g11);

        Game g12 =new Game("Spyro",Genres.RPG,"Al igual que en los videojuegos originales, el protagonista es Spyro, un pequeño dragón morado que cuenta con habilidades como escupir fuego, saltar, volar o embestir, que le permiten enfrentarse en diversas aventuras contra Gnasty Gnorc, Ripto y La Hechicera a lo largo de tres juegos recopilados en esta flamante trilogía.");
        setGameImage(g12, "/sample_images/image-11.jpg");
        Chat chat12 = new Chat();
        g12.setChat(chat12);
        gameRepository.save(g12);

        Game g13 =new Game("Smite",Genres.Action,"Smite es un juego multijugador-Online-Battle-Arena (MOBA), juego free-to-play desarrollado por Hi-Rez Studios. El juego consiste en dos equipos de cinco jugadores uno contra el otro en un intento de destruir la base del enemigo. Smite se basa en distintas tradiciones de varios Panteones mitológicos, representando a dichos personajes gráficamente. Al igual que otros juegos MOBA, Smite enfrenta a los jugadores en 5v5 entre sí, en un estadio cerrado. El objetivo del juego es destruir la base del otro equipo, en este caso representado por el enemigo Titan, un gran jefe que debe ser derrotado para el final. El primer equipo de destruir Titan del equipo enemigo gana.");
        setGameImage(g13, "/sample_images/image-12.jpg");
        Chat chat13 = new Chat();
        g13.setChat(chat13);
        gameRepository.save(g13);

        Game g14 =new Game("Shadow of the colossus",Genres.Puzzles,"El juego narra la épica historia de un joven, conocido únicamente como Wander (del inglés wanderer, 'alguien que deambula'), que debe viajar a caballo a través de un vasto territorio y derrotar a 16 gigantes, conocidos colectivamente como «Colossi» («colosos» en español) para devolver la vida a una joven.");
        setGameImage(g14, "/sample_images/image-13.jpg");
        Chat chat14 = new Chat();
        g14.setChat(chat14);
        gameRepository.save(g14);

        Game g15 =new Game("Rocket league",Genres.Action,"Rocket League es un videojuego que combina el fútbol con los vehículos. Fue desarrollado por Psyonix y lanzado el 7 de julio del 2015. Se encuentra disponible en español, tiene modos de juego cooperativo, de un jugador y en línea. Es la secuela de Supersonic Acrobatic Rocket-Powered Battle-Cars, juego lanzado para PlayStation 3 en 2008. Durante la fase de desarrollo, Rocket League llevaba como nombre tentativo Battle Cars 2.");
        setGameImage(g15, "/sample_images/image-14.jpg");
        Chat chat15 = new Chat();
        g15.setChat(chat15);
        gameRepository.save(g15);

        Game g16 =new Game("Uncharted 4",Genres.Action,"El retirado cazafortunas Nathan Drake vive felizmente su vida junto con su esposa Elena Fisher, pero todo se derrumba cuando aparece su hermano Sam, el que Nathan pensaba que estaba muerto. Sam necesita su ayuda para desenmascarar una conspiración histórica del famoso pirata aventurero Henry Avery y su legendario tesoro. Además, no son los únicos que buscan el tesoro, ya que Rafe Adler, multimillonario y exsocio de los hermanos Drake mientras estuvieron en una prisión, está buscándolo también, con la ayuda de Nadine Ross, la líder de la red de mercenarios Shoreline. Debido a que Nathan se siente culpable por haber dado por muerto a su hermano en el pasado, decide volver al mundo de los cazatesoros. ");
        setGameImage(g16, "/sample_images/image-15.jpg");
        Chat chat16 = new Chat();
        g16.setChat(chat16);
        gameRepository.save(g16);;
        Post p1=new Post("Noticia 1", "11/11/11", "11/11/11","Huey", "Esta es la noticia 1", PostType.News);
        setPostImage(p1, "/sample_images/post-image-1.jpg");
        g1.addPost(p1);
        postRepository.save(p1);
        Post p2=new Post("Noticia 2", "11/11/11", "11/11/11","Dewey", "Esta es la noticia 2", PostType.Updates);
        setPostImage(p2, "/sample_images/post-image-2.jpg");
        g2.addPost(p2);
        postRepository.save(p2);
        Post p3=new Post("Noticia 3", "11/11/11", "11/11/11","Louie", "Esta es la noticia 3", PostType.Guides);
        setPostImage(p3, "/sample_images/post-image-3.jpg");
        g3.addPost(p3);
        postRepository.save(p3);
        Post p4=new Post("Noticia 4", "11/11/11", "11/11/11","Webby", "Esta es la noticia 4", PostType.News);
        setPostImage(p4, "/sample_images/post-image-4.jpg");
        g4.addPost(p4);
        postRepository.save(p4);

        ArrayList<Long> suscriptions = new ArrayList<>();
        suscriptions.add(g2.getId());
		User user1 = new User("Kike", "12345", suscriptions);
		user1.setAdmin(true);
        user1.setRoles();
        setUserImage(user1, "/sample_images/user-image-0.jpg");
        userRepository.save(user1);

		User user2 = new User("Pepe", "54321", null);
		user2.setAdmin(false);
        user2.setRoles();
        setUserImage(user2, "/sample_images/user-image-1.jpg");
        userRepository.save(user2);

    }

    public void setGameImage(Game game, String classpathResource) throws IOException {
        game.setImage(true);
        Resource image = (Resource) new ClassPathResource(classpathResource);
        game.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));

    }
    public void setPostImage(Post post, String classpathResource)throws IOException{
        post.setImage(true);
        Resource image = (Resource) new ClassPathResource(classpathResource);
        post.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));
    }

    public void setUserImage(User user, String classpathResource) throws IOException {
        user.setImage(true);
        Resource image = (Resource) new ClassPathResource(classpathResource);
        user.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.contentLength()));

    }

}