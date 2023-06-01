package com.m19y.restfull.service;

import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.RegisterUserRequest;
import com.m19y.restfull.model.request.UpdateUserRequest;
import com.m19y.restfull.model.response.UserResponse;

public interface UserService {
  void register(RegisterUserRequest request);

  UserResponse get(User user);
  UserResponse update(User user, UpdateUserRequest request);
}
