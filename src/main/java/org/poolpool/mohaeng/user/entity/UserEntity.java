package org.poolpool.mohaeng.user.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // 테이블명이 다르면 여기만 바꾸면 됨
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(nullable = false, length = 100)
  private String userName;

  public Long getUserId() { return userId; }
  public String getUserName() { return userName; }
  public void setUserName(String userName) { this.userName = userName; }
}
