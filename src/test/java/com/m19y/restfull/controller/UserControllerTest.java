package com.m19y.restfull.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.RegisterUserRequest;
import com.m19y.restfull.model.request.UpdateUserRequest;
import com.m19y.restfull.model.response.UserResponse;
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
class UserControllerTest {

  private final MockMvc mvc;
  private final UserRepository repository;
  private final ObjectMapper mapper;

  @Autowired
  UserControllerTest(MockMvc mvc, UserRepository repository, ObjectMapper mapper) {
    this.mvc = mvc;
    this.repository = repository;
    this.mapper = mapper;
  }

  @BeforeEach
  void setUp() {
//    menghapus semua data di database, supaya datanya fress dari awal
    repository.deleteAll();
  }

  @Test
  void testRegisterSuccess() throws Exception{
    RegisterUserRequest userRequest = new RegisterUserRequest();
    userRequest.setName("ikbal");
    userRequest.setUsername("mabok");
    userRequest.setPassword("rahasia");

    mvc.perform(
            post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(userRequest))
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
      assertEquals("OK", response.getData());
    });
  }

  @Test
  void testRegisterFailed() throws Exception{
    RegisterUserRequest userRequest = new RegisterUserRequest();
    userRequest.setName("ada");
    userRequest.setUsername(""); // filed
    userRequest.setPassword("asdfa");

    mvc.perform(
            post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(userRequest))
    ).andExpectAll(
            status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void testRegisterDuplicate() throws Exception{

    User user = new User();
    user.setName("wwkwek");
    user.setUsername("sama"); // duplicate
    user.setPassword("ekrgjsodfmg");
    repository.save(user);

    RegisterUserRequest userRequest = new RegisterUserRequest();
    userRequest.setName("asdfasdf");
    userRequest.setUsername("sama"); // duplicate
    userRequest.setPassword("xgfdwrge");

    mvc.perform(
            post("/api/v1/users/register")
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(mapper.writeValueAsString(userRequest))
    ).andExpectAll(
            status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {

              });
      assertEquals("Username Already Registered", response.getErrors());
    });
  }

  @Test
  void getUserUnauthorized() throws Exception{
    mvc.perform(
            get("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "notfound")
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getUserUnauthorizedTokenNotSent() throws Exception{
    mvc.perform(
            get("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getUserSuccess() throws Exception{

    User user = new User();
    user.setUsername("saya");
    user.setPassword("saya");
    user.setName("Saya hahahah");
    user.setToken("token hahaha");
    user.setTokenExpiredAt(System.currentTimeMillis() + 100000000L);
    repository.save(user);

    mvc.perform(
            get("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "token hahaha")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<UserResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<UserResponse>>() {
              });

      assertNull(response.getErrors());
      assertEquals(user.getUsername(), response.getData().getUsername());
      assertEquals(user.getName(), response.getData().getName());

    });
  }

  @Test
  void getUserUnauthorizedTokenExpired() throws Exception{

    User user = new User();
    user.setUsername("saya");
    user.setPassword("saya");
    user.setName("Saya hahahah");
    user.setToken("token hahaha");
    user.setTokenExpiredAt(System.currentTimeMillis() - 100000000L);
    repository.save(user);

    mvc.perform(
            get("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "token hahaha")
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });

      assertNotNull(response.getErrors());

    });
  }

  @Test
  void updateUserUnauthorized() throws Exception {
    UpdateUserRequest request = new UpdateUserRequest();

    mvc.perform(
            patch("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isUnauthorized()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void updateUserSuccess() throws Exception {

    User user = new User();
    user.setUsername("saya");
    user.setPassword("saya");
    user.setName("Saya hahahah");
    user.setToken("token hahaha");
    user.setTokenExpiredAt(System.currentTimeMillis() + 100000000L);
    repository.save(user);

    UpdateUserRequest request = new UpdateUserRequest();
    request.setName("Jamal ludin");
    request.setPassword("wakwakwak");

    mvc.perform(
            patch("/api/v1/users/current")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", user.getToken())
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<UserResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<UserResponse>>() {
              });

      assertNull(response.getErrors());
      assertNotNull(response.getData());
      assertEquals(request.getName(), response.getData().getName());
      assertEquals(user.getUsername(), response.getData().getUsername());

      User userFromDb = repository.findById(response.getData().getUsername()).orElse(null);
      assertNotNull(userFromDb); // check kalau ada
      assertTrue(BCrypt.checkpw(request.getPassword(), userFromDb.getPassword())); // check klw password sudah di update


    });
  }
}