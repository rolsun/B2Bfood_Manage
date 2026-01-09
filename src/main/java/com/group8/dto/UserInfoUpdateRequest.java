package com.group8.dto;

import lombok.Data;

@Data
public class UserInfoUpdateRequest {
    private String userName;
    private String phone;
    private String email;
    private String address;
}
