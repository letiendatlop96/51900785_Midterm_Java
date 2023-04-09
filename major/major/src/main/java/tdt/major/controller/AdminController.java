package tdt.major.controller;

import tdt.major.dto.ProductDTO;
import tdt.major.dto.UserDTO;
import tdt.major.model.Category;
import tdt.major.model.Product;
import tdt.major.model.Role;
import tdt.major.model.User;
import tdt.major.service.CategoryService;
import tdt.major.service.ProductService;
import tdt.major.service.RoleService;
import tdt.major.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
	public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@GetMapping("/myadmin")
	public String adminHome() {
		return "adminHome";
	}// page admin home

	// Accounts
	@GetMapping("/myadmin/users")
	public String getAcc(Model model) {
		model.addAttribute("users", userService.getAllUser());
		// model.addAttribute("roles", roleService.getAllRole());
		return "users";
	}

	@GetMapping("/myadmin/users/add")
	public String getUserAdd(Model model) {
		model.addAttribute("userDTO", new UserDTO());
		model.addAttribute("roles", roleService.getAllRole());
		return "usersAdd";
	}

	@PostMapping("/myadmin/users/add")
	public String postUserAdd(@ModelAttribute("userDTO") UserDTO userDTO) {
		// convert dto > entity
		User user = new User();
		user.setId(userDTO.getId());
		user.setEmail(userDTO.getEmail());
		user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		List<Role> roles = new ArrayList<>();
		for (Integer item : userDTO.getRoleIds()) {
			roles.add(roleService.findRoleById(item).get());
		}
		user.setRoles(roles);

		userService.updateUser(user);
		return "redirect:/myadmin/users";
	}

	@GetMapping("/myadmin/users/delete/{id}")
	public String deleteUser(@PathVariable int id) {
		userService.removeUserById(id);
		return "redirect:/myadmin/users";
	}// delete 1 user

	@GetMapping("/myadmin/users/update/{id}")
	public String updateUser(@PathVariable int id, Model model) {
		Optional<User> opUser = userService.getUserById(id);
		if (opUser.isPresent()) {
			User user = opUser.get();
			// convert entity > dto
			UserDTO userDTO = new UserDTO();
			userDTO.setId(user.getId());
			userDTO.setEmail(user.getEmail());
			userDTO.setPassword("");
			userDTO.setFirstName(user.getFirstName());
			userDTO.setLastName(user.getLastName());
			List<Integer> roleIds = new ArrayList<>();
			for (Role item : user.getRoles()) {
				roleIds.add(item.getId());
			}

			model.addAttribute("userDTO", userDTO);
			model.addAttribute("roles", roleService.getAllRole());
			return "usersAdd";
		} else {
			return "404";
		}

	}

	// Categories session
	@GetMapping("/myadmin/categories")
	public String getCat(Model model) {
		model.addAttribute("categories", categoryService.getAllCategory());
		return "categories";
	}// view all categories

	@GetMapping("/myadmin/categories/add")
	public String getCatAdd(Model model) {
		model.addAttribute("category", new Category());
		return "categoriesAdd";
	}// form add new category

	@PostMapping("/myadmin/categories/add")
	public String postCatAdd(@ModelAttribute("category") Category category) {
		categoryService.updateCategory(category);
		return "redirect:/myadmin/categories";
	}// form add new category > do add

	@GetMapping("/myadmin/categories/delete/{id}")
	public String deleteCat(@PathVariable int id) {
		categoryService.removeCategoryById(id);
		return "redirect:/myadmin/categories";
	}// delete 1 category

	@GetMapping("/myadmin/categories/update/{id}")
	public String updateCat(@PathVariable int id, Model model) {
		Optional<Category> category = categoryService.getCategoryById(id);
		if (category.isPresent()) {
			model.addAttribute("category", category.get());
			return "categoriesAdd";
		} else {
			return "404";
		}
	}// form edit category, fill old data into form

	// Products session
	@GetMapping("/myadmin/products")
	public String getPro(Model model) {
		model.addAttribute("products", productService.getAllProduct());
		return "products";
	}// view all products

	@GetMapping("/myadmin/products/add")
	public String getProAdd(Model model) {
		model.addAttribute("productDTO", new ProductDTO());
		model.addAttribute("categories", categoryService.getAllCategory());
		return "productsAdd";
	}// form add new product

	@PostMapping("/myadmin/products/add")
	public String postProAdd(@ModelAttribute("productDTO") ProductDTO productDTO,
			@RequestParam("productImage") MultipartFile fileProductImage, @RequestParam("imgName") String imgName)
			throws IOException {
		// convert dto > entity
		Product product = new Product();
		product.setId(productDTO.getId());
		product.setName(productDTO.getName());
		product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
		product.setPrice(productDTO.getPrice());
		product.setWeight(productDTO.getWeight());
		product.setDescription(productDTO.getDescription());
		String imageUUID;
		if (!fileProductImage.isEmpty()) {
			imageUUID = fileProductImage.getOriginalFilename();
			Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
			Files.write(fileNameAndPath, fileProductImage.getBytes());
		} else {
			imageUUID = imgName;
		} // save image
		product.setImageName(imageUUID);

		productService.updateProduct(product);
		return "redirect:/myadmin/products";
	}// form add new product > do add

	@GetMapping("/myadmin/products/delete/{id}")
	public String deletePro(@PathVariable long id) {
		productService.removeProductById(id);
		return "redirect:/myadmin/products";
	}// delete 1 product

	@GetMapping("/myadmin/products/update/{id}")
	public String updatePro(@PathVariable long id, Model model) {
		Optional<Product> opProduct = productService.getProductById(id);
		if (opProduct.isPresent()) {
			Product product = opProduct.get();
			// convert entity > dto
			ProductDTO productDTO = new ProductDTO();
			productDTO.setId(product.getId());
			productDTO.setName(product.getName());
			productDTO.setCategoryId(product.getCategory().getId());
			productDTO.setPrice(product.getPrice());
			productDTO.setWeight(product.getWeight());
			productDTO.setDescription(product.getDescription());
			productDTO.setImageName(product.getImageName());

			model.addAttribute("productDTO", productDTO);
			model.addAttribute("categories", categoryService.getAllCategory());
			return "productsAdd";
		} else {
			return "404";
		}

	}// form edit product, fill old data into form
}
