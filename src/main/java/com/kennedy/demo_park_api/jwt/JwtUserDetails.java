package com.kennedy.demo_park_api.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JwtUserDetails extends User {

    private com.kennedy.demo_park_api.entities.User user;

    public JwtUserDetails(com.kennedy.demo_park_api.entities.User user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public Long getId(){
        return this.user.getId();
    }

    public String role(){
        return this.user.getRole().name();
    }
}
