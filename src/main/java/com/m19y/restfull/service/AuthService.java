package com.m19y.restfull.service;

import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.LoginUserRequest;
import com.m19y.restfull.model.response.TokenResponse;

public interface AuthService {
  TokenResponse login(LoginUserRequest request);
  void logout(User user);
}
