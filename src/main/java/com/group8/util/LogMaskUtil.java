package com.group8.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogMaskUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 黑名单字段：企业里常见敏感项
    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
            "\"(password|pwd|token|accessToken|refreshToken|authorization|phone|mobile|email)\"\\s*:\\s*\"(.*?)\"",
            Pattern.CASE_INSENSITIVE
    );

    private static final int MAX_LOG_LENGTH = 2000;

    private LogMaskUtil() {}

    public static String maskArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            sb.append(maskOne(args[i]));
            if (i < args.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return truncate(sb.toString());
    }

    public static String maskObject(Object obj) {
        return truncate(maskOne(obj));
    }

    private static String maskOne(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof HttpServletRequest) return "\"HttpServletRequest\"";
        if (obj instanceof HttpServletResponse) return "\"HttpServletResponse\"";
        if (obj instanceof MultipartFile file) {
            return "{\"fileName\":\"" + file.getOriginalFilename() + "\",\"size\":" + file.getSize() + "}";
        }

        try {
            String json = OBJECT_MAPPER.writeValueAsString(obj);
            Matcher matcher = SENSITIVE_PATTERN.matcher(json);
            return matcher.replaceAll("\"$1\":\"***\"");
        } catch (JsonProcessingException e) {
            return "\"" + obj + "\"";
        }
    }

    private static String truncate(String text) {
        if (text == null || text.length() <= MAX_LOG_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_LOG_LENGTH) + "...(truncated)";
    }
}
