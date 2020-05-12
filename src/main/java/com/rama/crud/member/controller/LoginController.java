package com.rama.crud.member.controller;

import com.rama.crud.member.domain.User;
import com.rama.crud.member.service.CustomUserDetailsService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private CustomUserDetailsService userService;

    private ModelAndView getDefaultObjection(String viewName){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("fullName", "Howdy " + user.getFullname());
        modelAndView.addObject("role", auth.getAuthorities().toString().replaceAll("[^a-zA-Z0-9]", ""));
        modelAndView.setViewName(viewName);
        return modelAndView;
    }

    private Model getUserCurrentAuth(Model model){
        // To get current user data
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userAuth = userService.findUserByEmail(auth.getName());
        model.addAttribute("currentUser", userAuth);
        model.addAttribute("fullName", "Howdy " + userAuth.getFullname());
        model.addAttribute("role", auth.getAuthorities().toString().replaceAll("[^a-zA-Z0-9]", ""));
        return model;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = "/user-new", method = RequestMethod.GET)
    public ModelAndView signup(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        modelAndView.addObject("user", user);
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("fullName", "Howdy " + user.getFullname());
        modelAndView.addObject("role", auth.getAuthorities().toString().replaceAll("[^a-zA-Z0-9]", ""));
        modelAndView.setViewName("user-new");
        return modelAndView;
    }

    @RequestMapping(value = "/user-new", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "User is already registered");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("user-new");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User successfully registered");
            modelAndView.addObject("user", new User());
            getUserCurrentAuth(model);
            modelAndView.setViewName("dashboard");

        }
        return modelAndView;
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ModelAndView dashboard( @RequestParam(value = "email", required = false) String email ) {
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("fullName", "Howdy " + user.getFullname());
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        modelAndView.addObject("role", auth.getAuthorities().toString().replaceAll("[^a-zA-Z0-9]", ""));
        modelAndView.setViewName("dashboard");
        return modelAndView;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView singleUpdateUser( @RequestParam( value = "id", required = false) String email ){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("currentUser", user);
        modelAndView.addObject("role", auth.getAuthorities().toString().replaceAll("[^a-zA-Z0-9]", ""));
        modelAndView.setViewName("update-user");
        return modelAndView;
    }

    @ModelAttribute("users")
    public List<User> users() {
        return userService.findAll();
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") String id, Model model) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        getUserCurrentAuth(model);
        return "update-user";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") String id, @Valid User user,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            user.setId(id);
            return "update-user";
        }
        userService.saveUser(user);
        model.addAttribute("users", userService.findAll());
        getUserCurrentAuth(model);
        return "dashboard";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") String id, Model model) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userService.deleteUser(user.getId());
        model.addAttribute("users", userService.findAll());
        getUserCurrentAuth(model);
        return "dashboard";
    }

    @GetMapping("/profile/{id}")
    public String userProfile( @PathVariable("id") String id, Model model ){
        User user = userService.findUserById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        getUserCurrentAuth(model);
        return "profile";
    }

    @RequestMapping("/member")
    public ModelAndView member() {
        return getDefaultObjection("member");
    }



}
