package com.zayden.post_service.service;

import java.time.Instant;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.zayden.post_service.dto.request.PostRequest;
import com.zayden.post_service.dto.request.SearchPostRequest;
import com.zayden.post_service.dto.response.PageResponse;
import com.zayden.post_service.dto.response.PostResponse;
import com.zayden.post_service.dto.response.UserProfileResponse;
import com.zayden.post_service.entity.Post;
import com.zayden.post_service.mapper.PostMapper;
import com.zayden.post_service.repository.PostRepository;
import com.zayden.post_service.repository.httpclient.ProfileClient;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {
    PostRepository postRepository;
    ProfileClient profileClient;
    DateTimeFormatter dateTimeFormatter;
    PostMapper postMapper;

    public PostResponse createPost(PostRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();

        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        postRepository.save(post);

        UserProfileResponse userProfile = null;
        try {
            userProfile = profileClient.getUserProfile(userId).getResult();
        } catch (FeignException e) {
            log.error("Error while getting user profile!", e);
        }

        var username = userProfile != null ? userProfile.getUsername() : null;

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setUsername(username);
        postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));

        return postResponse;
    }

    public PageResponse<PostResponse> getMyPosts(int page, int size) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfileResponse userProfile = null;

        try {
            userProfile = profileClient.getUserProfile(userId).getResult();
        } catch (FeignException e) {
            log.error("Error while getting user profile!", e);
        }

        var username = userProfile != null ? userProfile.getUsername() : null;

        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = postRepository.findAllByUserId(userId, pageable);

        var postList = pageData.stream()
                .map(post -> {
                    var postResponse = postMapper.toPostResponse(post);
                    postResponse.setUsername(username);
                    postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
                    return postResponse;
                })
                .toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pagesSize(pageData.getSize())
                .totalPage(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }

    public PageResponse<PostResponse> searchPost(SearchPostRequest request) {
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

        var postList = postRepository.searchText(request.getText(), pageable);

        var postResponseList = postList.map(this::toPostResponse).toList();

        return PageResponse.<PostResponse>builder()
                .currentPage(request.getPage())
                .pagesSize(request.getSize())
                .totalElements(postList.getTotalElements())
                .data(postResponseList)
                .build();
    }

    public String deletePost(String postId) {
        postRepository.deleteById(postId);
        return "Post has been deleted!";
    }

    private PostResponse toPostResponse(Post post) {
        var postResponse = postMapper.toPostResponse(post);

        try {
            postResponse.setUsername(
                    profileClient.getUserProfile(post.getUserId()).getResult().getUsername());
        } catch (FeignException e) {
            log.error("Fail to fetch username fo userId={}!", post.getUserId(), e);
            postResponse.setUsername("Unknow");
        }

        postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
        return postResponse;
    }
}
