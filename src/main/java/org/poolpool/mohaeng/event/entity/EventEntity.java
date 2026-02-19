package org.poolpool.mohaeng.event.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "events") // 테이블명이 다르면 여기만 바꾸면 됨
public class EventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long eventId;

  @Column(nullable = false, length = 200)
  private String title;

  public Long getEventId() { return eventId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
}
