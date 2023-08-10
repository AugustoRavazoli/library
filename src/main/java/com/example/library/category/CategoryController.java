package com.example.library.category;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import com.example.library.user.User;

@Controller
public class CategoryController {
	
	private final CategoryService categoryService;
  private final CategoryMapper categoryMapper;
	
	public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
		this.categoryService = categoryService;
    this.categoryMapper = categoryMapper;
	}
	
	@GetMapping("/create-category")
	public String createCategoryPage(Model model) {
		model.addAttribute("category", new CategoryRequest());
		return "category/create-category";
	}
	
	@PostMapping("/create-category")
	public String createCategory(
		@AuthenticationPrincipal User user,
		@Valid @ModelAttribute("category") CategoryRequest category,
		BindingResult result,
    Model model
	) {
		if (result.hasErrors()) {
			return "category/create-category";
		}
		try {
			categoryService.createCategory(categoryMapper.toEntity(category), user);
		} catch (CategoryAlreadyExistsException ex) {
			result.rejectValue("name", "duplicate", ex.getMessage());
			return "category/create-category";
		}
		return "redirect:/list-categories";
	}
	
	@GetMapping("/list-categories")
	public String categoriesPage(@AuthenticationPrincipal User user, Model model) {
		var categories = categoryService.listCategories(user)
      .stream()
      .map(categoryMapper::toResponse)
      .toList();
		model.addAttribute("categories", categories);
		return "category/list-categories";
	}

  @GetMapping("/edit-category/{id}")
  public String editCategoryPage(@AuthenticationPrincipal User user, @PathVariable long id, Model model) {
    var category = categoryMapper.toResponse(categoryService.findCategory(id, user));
    model.addAttribute("id", category.getId());
    model.addAttribute("category", category);
    return "category/edit-category";
  }

  @PostMapping("/edit-category/{id}")
  public String editCategory(
	  @AuthenticationPrincipal User user,
    @PathVariable long id,
		@Valid @ModelAttribute("category") CategoryRequest category,
		BindingResult result,
		Model model
  ) {
		if (result.hasErrors()) {
      model.addAttribute("id", id);
			return "category/edit-category";
		}
		try {
			categoryService.editCategory(id, categoryMapper.toEntity(category), user);
		} catch (CategoryAlreadyExistsException ex) {
			result.rejectValue("name", "duplicate", ex.getMessage());
      model.addAttribute("id", id);
			return "category/edit-category";
		}
		return "redirect:/list-categories";
  }

	@PostMapping("/delete-category/{id}")
	public String deleteCategory(@AuthenticationPrincipal User user, @PathVariable long id, RedirectAttributes redirect) {
    try {
		  categoryService.deleteCategory(id, user);
    } catch (CategoryInUseException ex) {
      redirect.addFlashAttribute("deleteError", ex.getMessage());
    }
		return "redirect:/list-categories";
  }

}
