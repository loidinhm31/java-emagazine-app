package com.emagazine.web.controller.client;

import java.util.List;

import com.emagazine.web.model.ArticleDetails;
import com.emagazine.web.model.Comment;
import com.emagazine.web.model.Post;
import com.emagazine.web.model.request.CommentRequest;
import com.emagazine.web.service.ArticleService;
import com.emagazine.web.service.CommentService;
import com.emagazine.web.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/post")
public class PostContentController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public String showPost(@RequestParam("id") Long postId,
                           Model theModel) {

        Post post = null;

        post = postService.fetchPostsById(postId);

        ArticleDetails currArticle = null;

        currArticle = articleService.fetchArticle(post.getArticle().getId());


        List<Comment> comments = commentService.fetchCommentsForPost(postId);
        theModel.addAttribute("comments", comments);


        theModel.addAttribute("title", post.getTitle());
        theModel.addAttribute("post", post);
        theModel.addAttribute("currArticle", currArticle);

        return "client/content_page";
    }


    @PostMapping
    public String postComment(@RequestParam("id") Long postId,
                              @RequestParam("content") String content,
                              @RequestParam("createdByName") String createdByName,
                              @RequestParam("createdByEmail") String createdByEmail) {

        CommentRequest theComment = new CommentRequest(createdByName, createdByEmail, content, postId);


        commentService.saveComment(theComment);

        return "redirect:/post?id=" + postId;

    }
}
