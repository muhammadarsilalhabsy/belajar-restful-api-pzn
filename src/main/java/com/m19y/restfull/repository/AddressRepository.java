package com.m19y.restfull.repository;

import java.util.List;
import com.m19y.restfull.entity.Address;
import com.m19y.restfull.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
  Optional<Address> findFirstByContactAndId(Contact contact, String id);

  List<Address> findAllByContact(Contact contact);
}
