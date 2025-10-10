
package com.example.orders.model;
import jakarta.persistence.*;
@Entity @Table(name="order_items")
public class OrderItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional=false) @JoinColumn(name="order_id") private Order order;
  @ManyToOne(optional=false) @JoinColumn(name="product_id") private Product product;
  private Integer quantity;
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Order getOrder(){return order;} public void setOrder(Order order){this.order=order;}
  public Product getProduct(){return product;} public void setProduct(Product product){this.product=product;}
  public Integer getQuantity(){return quantity;} public void setQuantity(Integer quantity){this.quantity=quantity;}
}
