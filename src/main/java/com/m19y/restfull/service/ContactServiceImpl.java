package com.m19y.restfull.service;

import com.m19y.restfull.entity.Contact;
import com.m19y.restfull.entity.User;
import com.m19y.restfull.model.response.ContactResponse;
import com.m19y.restfull.model.request.CreateContactRequest;
import com.m19y.restfull.model.request.SearchContactRequest;
import com.m19y.restfull.model.request.UpdateContactRequest;
import com.m19y.restfull.repository.ContactRepository;
import com.m19y.restfull.util.Utils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService{

  private final ContactRepository repository;

  private final Utils utils;


  @Autowired
  public ContactServiceImpl(ContactRepository repository, Utils utils) {
    this.repository = repository;
    this.utils = utils;
  }

  @Override
  @Transactional
  public ContactResponse create(User user, CreateContactRequest request) {

    // validasi request
    utils.validate(request);

    // set contact
    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setFirstName(request.getFirstName());
    contact.setLastName(request.getLastName());
    contact.setEmail(request.getEmail());
    contact.setPhone(request.getPhone());
    contact.setUser(user);

    // save contact
    repository.save(contact);

    return utils.toContactResponse(contact);
  }

  @Override
  @Transactional(readOnly = true)
  public ContactResponse get(User user, String contactId) {

    // find contact and contactId
    Contact contact = repository.findFirstByUserAndId(user, contactId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // set to contact response
    return utils.toContactResponse(contact);
  }

  @Override
  @Transactional
  public ContactResponse update(User user, UpdateContactRequest request) {

    utils.validate(request);

    // find contact and id
    Contact contact = repository.findFirstByUserAndId(user, request.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    // update contact data
    contact.setFirstName(request.getFirstName());
    contact.setLastName(request.getLastName());
    contact.setEmail(request.getEmail());
    contact.setPhone(request.getPhone());


    return utils.toContactResponse(contact);
  }

  @Override
  @Transactional
  public void delete(User user, String contactId) {
    Contact contact = repository.findFirstByUserAndId(user, contactId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

    repository.delete(contact);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ContactResponse> search(User user, SearchContactRequest request) {
    Specification<Contact> specification = (root, query, builder) -> {

      List<Predicate> predicates = new ArrayList<>();

      predicates.add(builder.equal(root.get("user"), user));

      if(Objects.nonNull(request.getName())){
        predicates.add(builder.or(
                builder.like(root.get("firstName"), "%" + request.getName() + "%"),
                builder.like(root.get("lastName"), "%" + request.getName() + "%")
        ));
      }

      if(Objects.nonNull(request.getEmail())){
        predicates.add(builder.like(root.get("email"), "%" + request.getEmail() + "%"));
      }

      if(Objects.nonNull(request.getPhone())){
        predicates.add(builder.like(root.get("phone"), "%" + request.getPhone() + "%"));
      }

      return query.where(predicates.toArray(new Predicate[]{})).getRestriction();

    };

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
    Page<Contact> contacts = repository.findAll(specification, pageable);
    List<ContactResponse> contactResponses = contacts.getContent().stream()
            .map(contact -> utils.toContactResponse(contact))
            .collect(Collectors.toList());

    return new PageImpl<>(contactResponses, pageable, contacts.getTotalElements());

  }
}
