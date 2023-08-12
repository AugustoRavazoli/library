package com.example.library.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;

@Controller
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;
	
  public UserController(UserService userService, UserMapper userMapper) {
    this.userService = userService;
    this.userMapper = userMapper;
  }

  @GetMapping("/register")
  public String registerPage(Model model) {
    model.addAttribute("user", new UserRequest());
    return "user/register";
  }
	
  @PostMapping("/register")
  public String registerUser(
    @Valid @ModelAttribute("user") UserRequest userRequest,
    BindingResult result
  ) {
    if (result.hasErrors()) {
      return "user/register";
    }
    try {
      userService.registerUser(userMapper.toEntity(userRequest));
    } catch (UsernameTakenException ex) {
      result.rejectValue("username", "duplicate", ex.getMessage());
      return "user/register";
    }
    return "user/success";
  }
	
  @GetMapping("/login")
  public String loginPage() {
    return "user/login";
  }

}
