package com.m19y.restfull.controller;

import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.CreateAddressRequest;
import com.m19y.restfull.model.request.UpdateAddressRequest;
import com.m19y.restfull.model.response.AddressResponse;
import com.m19y.restfull.model.response.WebResponse;
import com.m19y.restfull.service.AddressServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacts")
public class AddressController {
  private final AddressServiceImpl addressService;


  @Autowired
  public AddressController(AddressServiceImpl addressService) {
    this.addressService = addressService;
  }


  @PostMapping(
          path = "/{contactId}/addresses",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<AddressResponse> create(User user,
                                             @RequestBody CreateAddressRequest request,
                                             @PathVariable("contactId") String contactId){
    // set contact id
    request.setContactId(contactId);

    // create address
    AddressResponse response = addressService.create(user, request);

    return WebResponse.<AddressResponse>builder()
            .data(response)
            .build();
  }

  @GetMapping(
          path = "/{contactId}/addresses/{addressId}",
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<AddressResponse> get(User user,
                                          @PathVariable("contactId") String contactId,
                                          @PathVariable("addressId") String addressId){

    AddressResponse response = addressService.get(user, contactId, addressId);

    return WebResponse.<AddressResponse>builder()
            .data(response)
            .build();
  }

  @GetMapping(
          path = "/{contactId}/addresses",
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<List<AddressResponse>> getListAddress(User user,
                                          @PathVariable("contactId") String contactId) {

    List<AddressResponse> response = addressService.listAddress(user, contactId);

    return WebResponse.<List<AddressResponse>>builder()
            .data(response)
            .build();
  }

  @PutMapping(
          path = "/{contactId}/addresses/{addressId}",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<AddressResponse> create(User user,
                                             @RequestBody UpdateAddressRequest request,
                                             @PathVariable("contactId") String contactId,
                                             @PathVariable("addressId") String addressId){
    // set contact id
    request.setContactId(contactId);
    request.setAddressId(addressId);

    // create address
    AddressResponse response = addressService.update(user, request);

    return WebResponse.<AddressResponse>builder()
            .data(response)
            .build();
  }

  @DeleteMapping(
          path = "/{contactId}/addresses/{addressId}",
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  public WebResponse<String> remove(User user,
                                          @PathVariable("contactId") String contactId,
                                          @PathVariable("addressId") String addressId){

    addressService.remove(user, contactId, addressId);

    return WebResponse.<String>builder()
            .data("OK")
            .build();
  }
}
