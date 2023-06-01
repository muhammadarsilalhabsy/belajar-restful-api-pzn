package com.m19y.restfull.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="contacts")
public class Contact {

  @Id
  private String id;

  @Column(nullable = false, name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String phone;

  @ManyToOne
  @JoinColumn(name = "username", referencedColumnName = "username")
  private User user;

  @OneToMany(mappedBy = "contact")
  private List<Address> addresses;


}
