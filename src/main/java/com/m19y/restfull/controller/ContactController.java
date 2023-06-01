package com.m19y.restfull.controller;

import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.CreateContactRequest;
import com.m19y.restfull.model.request.SearchContactRequest;
import com.m19y.restfull.model.request.UpdateContactRequest;
import com.m19y.restfull.model.response.ContactResponse;
import com.m19y.restfull.model.response.PagingResponses;
import com.m19y.restfull.model.response.WebResponse;
import com.m19y.restfull.service.ContactServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

  private final ContactServiceImpl service;


  @Autowired
  public ContactController(ContactServiceImpl service) {
    this.service = service;
  }

  @PostMapping(
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<ContactResponse> create(User user,
                                             @RequestBody CreateContactRequest request){
    ContactResponse contactResponse = service.create(user, request);

    return WebResponse.<ContactResponse>builder()
            .data(contactResponse)
            .build();
  }

  @GetMapping(
          path = "/{contactsId}",
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<ContactResponse> get(User user,
                                          @PathVariable("contactsId") String contactsId){

    ContactResponse response = service.get(user, contactsId);

    return WebResponse.<ContactResponse>builder()
            .data(response)
            .build();
  }

  @PutMapping(
          path = "/{contactsId}",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<ContactResponse> update(User user,
                                             @RequestBody UpdateContactRequest request,
                                             @PathVariable("contactsId") String contactsId){
    request.setId(contactsId);
    ContactResponse response = service.update(user, request);
    return WebResponse.<ContactResponse>builder()
            .data(response)
            .build();
  }

  @DeleteMapping(path = "/{contactsId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<String> delete(User user, @PathVariable("contactsId") String contactsId){

    service.delete(user, contactsId);

    return WebResponse.<String>builder()
            .data("OK")
            .build();
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public WebResponse<List<ContactResponse>> search(User user,
                                                   @RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "email", required = false) String email,
                                                   @RequestParam(value = "phone", required = false) String phone,
                                                   @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", required = false, defaultValue = "10") Integer size){
    SearchContactRequest request = SearchContactRequest.builder()
            .page(page)
            .size(size)
            .name(name)
            .email(email)
            .phone(phone)
            .build();

    Page<ContactResponse> contactResponses = service.search(user, request);

    return WebResponse.<List<ContactResponse>>builder()
            .data(contactResponses.getContent())
            .paging(PagingResponses.builder()
                    .currentPage(contactResponses.getNumber())
                    .totalPage(contactResponses.getTotalPages())
                    .size(contactResponses.getSize())
                    .build())
            .build();
  }
}
