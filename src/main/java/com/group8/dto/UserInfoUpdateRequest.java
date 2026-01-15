package com.group8.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoUpdateRequest {
    private String userName;
    private String phone;
    private String email;
    private String address;

    public String getuserName() {
        return userName;
    }
}
