package com.m19y.restfull.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.LoginUserRequest;
import com.m19y.restfull.model.response.TokenResponse;
import com.m19y.restfull.model.response.WebResponse;
import com.m19y.restfull.repository.UserRepository;
import com.m19y.restfull.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

  private final MockMvc mockMvc;

  private final UserRepository repository;

  private final ObjectMapper objectMapper;

  @Autowired
  AuthControllerTest(MockMvc mockMvc, UserRepository repository, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  @BeforeEach
  void setUp() {
//    menghapus semua data di database, supaya datanya fress dari awal
    repository.deleteAll();
  }

  @Test
  void loginFailedUserNotFound() throws Exception {
    LoginUserRequest request = new LoginUserRequest();
    request.setPassword("wkwkw");
    request.setUsername("wkwkw");


    mockMvc.perform(
            post("/api/v1/auth/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
      assertEquals("This username maybe not exist", response.getErrors());
      });

  }

  @Test
  void loginPasswordWrong() throws Exception {

    User user = new User();
    user.setName("aku");
    user.setPassword(BCrypt.hashpw("aku", BCrypt.gensalt()));
    user.setUsername("aku");
    repository.save(user);

    LoginUserRequest request = new LoginUserRequest();
    request.setPassword("wkwkw");
    request.setUsername("aku");


    mockMvc.perform(
            post("/api/v1/auth/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
      assertEquals("You send wrong password", response.getErrors());
    });

  }

  @Test
  void loginSuccess() throws Exception {
    User user = new User();
    user.setName("wkwkw");
    user.setPassword(BCrypt.hashpw("wkwkw", BCrypt.gensalt()));
    user.setUsername("wkwkw");
    repository.save(user);

    LoginUserRequest request = new LoginUserRequest();
    request.setPassword("wkwkw");
    request.setUsername("wkwkw");


    mockMvc.perform(
            post("/api/v1/auth/login")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<TokenResponse> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });
      assertNull(response.getErrors());
      assertNotNull(response.getData());

      User userDb = repository.findById(user.getUsername()).orElse(null);
      assertEquals(userDb.getToken(), response.getData().getToken());
      assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
    });

  }

  @Test
  void LogoutFailedOrUnauthorized() throws Exception {
    mockMvc.perform(
            delete("/api/v1/auth/logout")
                    .accept(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {

              });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void logoutSuccess() throws Exception{
    User user = new User();
    user.setToken("ada");
    user.setUsername("ada");
    user.setPassword("ada");
    user.setName("ada");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
    repository.save(user);

    mockMvc.perform(
            delete("/api/v1/auth/logout")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "ada")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {

              });

      assertNull(response.getErrors());
      assertEquals("OK", response.getData());

      User userFromDB = repository.findById(user.getUsername()).orElse(null);
      assertNotNull(userFromDB);
      assertNull(userFromDB.getToken());
      assertNull(userFromDB.getTokenExpiredAt());
    });
  }
}