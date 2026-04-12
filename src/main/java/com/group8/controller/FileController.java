
        package com.group8.controller;

import com.group8.dto.FileUploadResponse;
import com.group8.entity.Result;
import com.group8.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/file")
@Tag(name = "文件管理", description = "文件上传、删除等操作")
public class FileController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传图片文件到阿里云 OSS")
    public Result uploadFile(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "目录名，例如：avatar, product, category")
            @RequestParam(value = "dir", defaultValue = "upload") String dir
    ) {
        try {
            String fileUrl = fileUploadService.uploadFile(file, dir);

            FileUploadResponse response = FileUploadResponse.builder()
                    .url(fileUrl)
                    .filename(file.getOriginalFilename())
                    .size(file.getSize())
                    .type(file.getContentType())
                    .build();

            return Result.success(response);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文件", description = "从阿里云 OSS 删除文件")
    public Result deleteFile(
            @Parameter(description = "文件 URL") @RequestParam("url") String url
    ) {
        try {
            fileUploadService.deleteFile(url);
            return Result.success();
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return Result.error("文件删除失败：" + e.getMessage());
        }
    }

}
