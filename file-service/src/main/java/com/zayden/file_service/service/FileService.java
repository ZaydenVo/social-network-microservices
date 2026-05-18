package com.zayden.file_service.service;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zayden.file_service.dto.response.FileData;
import com.zayden.file_service.dto.response.FileResponse;
import com.zayden.file_service.exception.AppException;
import com.zayden.file_service.exception.ErrorCode;
import com.zayden.file_service.mapper.FileMgmtMapper;
import com.zayden.file_service.repository.FileMgmtRepository;
import com.zayden.file_service.repository.FileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {
    FileMgmtRepository fileMgmtRepository;
    FileRepository fileRepository;
    FileMgmtMapper fileMgmtMapper;

    public FileResponse uploadFile(MultipartFile file) throws IOException {
        var fileInfo = fileRepository.store(file);

        var fileMgmt = fileMgmtMapper.toFileMgmt(fileInfo);
        fileMgmt.setOwnerId(
                SecurityContextHolder.getContext().getAuthentication().getName());
        fileMgmtRepository.save(fileMgmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

    public FileData downloadFile(String fileName) throws IOException {
        var fileMgmt =
                fileMgmtRepository.findById(fileName).orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_EXISTED));

        return new FileData(fileMgmt.getContentType(), fileRepository.read(fileMgmt));
    }
}
