package com.group8.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    /**
     * 上传文件到阿里云 OSS
     * @param file 上传的文件
     * @param dir 目录名（例如：avatar, product, category）
     * @return 文件的访问 URL
     */
    String uploadFile(MultipartFile file, String dir);

    /**
     * 删除文件
     * @param fileUrl 文件 URL
     */
    void deleteFile(String fileUrl);
}
