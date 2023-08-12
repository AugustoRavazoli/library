package com.example.library.user;

import static java.util.Collections.singleton;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User implements UserDetails {
	
  private static final long serialVersionUID = -7159169811087327108L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;
	
  @Column(nullable = false, unique = true)
  private String username;
	
  @Column(nullable = false)
  private String password;
	
  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  private boolean enabled = true;
	
  public User() {}
	
  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }
  
  public Long getId() {
    return id;
  }
	
  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
	
  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
	
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return singleton(role);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

}
