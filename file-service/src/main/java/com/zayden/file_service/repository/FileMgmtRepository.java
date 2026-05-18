package com.zayden.file_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.zayden.file_service.entity.FileMgmt;

@Repository
public interface FileMgmtRepository extends MongoRepository<FileMgmt, String> {}
