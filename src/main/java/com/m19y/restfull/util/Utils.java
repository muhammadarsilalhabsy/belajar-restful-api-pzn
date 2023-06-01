package com.m19y.restfull.util;

import com.m19y.restfull.entity.Address;
import com.m19y.restfull.entity.Contact;
import com.m19y.restfull.model.response.AddressResponse;
import com.m19y.restfull.model.response.ContactResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class Utils {

  @Autowired
  private Validator validator;

  public void validate(Object request){
    Set<ConstraintViolation<Object>> constraintViolations = validator.validate(request);
    if (constraintViolations.size() != 0){
      // errors
      throw new ConstraintViolationException(constraintViolations);
    }
  }

  public Long next30Days(){
    return System.currentTimeMillis() + (1000 * 16 * 24 *30);
  }

  public ContactResponse toContactResponse(Contact contact){
    return ContactResponse.builder()
            .id(contact.getId())
            .firstName(contact.getFirstName())
            .lastName(contact.getLastName())
            .phone(contact.getPhone())
            .email(contact.getEmail())
            .build();
  }

  public AddressResponse toAddressResponse(Address address){
    return AddressResponse.builder()
            .id(address.getId())
            .city(address.getCity())
            .street(address.getStreet())
            .country(address.getCountry())
            .province(address.getProvince())
            .postalCode(address.getPostalCode())
            .build();
  }
}
