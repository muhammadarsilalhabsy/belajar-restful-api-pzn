package com.m19y.restfull.controller;

import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.RegisterUserRequest;
import com.m19y.restfull.model.request.UpdateUserRequest;
import com.m19y.restfull.model.response.UserResponse;
import com.m19y.restfull.model.response.WebResponse;
import com.m19y.restfull.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/users")
public class UserController {

  @Autowired
  private UserServiceImpl userService;


  @PostMapping(
          path = "/register",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> register(@RequestBody RegisterUserRequest request) {
    userService.register(request);
    return WebResponse.<String>builder().data("OK").build();
  }

  @GetMapping(
          path = "/current",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<UserResponse> get(User user){
    UserResponse response = userService.get(user);
    return WebResponse.<UserResponse>builder()
            .data(response)
            .build();
  }

  @PatchMapping(
          path = "/current",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request){
    UserResponse response = userService.update(user, request);
    return WebResponse.<UserResponse>builder()
            .data(response)
            .build();
  }

}
