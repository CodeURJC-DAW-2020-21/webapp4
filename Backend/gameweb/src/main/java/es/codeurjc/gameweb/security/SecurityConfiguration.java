package es.codeurjc.gameweb.security;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	RepositoryUserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10, new SecureRandom());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// Public pages
		http.authorizeRequests().antMatchers("/").permitAll();
		http.authorizeRequests().antMatchers("/LogInPage").permitAll();
		http.authorizeRequests().antMatchers("/error").permitAll();
		http.authorizeRequests().antMatchers("/logout").permitAll();
		http.authorizeRequests().antMatchers("/error-404").permitAll();
		http.authorizeRequests().antMatchers("/error-500").permitAll();
		http.authorizeRequests().antMatchers("/expandedPost").permitAll();
		http.authorizeRequests().antMatchers("/GameList").permitAll();
		http.authorizeRequests().antMatchers("/GamePage").permitAll();
		http.authorizeRequests().antMatchers("/gamestadistics").permitAll();
		http.authorizeRequests().antMatchers("/listPost").permitAll();
		http.authorizeRequests().antMatchers("/morePosts").permitAll();
		http.authorizeRequests().antMatchers("/showMoreGames").permitAll();

		// Private pages (all other pages)
		http.authorizeRequests().antMatchers("/adminUpdates").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers("/createPostPage").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers("/editGame").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers("/newGame").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers("/Profile").hasAnyRole("USER","ADMIN");
		http.authorizeRequests().antMatchers("/Subscriptions").hasAnyRole("USER","ADMIN");	


		// Login form
		http.formLogin().loginPage("/LogInPage");
		http.formLogin().usernameParameter("username");
		http.formLogin().passwordParameter("password");
		http.formLogin().failureUrl("/loginerror");

		// Logout
		http.logout().logoutUrl("/logout");
		http.logout().logoutSuccessUrl("/");

		

	}
}
