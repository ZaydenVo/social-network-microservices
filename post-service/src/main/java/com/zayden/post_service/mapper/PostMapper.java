package com.zayden.post_service.mapper;

import org.mapstruct.Mapper;

import com.zayden.post_service.dto.response.PostResponse;
import com.zayden.post_service.entity.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
