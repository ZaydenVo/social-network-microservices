package com.zayden.post_service.controller;

import org.springframework.web.bind.annotation.*;

import com.zayden.post_service.dto.ApiResponse;
import com.zayden.post_service.dto.request.PostRequest;
import com.zayden.post_service.dto.request.SearchPostRequest;
import com.zayden.post_service.dto.response.PageResponse;
import com.zayden.post_service.dto.response.PostResponse;
import com.zayden.post_service.service.PostService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;

    @PostMapping("/create")
    ApiResponse<PostResponse> createPost(@RequestBody PostRequest request) {
        return ApiResponse.<PostResponse>builder()
                .result(postService.createPost(request))
                .build();
    }

    @GetMapping("/my-posts")
    ApiResponse<PageResponse<PostResponse>> getMyPosts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .result(postService.getMyPosts(page, size))
                .build();
    }

    @PostMapping("/search")
    ApiResponse<PageResponse<PostResponse>> searchPost(@RequestBody SearchPostRequest request) {
        return ApiResponse.<PageResponse<PostResponse>>builder()
                .result(postService.searchPost(request))
                .build();
    }

    @DeleteMapping("/{postId}")
    ApiResponse<String> deletePost(@PathVariable String postId) {
        return ApiResponse.<String>builder()
                .result(postService.deletePost(postId))
                .build();
    }
}
