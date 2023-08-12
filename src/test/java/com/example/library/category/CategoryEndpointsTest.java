package com.example.library.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.security.test.context.support.TestExecutionEvent.TEST_EXECUTION;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import com.example.library.book.Book;
import com.example.library.book.BookRepository;
import com.example.library.user.User;
import com.example.library.user.UserRepository;

@WithUserDetails(value = "username", setupBefore = TEST_EXECUTION)
@SpringBootTest
@ActiveProfiles("test")
public class CategoryEndpointsTest extends EndpointsTestTemplate {
	
  @Autowired
  private UserRepository userRepository;
	
  @Autowired
  private CategoryRepository categoryRepository;
	
  @Autowired
  private BookRepository bookRepository;
	
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
  @DisplayName("Create category tests")
  class CreateCategoryEndpointTests {
		
    @Test
    @DisplayName("Retrieve create category page")
    void retrieveCreateCategoryPage() throws Exception {
      // when
      client.perform(get("/create-category"))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("category", hasProperty("name")),
        view().name("category/create-category")
      );
    }
    
    @Test
    @DisplayName("Create category")
    void createCategory() throws Exception {
      // when
      client.perform(post("/create-category")
        .param("name", "Fantasy")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isFound(),
        redirectedUrl("/list-categories")
      );
      assertThat(categoryRepository.findAll()).size().isEqualTo(1)
        .returnToIterable()
        .first()
        .extracting("name", "owner.username")
        .doesNotContainNull()
        .containsExactly("Fantasy", "username");
    }
    
    @Test
    @DisplayName("Don't create category with name taken")
    void doNotCreateCategoryWithNameTaken() throws Exception {
      // given
      persistCategory("Fantasy", currentUser);
      // when
      client.perform(post("/create-category")
        .param("name", "Fantasy")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrorCode("category", "name", "duplicate"),
        view().name("category/create-category")
      );
      // and
      assertThat(categoryRepository.count()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Don't create category with blank fields")
    void doNotCreateCategoryWithBlankFields() throws Exception {
      // when
      client.perform(post("/create-category")
        .param("name", " ")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrors("category", "name"),
        view().name("category/create-category")
      );
      // and
      assertThat(categoryRepository.count()).isZero();
    }
		
  }
	
  @Nested
  @DisplayName("List categories tests")
  class ListCategoriesEndpointTests {
		
    @Test
    @DisplayName("Retrieve categories page with categories")
    void retrieveCategoriesPageWithCategories() throws Exception {
      // given
      persistCategory("Fantasy", currentUser);
      persistCategory("Romance", currentUser);
      persistCategory("Science Fiction", currentUser);
      // when
      client.perform(get("/list-categories"))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("categories", hasSize(3)),
        model().attribute("categories", contains(
          hasProperty("name", is("Fantasy")),
          hasProperty("name", is("Romance")),
          hasProperty("name", is("Science Fiction"))
        )),
        view().name("category/list-categories")
      );
    }

    @Test
    @DisplayName("Don't retrieve categories page with categories from another user")
    void doNotRetrieveCategoriesPageWithCategoriesFromAnotherUser() throws Exception {
      // given
      persistCategory("Fantasy", otherUser);
      persistCategory("Romance", otherUser);
      persistCategory("Science Fiction", otherUser);
      // when
      client.perform(get("/list-categories"))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("categories", hasSize(0)),
        view().name("category/list-categories")
      );
    }
		
  }
	
  @Nested
  @DisplayName("Edit category tests")
  class EditCategoryEndpointTests {

    @Test
    @DisplayName("Retrieve edit category page")
    void retrieveEditCategoryPage() throws Exception {
      // given
      var id = persistCategory("Fantasy", currentUser).getId();
      // when
      client.perform(get("/edit-category/{id}", id))
      // then
      .andExpectAll(
        status().isOk(),
        model().attribute("category", allOf(
          hasProperty("id", is(id)),
          hasProperty("name", is("Fantasy"))
        )),
        view().name("category/edit-category")
      );
    }
    
    @Test
    @DisplayName("Edit category")
    void editCategory() throws Exception {
      // given
      var id = persistCategory("Fantasy", currentUser).getId();
      // when
      client.perform(post("/edit-category/{id}", id)
        .param("name", "Romance")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isFound(),
        redirectedUrl("/list-categories")
      );
      assertThat(categoryRepository.findByIdAndOwner(id, currentUser)).get()
        .extracting("name", "owner.username")
        .doesNotContainNull()
        .containsExactly("Romance", "username");
    }
    
    @Test
    @DisplayName("Don't edit category that doesn't exist")
    void doNotEditCategoryThatDoesNotExist() throws Exception {
      // when
      client.perform(post("/edit-category/{id}", 1)
        .param("name", "Romance")
        .with(csrf())
      )
      // then
      .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Don't edit category from another user")
    void doNotEditCategoryFromAnotherUser() throws Exception {
      // given
      var id = persistCategory("Fantasy", otherUser).getId();
      // when
      client.perform(post("/edit-category/{id}", id)
        .param("name", "Romance")
        .with(csrf())
      )
      // then
      .andExpect(status().isNotFound());
      assertThat(categoryRepository.findByIdAndOwner(id, otherUser)).get()
        .extracting("name", "owner.username")
        .doesNotContainNull()
        .containsExactly("Fantasy", "admin");
    }

    @Test
    @DisplayName("Don't edit category with name taken")
    void doNotEditCategoryWithNameTaken() throws Exception {
      // given
      persistCategory("Romance", currentUser);
      var id = persistCategory("Fantasy", currentUser).getId();
      // when
      client.perform(post("/edit-category/{id}", id)
        .param("name", "Romance")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrorCode("category", "name", "duplicate"),
        view().name("category/edit-category")
      );
      assertThat(categoryRepository.findByIdAndOwner(id, currentUser)).get()
        .extracting("name", "owner.username")
        .doesNotContainNull()
        .containsExactly("Fantasy", "username");
    }
    
    @Test
    @DisplayName("Don't edit category with blank fields")
    void doNotEditCategoryWithBlankFields() throws Exception {
      // given
      var id = persistCategory("Fantasy", currentUser).getId();
      // when
      client.perform(post("/edit-category/{id}", id)
        .param("name", " ")
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isOk(),
        model().attributeHasFieldErrors("category", "name"),
        view().name("category/edit-category")
      );
      assertThat(categoryRepository.findByIdAndOwner(id, currentUser)).get()
        .extracting("name", "owner.username")
        .doesNotContainNull()
        .containsExactly("Fantasy", "username");
    }
		
  }
	
  @Nested
  @DisplayName("Delete category tests")
  class DeleteCategoryEndpointTests {
		
    @Test
    @DisplayName("Delete category")
    void deleteCategory() throws Exception {
      // given
      var id = persistCategory("Fantasy", currentUser).getId();
      // when
      client.perform(post("/delete-category/{id}", id)
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isFound(),
        redirectedUrl("/list-categories")
      );
      assertThat(categoryRepository.count()).isZero();
    }
    
    @Test
    @DisplayName("Don't delete category that doesn't exist")
    void doNotDeleteCategoryThatDoesNotExist() throws Exception {
      // when
      client.perform(post("/delete-category/{id}", 1)
        .with(csrf())
      )
      // then
      .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Don't delete category from another user")
    void doNotDeleteCategoryFromAnotherUser() throws Exception {
      // given
      var id = persistCategory("Fantasy", otherUser).getId();
      // when
      client.perform(post("/delete-category/{id}", id)
        .with(csrf())
      )
      // then
      .andExpect(status().isNotFound());
      assertThat(categoryRepository.existsByIdAndOwner(id, otherUser)).isTrue();
    }

    @Test
    @DisplayName("Don't delete category in use by some book")
    void doNotDeleteCategoryInUseBySomeBook() throws Exception {
      // given
      var category = persistCategory("Fantasy", currentUser);
      var book = new Book("The Hobbit", "Adventures of Bilbo", category);
      book.setOwner(currentUser);
      bookRepository.save(book);
      // when
      client.perform(post("/delete-category/{id}", category.getId())
        .with(csrf())
      )
      // then
      .andExpectAll(
        status().isFound(),
        flash().attributeExists("deleteError"),
        redirectedUrl("/list-categories")
      );
      assertThat(categoryRepository.count()).isEqualTo(1);
    }
		
  }
	
  private Category persistCategory(String name, User owner) {
    var category = new Category(name);
    category.setOwner(owner);
    return categoryRepository.save(category);
  }

}
