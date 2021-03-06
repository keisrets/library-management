package kn18012.librarymanagement.controller;

import kn18012.librarymanagement.domain.Role;
import kn18012.librarymanagement.domain.User;
import kn18012.librarymanagement.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/lib-admin")
public class AdminController {

    private UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String index() {
        return "redirect:/lib-admin/users/page/1/?phrase=";
    }

    @GetMapping("/users/page/{pageNumber}")
    public String pagedUserView(@PathVariable("pageNumber") int pageNumber, @RequestParam("phrase") String phrase, Model model) {
        // create a new page with user search results
        Page<User> page = userService.searchForUser(phrase, pageNumber);
        // get page content
        List<User> users = page.getContent();

        // add necessary attributes to template
        model.addAttribute("users", users);
        model.addAttribute("phrase", phrase);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());
        return "adm/index";
    }

    @GetMapping("/new-user")
    public String addUserView(Model model) {
        // add new user object and all possible roles to model
        model.addAttribute("newUser", new User());
        model.addAttribute("roles", userService.findAllRoles());
        return "adm/new-user";
    }

    @PostMapping("/create-user")
    public String createUser(@Valid @ModelAttribute User user, BindingResult bindingResult) {
        // check for data validation errors
        if(bindingResult.hasErrors()) {
            return "adm/new-user";
        } else {
            // check if email not taken
            if(userService.userExists(user.getEmail())) {
                bindingResult.rejectValue("email", "error.user", "E-mail already taken!");
                return "adm/new-user";
            }
        }
        // set user role
        List<Role> roles = user.getRoles();
        userService.saveUser(user, roles);
        return "redirect:/lib-admin/users/page/1/?phrase=&user_creation=success";
    }

    @GetMapping("/edit-user/{userId}")
    public String editUserView(@AuthenticationPrincipal User user, @PathVariable("userId") Long id, Model model) {
        // if user not found by id, return invalid request
        User userToEdit = userService.findById(id);
        if(userToEdit == null) {
            return "error-page";
        } else {
            // add current user and userToEdit to model
            model.addAttribute("user", user);
            model.addAttribute("userToEdit", userService.findById(id));
            return "adm/edit-user";
        }
    }

    @PostMapping("/update-user/{userId}")
    public String updateUser(@PathVariable("userId") Long id, @Valid @ModelAttribute User user, BindingResult bindingResult) {
        // check for data validation errors
        if(bindingResult.hasErrors())
            return "adm/edit-user";

        // check if user is in the data base
        User test = userService.findById(id);
        if(test == null) {
            return "error-page";
        } else {
            userService.update(id, user);
            return "redirect:/lib-admin/users/page/1/?phrase=&user_update=success";
        }
    }

    @DeleteMapping
    @RequestMapping("/delete-user/{userId}")
    public String deleteUser(@PathVariable("userId") Long id) {
        // check if user is in data base
        User test = userService.findById(id);
        if(test == null) {
            return "error-page";
        } else {
            userService.deleteById(id);
            return "redirect:/lib-admin/users/page/1/?phrase=&user_delete=success";
        }
    }
}