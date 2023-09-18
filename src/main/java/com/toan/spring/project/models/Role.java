package com.toan.spring.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "roles")
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // @Enumerated(EnumType.STRING)
  @Column(name = "name")
  private String name;
  @Column(name = "code")
  private String code;
  @Column(name = "description")
  private String description;

  public Role(String name) {
    this.name = name;
    this.code = code;
    this.description = description;

  }
}