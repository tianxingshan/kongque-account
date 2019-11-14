/**
 * 
 */
package com.kongque.component.oauth.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.kongque.constants.Constants;

/**
 * @author yuehui
 *
 * @2017年12月19日
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private static String REALM = "YUE_OAUTH_REALM";

    @Autowired
    private TokenStore tokenStore;

    @Resource(name="customJdbcClientDetailsService") 
    private ClientDetailsService clientDetailsService;
    
    @Autowired
    private UserApprovalHandler userApprovalHandler;
    
    @Resource(name="authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    
    @Bean  
    public ApprovalStore approvalStore() {  
      TokenApprovalStore approvalStore = new TokenApprovalStore();  
      approvalStore.setTokenStore(tokenStore);  
      return approvalStore;  
    }  
    public  AuthorizationServerTokenServices tokenServices() {  
        MyTokenService tokenServices = new MyTokenService();  
        tokenServices.setTokenStore(tokenStore);  
        tokenServices.setSupportRefreshToken(true);  
        tokenServices.setClientDetailsService(clientDetailsService); 
        tokenServices.setRefreshTokenValiditySeconds(Constants.SYSCONSTANTS.TOKEN_TIMEOUT);
        return tokenServices;  
      }  
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenServices(tokenServices()).userApprovalHandler(userApprovalHandler).authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients();
        oauthServer.realm(REALM + "/client");
    }

}
