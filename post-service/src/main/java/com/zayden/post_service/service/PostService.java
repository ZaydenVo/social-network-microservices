package com.zayden.post_service.service;

import com.zayden.post_service.dto.request.PostRequest;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
            log.error("Error while getting user profile", e);
        }

        var username = userProfile != null ? userProfile.getUsername() : null;

        PostResponse postResponse = postMapper.toPostResponse(post);
        postResponse.setUsername(username);
        postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));

        return postResponse;
    }
}
