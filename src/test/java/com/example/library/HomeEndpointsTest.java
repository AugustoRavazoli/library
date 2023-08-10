package com.example.library;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@ActiveProfiles("test")
public class HomeEndpointsTest extends EndpointsTestTemplate {

  @Test
  @DisplayName("Retrieve home page")
  void retrieveHomePage() throws Exception {
    // when
    client.perform(get("/"))
    // then
    .andExpectAll(
      status().isOk(),
      view().name("home")
    );
  }

}
