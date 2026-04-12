package com.group8.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoUpdateRequest {
    private String username;
    private String phone;
    private String email;
    private String address;
    private String avatar;
}
