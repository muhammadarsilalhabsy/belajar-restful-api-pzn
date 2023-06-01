package com.m19y.restfull.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m19y.restfull.entity.Contact;
import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.response.ContactResponse;
import com.m19y.restfull.model.request.CreateContactRequest;
import com.m19y.restfull.model.request.UpdateContactRequest;
import com.m19y.restfull.model.response.WebResponse;
import com.m19y.restfull.repository.ContactRepository;
import com.m19y.restfull.repository.UserRepository;
import com.m19y.restfull.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

  private final MockMvc mockMvc;

  private final UserRepository userRepository;

  private final ContactRepository contactRepository;

  private final ObjectMapper mapper;

  @Autowired
  public ContactControllerTest(MockMvc mockMvc, UserRepository userRepository, ContactRepository contactRepository, ObjectMapper mapper) {
    this.mockMvc = mockMvc;
    this.userRepository = userRepository;
    this.contactRepository = contactRepository;
    this.mapper = mapper;
  }
  

  @BeforeEach
  void setUp() {

    contactRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setName("saya");
    user.setUsername("saya");
    user.setPassword(BCrypt.hashpw("saya", BCrypt.gensalt()));
    user.setToken("test token");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);

    userRepository.save(user);
  }

  @Test
  void createContactBadRequest() throws Exception{
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("");
    request.setEmail("salahFormat");

    mockMvc.perform(
            post("/api/v1/contacts")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });
      assertNotNull(response.getErrors());

    });
  }

  @Test
  void createContactSuccess() throws Exception{
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("ajib");
    request.setEmail("ajib@gmai.com");
    request.setLastName("darmawan");
    request.setPhone("243234256346");


    mockMvc.perform(
            post("/api/v1/contacts")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = mapper.readValue(
              result.getResponse().getContentAsString()
              , new TypeReference<WebResponse<ContactResponse>>() {
      });
      assertNull(response.getErrors());
      assertEquals("ajib", response.getData().getFirstName());
      assertEquals("darmawan", response.getData().getLastName());
      assertEquals("ajib@gmai.com", response.getData().getEmail());
      assertEquals("243234256346", response.getData().getPhone());
      assertTrue(contactRepository.existsById(response.getData().getId()));
    });
  }

  @Test
  void getContactNotFound() throws Exception{

    mockMvc.perform(
            get("/api/v1/contacts/19991213345678")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void getContactSuccess() throws Exception{
    User user = userRepository.findById("saya").orElseThrow();

    Contact contact = new Contact();
    contact.setFirstName("Jamal");
    contact.setEmail("jamal@gmail.com");
    contact.setLastName("ludin");
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);

    contactRepository.save(contact);

    mockMvc.perform(
            get("/api/v1/contacts/" + contact.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<ContactResponse>>() {
      });
      assertNull(response.getErrors());
      assertEquals("Jamal", response.getData().getFirstName());
      assertEquals("ludin", response.getData().getLastName());
      assertEquals("jamal@gmail.com", response.getData().getEmail());

      assertTrue(contactRepository.existsById(contact.getId()));
    });
  }

  @Test
  void updateContactBadRequest() throws Exception{
    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("");
    request.setLastName("ada");

    mockMvc.perform(
            put("/api/v1/contacts/331231")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "test token")
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
  void updateContactSuccess() throws Exception{
    User user = userRepository.findById("saya").orElseThrow();

    Contact contact = new Contact();
    contact.setFirstName("Jamal");
    contact.setEmail("jamal@gmail.com");
    contact.setLastName("ludin");
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);

    contactRepository.save(contact);

    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("ajib");
    request.setEmail("ajib@gmai.com");
    request.setLastName("darmawan");
    request.setPhone("243234256346");


    mockMvc.perform(
            put("/api/v1/contacts/" + contact.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<ContactResponse> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<ContactResponse>>() {
              });

      assertNull(response.getErrors());
      assertEquals("ajib", response.getData().getFirstName());
      assertEquals("darmawan", response.getData().getLastName());
      assertEquals("ajib@gmai.com", response.getData().getEmail());
      assertEquals("243234256346", response.getData().getPhone());
      assertTrue(contactRepository.existsById(response.getData().getId()));
    });
  }

  @Test
  void deleteContactNotFound() throws Exception{

    mockMvc.perform(
            delete("/api/v1/contacts/19991213345678")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
      });
      assertNotNull(response.getErrors());
    });
  }

  @Test
  void deleteContactSuccess() throws Exception{
    User user = userRepository.findById("saya").orElseThrow();

    Contact contact = new Contact();
    contact.setFirstName("Jamal");
    contact.setEmail("jamal@gmail.com");
    contact.setLastName("ludin");
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);

    contactRepository.save(contact);

    mockMvc.perform(
            delete("/api/v1/contacts/" + contact.getId())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<WebResponse<String>>() {
              });
      assertNull(response.getErrors());
      assertNotNull(response.getData());
      assertEquals("OK", response.getData());
      assertFalse(contactRepository.existsById(contact.getId()));
    });
  }

  @Test
  void searchNotFound() throws Exception{


    mockMvc.perform(
            get("/api/v1/contacts")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNull(response.getErrors());
      assertEquals(0, response.getData().size());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(0, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());

    });
  }

  @Test
  void searchNameSuccess() throws Exception{

    User user = userRepository.findById("saya").orElseThrow();

    for (int i = 0; i < 100; i++) {

    Contact contact = new Contact();
    contact.setFirstName("Jamal " + i);
    contact.setEmail("jamal" + i +"@gmail.com");
    contact.setLastName("ludin");
    contact.setPhone("9912122" + i);
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);

    contactRepository.save(contact);
    }
    mockMvc.perform(
            get("/api/v1/contacts")
                    .queryParam("name", "Jamal")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());

    });

    mockMvc.perform(
            get("/api/v1/contacts")
                    .queryParam("name", "ludin")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());

    });

    mockMvc.perform(
            get("/api/v1/contacts")
                    .queryParam("email", "@gmail.com")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());

    });

    mockMvc.perform(
            get("/api/v1/contacts")
                    .queryParam("phone", "2122")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-API-TOKEN", "test token")
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<ContactResponse>> response = mapper.readValue(
              result.getResponse().getContentAsString(),
              new TypeReference<>() {
              });

      assertNull(response.getErrors());
      assertEquals(10, response.getData().size());
      assertEquals(0, response.getPaging().getCurrentPage());
      assertEquals(10, response.getPaging().getTotalPage());
      assertEquals(10, response.getPaging().getSize());

    });
  }


}