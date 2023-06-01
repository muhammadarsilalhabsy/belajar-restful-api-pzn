package com.m19y.restfull.service;

import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.RegisterUserRequest;
import com.m19y.restfull.model.request.UpdateUserRequest;
import com.m19y.restfull.model.response.UserResponse;
import com.m19y.restfull.repository.UserRepository;
import com.m19y.restfull.security.BCrypt;
import com.m19y.restfull.util.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService{

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private Utils utilsService;

  @Transactional
  @Override
  public void register(RegisterUserRequest request) {

    utilsService.validate(request);

    // check if users with username is already use
    if(userRepository.existsById(request.getUsername())){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Username Already Registered");
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    user.setName(request.getName());

    userRepository.save(user);
  }

  @Override
  public UserResponse get(User user) {

    return UserResponse.builder()
            .username(user.getUsername())
            .name(user.getName())
            .build();
  }

  @Transactional
  @Override
  public UserResponse update(User user, UpdateUserRequest request) {
    utilsService.validate(request);

    if(Objects.nonNull(request.getName())){
      user.setName(request.getName());
    }
    if(Objects.nonNull(request.getPassword())){
      user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
    }
    userRepository.save(user);
    return UserResponse.builder()
            .name(user.getName())
            .username(user.getUsername())
            .build();
  }
}
