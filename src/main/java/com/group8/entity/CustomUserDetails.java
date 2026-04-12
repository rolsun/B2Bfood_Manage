package com.group8.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Data
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Integer userType;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, 
                           Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public CustomUserDetails(String username, String password, 
                           Integer userType, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
