package com.example.library;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@ActiveProfiles("test")
class AuthorizationTests extends EndpointsTestTemplate {
	
	private static Stream<Arguments> provideAllowedUrlsForAnyUser() {
		return Stream.of(
			Arguments.of("/error")
		);
	}

  private static Stream<Arguments> provideAllowedUrlsForAnonymousUser() {
    return Stream.of(
      Arguments.of("/"),
      Arguments.of("/register"),
      Arguments.of("/login")
    );
  };

  private static Stream<Arguments> provideAllowedUrlsForAuthenticatedUser() {
    return Stream.of(
      Arguments.of("/create-book"),
      Arguments.of("/list-books"),
      Arguments.of("/edit-book"),
      Arguments.of("/delete-book"),
      Arguments.of("/create-category"),
      Arguments.of("/list-categories"),
      Arguments.of("/edit-category"),
      Arguments.of("/delete-category"),
      Arguments.of("/logout")
    );
  };

  @ParameterizedTest
  @MethodSource({ "provideAllowedUrlsForAnonymousUser", "provideAllowedUrlsForAnyUser"})
  @DisplayName("Anonymous user can access endpoints")
  void anonymousUserCanAccessEndpoints(String url) throws Exception {
    // when
    client.perform(options(url))
    // then
    .andExpect(status().is(not(401)));
  }

  @ParameterizedTest
  @MethodSource("provideAllowedUrlsForAuthenticatedUser")
  @DisplayName("Anonymous user can't access endpoints")
  void anonymousUserCanNotAccessEndpoints(String url) throws Exception {
    // when
    client.perform(options(url))
    // then
    .andExpect(status().isUnauthorized());
  }

  @WithMockUser
  @ParameterizedTest
  @MethodSource({ "provideAllowedUrlsForAuthenticatedUser", "provideAllowedUrlsForAnyUser" })
  @DisplayName("Authenticated user can access endpoints")
  void authenticatedUserCanAccessEndpoints(String url) throws Exception {
    // when
    client.perform(options(url))
    // then
    .andExpect(status().is(not(401)));
  }

  @WithMockUser
  @ParameterizedTest
  @MethodSource("provideAllowedUrlsForAnonymousUser")
  @DisplayName("Authenticated user can't access endpoints")
  void authenticatedUserCanNotAccessEndpoints(String url) throws Exception {
    // when
    client.perform(options(url))
    // then
    .andExpect(status().isForbidden());
  }

}
