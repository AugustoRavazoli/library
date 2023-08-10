package com.example.library.user;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import com.example.library.EndpointsTestTemplate;

@SpringBootTest
@ActiveProfiles("test")
class UserEndpointsTest extends EndpointsTestTemplate {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
  }

  @Nested
  @DisplayName("User registration tests")
  class RegisterUserEndpointTests {
    
    @Test
    @DisplayName("Retrieve registration page")
    void retrieveRegistrationPage() throws Exception {
      // when
      client.perform(get("/register"))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("user", allOf(
          hasProperty("username"),
          hasProperty("password")
        )),
        view().name("user/register")
      );
    }

    @Test
    @DisplayName("Register user")
    void registerUser() throws Exception {
      // when
      client.perform(post("/register")
        .param("username", "username")
        .param("password", "password")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        view().name("user/success")
      );
      assertThat(userRepository.findAll()).size().isEqualTo(1)
        .returnToIterable()
        .first()
        .matches(user -> passwordEncoder.matches("password", user.getPassword()))
        .extracting("username", "role", "enabled")
        .doesNotContainNull()
        .containsExactly("username", Role.USER, true);
    }

    @Test
    @DisplayName("Don't register user with username taken")
    void doNotRegisterUserWithUsernameTaken() throws Exception {
      // given
      userRepository.save(new User("username", "password"));
      // when
      client.perform(post("/register")
        .param("username", "username")
        .param("password", "password")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrorCode("user", "username", "duplicate"),
        view().name("user/register")
      );
      assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Don't register user with blank fields")
    void doNotRegisterUserWithBlankFields() throws Exception {
      // when
      client.perform(post("/register")
        .param("username", " ")
        .param("password", " ")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrors("user", "username"),
        model().attributeHasFieldErrors("user", "password"),
        view().name("user/register")
      );
      assertThat(userRepository.count()).isZero();
    }

  }
  
  @Nested
  @DisplayName("User login tests")
  class LoginUserEndpointTests {
  	
  	@Test
  	@DisplayName("Retrieve login page")
  	void retrieveLoginPage() throws Exception {
  		// when
  		client.perform(get("/login"))
  		// then
  		.andExpectAll(
  			status().isOk(),
  			view().name("user/login")
  		);
  	}
  	
  	@Test
  	@DisplayName("Login user")
  	void loginUser() throws Exception {
  		// given
  		userRepository.save(new User("username", passwordEncoder.encode("password")));
  		// when
  		client.perform(post("/login")
  			.param("username", "username")
        .param("password", "password")
        .with(csrf())
  		)
  		// then
  		.andExpectAll(
  			status().isFound(),
  			redirectedUrl("/list-books")
  		);
  	}
  	
  	private static Stream<Arguments> provideUsernameAndPassword() {
      return Stream.of(
      	Arguments.of("username", "pass"),
        Arguments.of("usr", "password"),
        Arguments.of("usr", "pass")
      );
  	}
  	
  	@ParameterizedTest
  	@MethodSource("provideUsernameAndPassword")
  	@DisplayName("Don't login user with invalid username or password")
  	void DoNotLoginUserWithInvalidUsernameOrPassword(String username, String password) throws Exception {
  		// given
  		userRepository.save(new User("username", passwordEncoder.encode("password")));
  		// when
  		client.perform(post("/login")
        .param("username", username)
        .param("password", password)
        .with(csrf())
  		)
  		// then
  		.andExpectAll(
  			status().isFound(),
  			redirectedUrl("/login?error")
  		);
  	}
  
  }
  
  @Nested
  @DisplayName("User logout tests")
  class LogoutUserEndpointTests {
  	
  	@Test
  	@DisplayName("Logout user")
  	void logoutUser() throws Exception {
  		// when
  		client.perform(post("/logout").with(csrf()))
  		// then
  		.andExpectAll(
  			status().isFound(),
  			redirectedUrl("/login?logout")
  		);
  	}
  	
  }

}
