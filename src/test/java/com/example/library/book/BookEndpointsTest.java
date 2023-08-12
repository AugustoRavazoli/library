package com.example.library.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasProperty;

import com.example.library.EndpointsTestTemplate;
import com.example.library.category.Category;
import com.example.library.category.CategoryRepository;
import com.example.library.user.User;
import com.example.library.user.UserRepository;

@WithUserDetails(value = "username", setupBefore = TEST_EXECUTION)
@SpringBootTest
@ActiveProfiles("test")
public class BookEndpointsTest extends EndpointsTestTemplate {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookRepository bookRepository;
	
  @Autowired
  private CategoryRepository categoryRepository;
	
  private User currentUser;

  private User otherUser;
	
  @BeforeEach
  void setUp() {
    bookRepository.deleteAll();
    categoryRepository.deleteAll();
    userRepository.deleteAll();
    currentUser = userRepository.save(
      new User("username", "$2a$10$TKJ6hXTseIzvSC.Zt1MluOtBjyLp7kM9/f1l/kRNBWq2LBxt.PHcK")
    );
    otherUser = userRepository.save(
      new User("admin", "$2a$10$7D5N.bUM/Cp1OTO0nASDx.qm0v04Tq8H4/zzKkdzJ5U4BD0WAoH26")
    );
  }

  @Nested
  @DisplayName("Create book tests")
  class CreateBookEndpointTests {
		
    @Test
    @DisplayName("Retrieve create book page")
    void retrieveCreateBookPage() throws Exception {
      // when
      client.perform(get("/create-book"))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("book", allOf(
          hasProperty("title"),
          hasProperty("description"),
          hasProperty("category")
        )),
        view().name("book/create-book")
      );
    }
    
    @Test
    @DisplayName("Create book")
    void createBook() throws Exception {
      // given
      persistCategory("Fantasy", currentUser);
      // when
      client.perform(post("/create-book")
        .param("title", "The Hobbit")
        .param("description", "Adventures of Bilbo")
        .param("category", "Fantasy")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isFound(),
        redirectedUrl("/list-books")
      );
      assertThat(bookRepository.findAll()).size().isEqualTo(1)
        .returnToIterable()
        .first()
        .extracting("title", "description", "category.name", "owner.username")
        .doesNotContainNull()
        .containsExactly("The Hobbit", "Adventures of Bilbo", "Fantasy", "username");
    }
    
    @Test
    @DisplayName("Don't create book with title taken")
    void doNotCreateBookWithTitleTaken() throws Exception {
      // given
      persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", currentUser);
      // when
      client.perform(post("/create-book")
        .param("title", "The Hobbit")
        .param("description", "Adventures of Bilbo")
        .param("category", "Fantasy")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrorCode("book", "title", "duplicate"),
        view().name("book/create-book")
      );
      assertThat(bookRepository.count()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Don't create book with blank fields")
    void doNotCreateBookWithBlankFields() throws Exception {
      // when
      client.perform(post("/create-book")
        .param("title", " ")
        .param("description", " ")
        .param("category", " ")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrors("book", "title", "description", "category"),
        view().name("book/create-book")
      );
      assertThat(bookRepository.count()).isZero();
    }
  
  }
	
  @Nested
  @DisplayName("List books tests")
  class ListBooksEndpointTests {
		
    @Test
    @DisplayName("Retrieve books page with books")
    void retrieveBookPageWithBooks() throws Exception {
      // given
      persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", currentUser);
      persistBook("The Lord of the Rings", "Adventures of Frodo", "Romance", currentUser);
      persistBook("War of the Worlds", "Alien invasion", "Science Fiction", currentUser);
      // when
      client.perform(get("/list-books"))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("currentPage", is(0)),
        model().attribute("totalPages", is(1)),
        model().attribute("books", hasSize(3)),
        model().attribute("books", contains(
          hasProperty("title", is("War of the Worlds")),
          hasProperty("title", is("The Lord of the Rings")),
          hasProperty("title", is("The Hobbit"))
        )),
        view().name("book/list-books")
      );
    }

    @Test
    @DisplayName("Retrieve books page with books containing title")
    void retrieveBookPageWithBooksContainingTitle() throws Exception {
      // given
      persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", currentUser);
      persistBook("The Lord of the Rings", "Adventures of Frodo", "Romance", currentUser);
      persistBook("War of the Worlds", "Alien invasion", "Science Fiction", currentUser);
      // when
      client.perform(get("/list-books?title=of the"))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("currentPage", is(0)),
        model().attribute("totalPages", is(1)),
        model().attribute("books", hasSize(2)),
        model().attribute("books", contains(
          hasProperty("title", is("War of the Worlds")),
          hasProperty("title", is("The Lord of the Rings"))
        )),
        view().name("book/list-books")
      );
    }

    @Test
    @DisplayName("Don't retrieve books page with books from another user")
    void doNotRetrieveBooksPageWithBooksFromAnotherUser() throws Exception {
      // given
      persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", otherUser);
      persistBook("The Lord of the Rings", "Adventures of Frodo", "Romance", otherUser);
      persistBook("War of the Worlds", "Alien invasion", "Science Fiction", otherUser);
      // when
      client.perform(get("/list-books"))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("currentPage", is(0)),
        model().attribute("totalPages", is(0)),
        model().attribute("books", hasSize(0)),
        view().name("book/list-books")
      );
    }
    
  }

  @Nested
  @DisplayName("Edit book tests")
  class EditBookEndpointTests {
		
    @Test
    @DisplayName("Retrieve edit book page")
    void retrieveEditBookPage() throws Exception {
      // given
      var id = persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", currentUser).getId();
      // when
      client.perform(get("/edit-book/{id}", id))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("book", allOf(
          hasProperty("id", is(id)),
          hasProperty("title", is("The Hobbit")),
          hasProperty("description", is("Adventures of Bilbo")),
          hasProperty("category", is("Fantasy"))
        )),
        view().name("book/edit-book")
      );
    }
   	
    @Test
    @DisplayName("Edit book")
    void editBook() throws Exception {
      // given
      persistCategory("Science Fiction", currentUser);
      var id = persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", currentUser).getId();
      // when
      client.perform(post("/edit-book/{id}", id)
        .param("title", "War of the Worlds")
        .param("description", "Alien invasion")
        .param("category", "Science Fiction")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isFound(),
        redirectedUrl("/list-books")
      );
      assertThat(bookRepository.findByIdAndOwner(id, currentUser)).get()
        .extracting("title", "description", "category.name", "owner.username")
        .doesNotContainNull()
        .containsExactly("War of the Worlds", "Alien invasion", "Science Fiction", "username");
    }
    
    @Test
    @DisplayName("Don't edit book that doesn't exist")
    void doNotEditBookThatDoesNotExist() throws Exception {
      // when
      client.perform(post("/edit-book/{id}", 1)
        .param("title", "War of the Worlds")
        .param("description", "Alien invasion")
        .param("category", "Science Fiction")
        .with(csrf())
      )
      // then
      .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Don't edit book from another user")
    void doNotEditBookFromAnotherUser() throws Exception {
      // given
      persistCategory("Science Fiction", currentUser);
      var id = persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", otherUser).getId();
      // when
      client.perform(post("/edit-book/{id}", id)
        .param("title", "War of the Worlds")
        .param("description", "Alien invasion")
        .param("category", "Fantasy")
        .with(csrf())
      )
      // then
      .andExpect(status().isNotFound());
      assertThat(bookRepository.findByIdAndOwner(id, otherUser)).get()
        .extracting("title", "description", "category.name", "owner.username")
        .doesNotContainNull()
        .containsExactly("The Hobbit", "Adventures of Bilbo", "Fantasy", "admin");
    }
   	
    @Test
    @DisplayName("Don't edit book with title taken")
    void doNotEditBookWithTitleTaken() throws Exception {
      // given
      persistBook("War of the Worlds", "Alien invasion", "Science Fiction", currentUser);
      var id = persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", currentUser).getId();
      // when
      client.perform(post("/edit-book/{id}", id)
        .param("title", "War of the Worlds")
        .param("description", "Alien invasion")
        .param("category", "Science Fiction")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrorCode("book", "title", "duplicate"),
        view().name("book/edit-book")
      );
      assertThat(bookRepository.findByIdAndOwner(id, currentUser)).get()
        .extracting("title", "description", "category.name", "owner.username")
        .doesNotContainNull()
        .containsExactly("The Hobbit", "Adventures of Bilbo", "Fantasy", "username");
    }

    @Test
    @DisplayName("Don't edit book with blank fields")
    void doNotEditBookWithBlankFields() throws Exception {
      // given
      var id = persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", currentUser).getId();
      // when
      client.perform(post("/edit-book/{id}", id)
        .param("title", " ")
        .param("description", " ")
        .param("category", " ")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrors("book", "title", "description", "category"),
        view().name("book/edit-book")
      );
      assertThat(bookRepository.findByIdAndOwner(id, currentUser)).get()
        .extracting("title", "description", "category.name", "owner.username")
        .doesNotContainNull()
        .containsExactly("The Hobbit", "Adventures of Bilbo", "Fantasy", "username");
    }

  }
	
  @Nested
  @DisplayName("Delete book tests")
  class DeleteBookEndpointTests {
		
    @Test
    @DisplayName("Delete book")
    void deleteBook() throws Exception {
      // given
      var id = persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", currentUser).getId();
      // when
      client.perform(post("/delete-book/{id}", id)
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isFound(),
        redirectedUrl("/list-books")
      );
      assertThat(bookRepository.count()).isZero();
      assertThat(categoryRepository.existsByNameAndOwner("Fantasy", currentUser)).isTrue();
    }
    
    @Test
    @DisplayName("Don't delete book that doesn't exist")
    void doNotDeleteBookThatDoesNotExist() throws Exception {
      // when
      client.perform(post("/delete-book/{id}", 1)
        .with(csrf())
      )
      // then
      .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Don't delete book from another user")
    void doNotDeleteBookFromAnotherUser() throws Exception {
      // given
      var id = persistBook("The Hobbit", "Adventures of Bilbo", "Fantasy", otherUser).getId();
      // when
      client.perform(post("/delete-book/{id}", id)
        .with(csrf())
      )
      // then
      .andExpect(status().isNotFound());
      assertThat(bookRepository.existsByIdAndOwner(id, otherUser)).isTrue();
    }

  }

  private Book persistBook(String title, String description, String categoryName, User owner) {
    var category = persistCategory(categoryName, owner);
    var book = new Book(title, description, category);
    book.setOwner(owner);
    return bookRepository.save(book);
  }
	
  private Category persistCategory(String name, User owner) {
    var category = new Category(name);
    category.setOwner(owner);
    return categoryRepository.save(category);
  }

}
