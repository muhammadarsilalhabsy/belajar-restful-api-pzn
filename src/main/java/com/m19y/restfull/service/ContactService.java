package com.m19y.restfull.service;

import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.response.ContactResponse;
import com.m19y.restfull.model.request.CreateContactRequest;
import com.m19y.restfull.model.request.SearchContactRequest;
import com.m19y.restfull.model.request.UpdateContactRequest;
import org.springframework.data.domain.Page;

public interface ContactService {
  ContactResponse create(User user, CreateContactRequest request);
  ContactResponse get(User user, String contactId);

  ContactResponse update(User user, UpdateContactRequest request);
  void delete(User user, String contactId);

  Page<ContactResponse> search(User user, SearchContactRequest request);
}
