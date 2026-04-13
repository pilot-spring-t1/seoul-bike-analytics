package com.metanet.seoulbike.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomUser extends User {

    private Long memberId;

    public CustomUser(
            Long memberId,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {

        super(username, password, authorities); // 부모 생성자 호출
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}