package com.m19y.restfull.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m19y.restfull.entity.Address;
import com.m19y.restfull.entity.Contact;
import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.CreateAddressRequest;
import com.m19y.restfull.model.request.UpdateAddressRequest;
import com.m19y.restfull.model.response.AddressResponse;
import com.m19y.restfull.model.response.WebResponse;
import com.m19y.restfull.repository.AddressRepository;
import com.m19y.restfull.repository.ContactRepository;
import com.m19y.restfull.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

  private final UserRepository userRepository;
  private final AddressRepository addressRepository;
  private final ContactRepository contactRepository;
  private final MockMvc mockMvc;
  private final ObjectMapper mapper;

  @Autowired
  public AddressControllerTest(UserRepository userRepository, AddressRepository addressRepository, ContactRepository contactRepository, MockMvc mockMvc, ObjectMapper mapper) {
    this.userRepository = userRepository;
    this.addressRepository = addressRepository;
    this.contactRepository = contactRepository;
    this.mockMvc = mockMvc;
    this.mapper = mapper;
  }

  @BeforeEach
  void setUp() {

    // delete all (urutan eksekusi delete harus benar)
    addressRepository.deleteAll();
    contactRepository.deleteAll();
    userRepository.deleteAll();

    // create user
    User user = new User();
    user.setToken("ada");
    user.setUsername("ada");
    user.setPassword("ada");
    user.setName("ada");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
    userRepository.save(user);

    // create contact
    Contact contact = new Contact();
    contact.setFirstName("Jamal");
    contact.setLastName("ludin");
    contact.setEmail("jamal@gmail.com");
    contact.setPhone("80880");
    contact.setId("contactTestId");
    contact.setUser(user);
    contactRepository.save(contact);
  }

  @Test
  void updateAddressIsSuccess() throws Exception {
    Contact contact = contactRepository.findById("contactTestId").orElse(null);

    Address address = new Address();
    address.setId("addressTestId");
    address.setStreet("lapulu");
    address.setCity("Kendari");
    address.setProvince("Cinagea");
    address.setCountry("Indonesia");
    address.setContact(contact);
    address.setPostalCode("80811");

    addressRepository.save(address);

    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setCity("Raha");
    request.setPostalCode("808088");
    request.setProvince("Sulawesi");
    request.setCountry("Malaysia");
    request.setStreet("Wa abe");

    mockMvc.perform(
            put("/api/v1/contacts/contactTestId/addresses/addressTestId")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNull(response.getErrors());
      assertNotNull(response.getData());

      Address addressDb = addressRepository.findById(response.getData().getId()).orElse(null);

      assertNotNull(addressDb);
      assertEquals(request.getCity(), addressDb.getCity());
      assertEquals(request.getStreet(), addressDb.getStreet());
      assertEquals(request.getProvince(), addressDb.getProvince());
      assertEquals(request.getCountry(), addressDb.getCountry());
      assertEquals(request.getPostalCode(), addressDb.getPostalCode());

    });
  }
  @Test
  void updateAddressBadRequest() throws Exception {

    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setCountry("");

    mockMvc.perform(
            put("/api/v1/contacts/contactTestId/addresses/addressTestId")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void createAddressBadRequest() throws Exception {

    CreateAddressRequest request = new CreateAddressRequest();
    request.setCountry("");

    mockMvc.perform(
            post("/api/v1/contacts/contactTestId/addresses")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isBadRequest()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNotNull(response.getErrors());
    });
  }

  @Test
  void createAddressIsSuccess() throws Exception {

    CreateAddressRequest request = new CreateAddressRequest();
    request.setCity("Raha");
    request.setPostalCode("808088");
    request.setProvince("Sulawesi");
    request.setCountry("Indonesia");
    request.setStreet("Wa abe");

    mockMvc.perform(
            post("/api/v1/contacts/contactTestId/addresses")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNull(response.getErrors());
      assertNotNull(response.getData());

      Address address = addressRepository.findById(response.getData().getId()).orElse(null);

      assertNotNull(address);
      assertEquals(request.getCity(), address.getCity());
      assertEquals(request.getStreet(), address.getStreet());
      assertEquals(request.getProvince(), address.getProvince());
      assertEquals(request.getCountry(), address.getCountry());
      assertEquals(request.getPostalCode(), address.getPostalCode());

    });
  }

  @Test
  void getAddressNotFound() throws Exception {

    mockMvc.perform(
            get("/api/v1/contacts/contactTestId/addresses/addressTestId")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNotNull(response.getErrors());
      assertEquals("Address is Not Found!", response.getErrors());
    });
  }

  @Test
  void getListAddressNotFound() throws Exception {

    mockMvc.perform(
            get("/api/v1/contacts/wrongId/addresses")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNotNull(response.getErrors());
      assertEquals("Contact is Not Found!", response.getErrors());
    });
  }

  @Test
  void getListAddressSuccess() throws Exception {

    Contact contact = contactRepository.findById("contactTestId").orElse(null);

    for (int i = 0; i < 10; i++) {

    Address address = new Address();
    address.setId("addressTestId" + i);
    address.setStreet("lapulu" + i);
    address.setCity("Kendari" + i);
    address.setProvince("Cinagea" + i);
    address.setCountry("Indonesia" + i);
    address.setContact(contact);
    address.setPostalCode("80811" + i);

    addressRepository.save(address);
    }

    mockMvc.perform(
            get("/api/v1/contacts/contactTestId/addresses")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<List<AddressResponse>> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNull(response.getErrors());
      assertNotNull(response.getData());
      assertEquals(10, response.getData().size());

    });
  }

  @Test
  void getAddressSuccess() throws Exception {

    Contact contact = contactRepository.findById("contactTestId").orElse(null);

    Address address = new Address();
    address.setId("addressTestId");
    address.setStreet("lapulu");
    address.setCity("Kendari");
    address.setProvince("Cinagea");
    address.setCountry("Indonesia");
    address.setContact(contact);
    address.setPostalCode("80811");

    addressRepository.save(address);

    mockMvc.perform(
            get("/api/v1/contacts/contactTestId/addresses/addressTestId")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<AddressResponse> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNull(response.getErrors());
      assertNotNull(response.getData());

      assertEquals(address.getId(), response.getData().getId());
      assertEquals(address.getCity(), response.getData().getCity());
      assertEquals(address.getStreet(), response.getData().getStreet());
      assertEquals(address.getCountry(), response.getData().getCountry());
      assertEquals(address.getProvince(), response.getData().getProvince());
      assertEquals(address.getPostalCode(), response.getData().getPostalCode());
    });
  }

  @Test
  void deleteAddressNotFound() throws Exception {

    mockMvc.perform(
            delete("/api/v1/contacts/contactTestId/addresses/addressTestId")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isNotFound()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNotNull(response.getErrors());
      assertEquals("Address is Not Found!", response.getErrors());
    });
  }

  @Test
  void deleteAddressSuccess() throws Exception {
    Contact contact = contactRepository.findById("contactTestId").orElse(null);

    Address address = new Address();
    address.setId("addressTestId");
    address.setStreet("lapulu");
    address.setCity("Kendari");
    address.setProvince("Cinagea");
    address.setCountry("Indonesia");
    address.setContact(contact);
    address.setPostalCode("80811");

    addressRepository.save(address);


    mockMvc.perform(
            delete("/api/v1/contacts/contactTestId/addresses/addressTestId")
                    .header("X-API-TOKEN", "ada")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
    ).andExpectAll(
            status().isOk()
    ).andDo(result -> {
      WebResponse<String> response = mapper.readValue(result.getResponse().getContentAsString(),
              new TypeReference<>() {});

      assertNull(response.getErrors());
      assertEquals("OK", response.getData());
      assertFalse(addressRepository.existsById("addressTestId"));
    });
  }
}































