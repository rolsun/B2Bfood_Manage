package com.group8.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.group8.config.OssProperties;
import com.group8.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private OssProperties ossProperties;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public String uploadFile(MultipartFile file, String dir) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String contentType = file.getContentType();

        validateFile(file, contentType, originalFilename);

        String fileName = generateFileName(fileExtension);
        String objectName = dir + "/" + fileName;

        OSS ossClient = null;
        try {
            ossClient = createOSSClient();
            InputStream inputStream = file.getInputStream();

            ossClient.putObject(ossProperties.getBucketName(), objectName, inputStream);

            String fileUrl = buildFileUrl(objectName);
            log.info("文件上传成功：{}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        } finally {
            if (ossClient != null) {
                try {
                    ossClient.shutdown();
                } catch (Exception e) {
                    log.error("关闭 OSS 客户端失败", e);
                }
            }
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        String objectName = extractObjectName(fileUrl);
        if (objectName == null) {
            return;
        }

        OSS ossClient = null;
        try {
            ossClient = createOSSClient();
            ossClient.deleteObject(ossProperties.getBucketName(), objectName);
            log.info("文件删除成功：{}", fileUrl);
        } catch (Exception e) {
            log.error("文件删除失败：{}", fileUrl, e);
        } finally {
            if (ossClient != null) {
                try {
                    ossClient.shutdown();
                } catch (Exception e) {
                    log.error("关闭 OSS 客户端失败", e);
                }
            }
        }
    }

    private OSS createOSSClient() {
        return new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );
    }

    private String buildFileUrl(String objectName) {
        return ossProperties.getUrlPrefix() + "/" + objectName;
    }

    private String extractObjectName(String fileUrl) {
        if (!fileUrl.startsWith(ossProperties.getUrlPrefix())) {
            return null;
        }
        return fileUrl.substring(ossProperties.getUrlPrefix().length() + 1);
    }

    private void validateFile(MultipartFile file, String contentType, String filename) {
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new RuntimeException("不支持的文件类型：" + contentType);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小超过限制：" + MAX_FILE_SIZE / 1024 / 1024 + "MB");
        }

        String extension = getFileExtension(filename);
        if (extension == null || extension.isEmpty()) {
            throw new RuntimeException("无效的文件名");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    private String generateFileName(String extension) {
        String timestamp = String.valueOf(new Date().getTime());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return timestamp + "_" + uuid + "." + extension;
    }

}
