package com.example.library.category;

import jakarta.persistence.Entity;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import com.example.library.user.User;

@Entity
@Table(
  name = "category",
  uniqueConstraints = @UniqueConstraint(columnNames = { "name", "owner_id" })
)
public class Category {
	
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;
	
  @ManyToOne(optional = false)
  @JoinColumn(name = "owner_id")
  private User owner;

  public Category() {}

  public Category(String name) {
    this.name = name;
  }
	
  public Long getId() {
    return id;
  }
		
  public String getName() {
    return name;
  }
	
  public void setName(String name) {
    this.name = name;
  }
	
  public User getOwner() {
    return owner;
  }
	
  public void setOwner(User owner) {
    this.owner = owner;
  }

}
