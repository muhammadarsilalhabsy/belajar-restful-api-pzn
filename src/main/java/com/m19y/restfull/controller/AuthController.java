package com.m19y.restfull.controller;

import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.LoginUserRequest;
import com.m19y.restfull.model.response.TokenResponse;
import com.m19y.restfull.model.response.WebResponse;
import com.m19y.restfull.service.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

  @Autowired
  private AuthServiceImpl authServiceImpl;

  @PostMapping(path = "/login",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request){
    TokenResponse tokenResponse = authServiceImpl.login(request);

    return WebResponse.<TokenResponse>builder()
            .data(tokenResponse)
            .build();
  }

  @DeleteMapping(
          path = "/logout",
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<String> logout(User user){
    authServiceImpl.logout(user);
    return WebResponse.<String>builder()
            .data("OK")
            .build();
  }


}
