package com.m19y.restfull.service;

import com.m19y.restfull.entity.Contact;
import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.CreateAddressRequest;
import com.m19y.restfull.model.request.UpdateAddressRequest;
import com.m19y.restfull.model.response.AddressResponse;

import java.util.List;

public interface AddressService {
  AddressResponse create(User user, CreateAddressRequest request);

  AddressResponse update(User user, UpdateAddressRequest request);

  AddressResponse get(User user, String contactId, String addressId);

  void remove(User user, String contactId, String addressId);

  List<AddressResponse> listAddress(User user, String contactId);
}
