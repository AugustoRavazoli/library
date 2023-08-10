CREATE TABLE IF NOT EXISTS app_user (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password CHAR(60) NOT NULL,
  role ENUM ('USER', 'ADMIN'),
  enabled BOOLEAN NOT NULL,
  
  PRIMARY KEY(id)
) engine = InnoDB;

CREATE TABLE IF NOT EXISTS category (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  owner_id INT UNSIGNED NOT NULL,
  
  UNIQUE (name, owner_id),
  FOREIGN KEY (owner_id) REFERENCES app_user (id)
) engine = InnoDB;

CREATE TABLE IF NOT EXISTS book (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  category_id INT UNSIGNED NOT NULL,
  owner_id INT UNSIGNED NOT NULL,
  created_at TIMESTAMP NOT NULL,
  
  UNIQUE (title, owner_id),
  FOREIGN KEY (category_id) REFERENCES category (id)
) engine = InnoDB;
