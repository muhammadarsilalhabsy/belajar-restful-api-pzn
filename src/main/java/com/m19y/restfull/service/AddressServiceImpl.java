package com.m19y.restfull.service;

import com.m19y.restfull.entity.Address;
import com.m19y.restfull.entity.Contact;
import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.request.CreateAddressRequest;
import com.m19y.restfull.model.request.UpdateAddressRequest;
import com.m19y.restfull.model.response.AddressResponse;
import com.m19y.restfull.repository.AddressRepository;
import com.m19y.restfull.repository.ContactRepository;
import com.m19y.restfull.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService{

  private final ContactRepository contactRepository;

  private final AddressRepository addressRepository;

  private final Utils utils;

  @Autowired
  public AddressServiceImpl(ContactRepository contactRepository, AddressRepository addressRepository, Utils utils) {
    this.contactRepository = contactRepository;
    this.addressRepository = addressRepository;
    this.utils = utils;
  }


  @Override
  @Transactional
  public AddressResponse create(User user, CreateAddressRequest request) {

    // validate
    utils.validate(request);

    // check contact if exists
    Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is Not Found!"));

    Address address = new Address();

    address.setId(UUID.randomUUID().toString());
    address.setContact(contact);
    address.setCity(request.getCity());
    address.setStreet(request.getStreet());
    address.setCountry(request.getCountry());
    address.setProvince(request.getProvince());
    address.setPostalCode(request.getPostalCode());

    // save to repository
    addressRepository.save(address);

    return utils.toAddressResponse(address);
  }

  @Override
  @Transactional(readOnly = true)
  public AddressResponse get(User user, String contactId, String addressId) {

    // check contact if exists
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is Not Found!"));

    Address address = addressRepository.findFirstByContactAndId(contact, addressId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is Not Found!"));

    return utils.toAddressResponse(address);
  }

  @Override
  @Transactional
  public AddressResponse update(User user, UpdateAddressRequest request) {

    // validate
    utils.validate(request);

    // check contact if exists
    Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is Not Found!"));

    Address address = addressRepository.findFirstByContactAndId(contact, request.getAddressId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is Not Found!"));

    // mapping
    address.setCity(request.getCity());
    address.setStreet(request.getStreet());
    address.setCountry(request.getCountry());
    address.setProvince(request.getProvince());
    address.setPostalCode(request.getPostalCode());

    return utils.toAddressResponse(address);
  }

  @Override
  @Transactional
  public void remove(User user, String contactId, String addressId) {

    // check contact if exists
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is Not Found!"));

    Address address = addressRepository.findFirstByContactAndId(contact, addressId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is Not Found!"));
    addressRepository.delete(address);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AddressResponse> listAddress(User user, String contactId) {

    // check contact if exists
    Contact contact = contactRepository.findFirstByUserAndId(user, contactId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is Not Found!"));

    List<Address> addresses = addressRepository.findAllByContact(contact);
    return addresses.stream()
            .map(address ->
              utils.toAddressResponse(address)
            ).collect(Collectors.toList());
  }
}
