package com.example.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {
    @PostMapping
    public ResponseEntity<Void> createPost() {
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Void> getPosts() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Void> getPost(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok().build();
    }
}
