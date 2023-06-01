package com.m19y.restfull.service;

import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.LoginUserRequest;
import com.m19y.restfull.model.response.TokenResponse;
import com.m19y.restfull.repository.UserRepository;
import com.m19y.restfull.security.BCrypt;
import com.m19y.restfull.util.Utils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService{

  @Autowired
  private UserRepository repository;

  @Autowired
  private Utils utilsService;

  @Override
  @Transactional
  public TokenResponse login(LoginUserRequest request){
    utilsService.validate(request);

    User user = repository.findById(request.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "This username maybe not exist"));

    if(BCrypt.checkpw(request.getPassword(), user.getPassword())){

      // sukses login
      user.setToken(UUID.randomUUID().toString());
      user.setTokenExpiredAt(utilsService.next30Days());
      repository.save(user);

      return TokenResponse.builder()
              .token(user.getToken())
              .expiredAt(user.getTokenExpiredAt())
              .build();
    }else{
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You send wrong password");
    }
  }

  @Override
  @Transactional
  public void logout(User user){
    user.setTokenExpiredAt(null);
    user.setToken(null);

    repository.save(user);
  }
}
