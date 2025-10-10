
package com.example.payments.model;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="payments")
public class Payment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  private Long orderId;
  private Double amount;
  @Column(length=20) private String status;
  private OffsetDateTime createdAt = OffsetDateTime.now();
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Long getOrderId(){return orderId;} public void setOrderId(Long orderId){this.orderId=orderId;}
  public Double getAmount(){return amount;} public void setAmount(Double amount){this.amount=amount;}
  public String getStatus(){return status;} public void setStatus(String status){this.status=status;}
  public OffsetDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(OffsetDateTime c){this.createdAt=c;}
}
