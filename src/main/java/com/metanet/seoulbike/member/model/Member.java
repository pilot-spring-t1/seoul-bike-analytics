package com.metanet.seoulbike.member.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Member implements UserDetails {
    
    private Long memberId;
    private String loginId;
    private String password;
    private String name;
    private String gender;
    private Integer age;
    private String role;
    private LocalDate createdAt;
    private LocalDate lastLoginAt;

    // 시큐리티가 권한(Role)을 확인할 때 사용하는 메서드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public String getUsername() {
        return this.loginId;
    }

    @Override
    public String getPassword() {
        return this.password;
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