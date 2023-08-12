package com.example.library.book;

import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.example.library.category.Category;
import com.example.library.user.User;

@Entity
@Table(
  name = "book",
  uniqueConstraints = @UniqueConstraint(columnNames = { "title", "owner_id" })
)
public class Book {
	
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
	
  @Column(nullable = false)
  private String title;
	
  @Column(nullable = false)
  private String description;
	
  @ManyToOne(optional = false)
  @JoinColumn(name = "category_id")
  private Category category;
	
  @ManyToOne(optional = false)
  @JoinColumn(name = "owner_id")
  private User owner;

  @CreationTimestamp
  private Instant createdAt;
	
  public Book() {}
	
  public Book(String title, String description, Category category) {
    this.title = title;
    this.description = description;
    this.category = category;
  }

  public Long getId() {
    return id;
  }
	
  public String getTitle() {
    return title;
  }
	
  public void setTitle(String title) {
    this.title = title;
  }
	
  public String getDescription() {
    return description;
  }
	
  public void setDescription(String description) {
    this.description = description;
  }
	
  public Category getCategory() {
    return category;
  }
	
  public void setCategory(Category category) {
    this.category = category;
  }
	
  public User getOwner() {
    return owner;
  }
	
  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

}
