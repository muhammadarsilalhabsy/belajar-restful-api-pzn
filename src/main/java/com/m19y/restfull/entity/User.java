package com.m19y.restfull.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="users")
public class User {

  @Id
  private String username;

  private String password;

  private String name;

  @Column(unique = true)
  private String token;

  @Column(name = "token_expired_at")
  private Long tokenExpiredAt;

  @OneToMany(mappedBy = "user")
  private List<Contact> contacts;



}
