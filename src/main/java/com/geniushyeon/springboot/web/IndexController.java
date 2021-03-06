package com.geniushyeon.springboot.web;

import javax.servlet.http.HttpSession;

import com.geniushyeon.springboot.config.auth.LoginUser;
import com.geniushyeon.springboot.config.auth.dto.SessionUser;
import com.geniushyeon.springboot.service.posts.PostsService;
import com.geniushyeon.springboot.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {
        model.addAttribute("posts", postsService.findAllDesc());

        if (user != null) {
            model.addAttribute("username", user.getName());
        }
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save"; // posts-save.mustache 호출
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {

        PostsResponseDto response = postsService.findById(id);
        model.addAttribute("posts", response);

        return "posts-update";
    }
}
