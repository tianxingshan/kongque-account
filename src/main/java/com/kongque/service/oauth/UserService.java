package com.kongque.service.oauth;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.kongque.entity.Account;

/**
 * @author Shengzhao Li
 */
public interface UserService extends UserDetailsService {

    boolean isExistedUsername(String username);

    String saveUser(Account formDto);
}