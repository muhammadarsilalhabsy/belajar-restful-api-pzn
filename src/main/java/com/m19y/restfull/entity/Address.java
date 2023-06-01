package com.m19y.restfull.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="addresses")
public class Address {

  @Id
  private String id;

  private String street;
  private String city;

  @Column(nullable = false)
  private String country;
  private String province;

  @Column(name = "postal_code")
  private String postalCode;

  @ManyToOne
  @JoinColumn(name = "contact_id", referencedColumnName = "id")
  private Contact contact;




}
