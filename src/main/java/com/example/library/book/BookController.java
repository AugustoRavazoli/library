package com.example.library.book;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;

import com.example.library.category.CategoryMapper;
import com.example.library.category.CategoryService;
import com.example.library.user.User;

@Controller
public class BookController {
	
	private final BookService bookService;
	private final CategoryService categoryService;
  private final BookMapper bookMapper;
  private final CategoryMapper categoryMapper;
	
	public BookController(
    BookService bookService,
    CategoryService categoryService,
    BookMapper bookMapper,
    CategoryMapper categoryMapper
  ) {
		this.bookService = bookService;
		this.categoryService = categoryService;
    this.bookMapper = bookMapper;
    this.categoryMapper = categoryMapper;
	}
		
	@ModelAttribute
	public void addCategoryModel(@AuthenticationPrincipal User user, Model model) {
    var categories = categoryService.listCategories(user)
      .stream()
      .map(categoryMapper::toResponse)
      .toList();
		model.addAttribute("categories", categories);
	}
		
	@GetMapping("/create-book")
	public String createBookPage(@AuthenticationPrincipal User user, Model model) {
		model.addAttribute("book", new BookRequest());
		return "book/create-book";
	}
	
	@PostMapping("/create-book")
	public String createBook(
		@AuthenticationPrincipal User user,
		@Valid @ModelAttribute("book") BookRequest book,
		BindingResult result,
		Model model
	) {
		if (result.hasErrors()) {
			return "book/create-book";
		}
		try {
			bookService.createBook(bookMapper.toEntity(book), user);
		} catch (TitleTakenException ex) {
			result.rejectValue("title", "duplicate", ex.getMessage());
			return "book/create-book";
		}
		return "redirect:/list-books";
	}
	
	@GetMapping("/list-books")
	public String booksPage(
		@AuthenticationPrincipal User user,
    @RequestParam(defaultValue = "") String title,
		@RequestParam(defaultValue = "0") int page,
		Model model
	) {
		var books = bookService.listBooks(title, user, page)
      .map(bookMapper::toResponse);
		addPaginationModel(page, model, books);
		return "book/list-books";
	}
		
	@GetMapping("/edit-book/{id}")
	public String editBookPage(@AuthenticationPrincipal User user, @PathVariable long id, Model model) {
		var book = bookMapper.toResponse(bookService.findBook(id, user));
    model.addAttribute("id", book.getId());
    model.addAttribute("book", book);
		return "book/edit-book";
	}

	@PostMapping("/edit-book/{id}")
	public String editBook(
		@AuthenticationPrincipal User user,
		@PathVariable long id,
		@Valid @ModelAttribute("book") BookRequest book,
		BindingResult result,
		Model model
	) {
		if (result.hasErrors()) {
      model.addAttribute("id", id);
			return "book/edit-book";
		}
		try {
			bookService.editBook(id, bookMapper.toEntity(book), user);
		} catch (TitleTakenException ex) {
			result.rejectValue("title", "duplicate", ex.getMessage());
      model.addAttribute("id", id);
			return "book/edit-book";
		}
		return "redirect:/list-books";
	}
	
	@PostMapping("/delete-book/{id}")
	public String deleteBook(@AuthenticationPrincipal User user, @PathVariable long id) {
		bookService.deleteBook(id, user);
		return "redirect:/list-books";
	}
	
	private String addPaginationModel(int page, Model model, Page<BookResponse> paginated) {
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("books", paginated.getContent());
		return "book/list-books";
	}

}
