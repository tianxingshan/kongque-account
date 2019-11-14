package com.kongque.service.oauth;


import java.util.Map;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.kongque.component.oauth.OauthClientDetails;

/**
 * @author Shengzhao Li
 */

public interface OauthService {

    OauthClientDetails loadOauthClientDetails(String clientId);

    void archiveOauthClientDetails(String clientId);

    Map<String, ?> checkToken( String token, String clientId);
    
    /**
     * 登录获取token
     * @param parameters
     * @return
     */
    OAuth2AccessToken accessToken( Map<String, String> parameters);
    
    /**
     * 验证token拥有url权限
     * @param targetUri
     * @param token
     * @param method
     * @return
     */
    public boolean checkoutUrl(String targetUri, String token, String method);
}