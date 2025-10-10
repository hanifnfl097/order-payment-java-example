
package com.example.orders.model;
import jakarta.persistence.*; import java.time.OffsetDateTime;
@Entity @Table(name="orders")
public class Order {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) @JoinColumn(name="user_id") private User user;
  private OffsetDateTime createdAt = OffsetDateTime.now();
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public User getUser(){return user;} public void setUser(User user){this.user=user;}
  public OffsetDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(OffsetDateTime c){this.createdAt=c;}
}
