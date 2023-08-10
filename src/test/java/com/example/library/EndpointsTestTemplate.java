package com.example.library;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public abstract class EndpointsTestTemplate {

  protected MockMvc client;

  @BeforeEach
  void setUp(WebApplicationContext context) {
    client = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .build();
  }

}
