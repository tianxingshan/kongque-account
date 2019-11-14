package com.kongque.service.oauth.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import com.kongque.component.IRedisClient;
import com.kongque.component.oauth.OauthClientDetails;
import com.kongque.constants.Constants;
import com.kongque.dao.impl.DaoServiceImpl;
import com.kongque.service.oauth.OauthService;

import net.sf.json.JSONArray;

/**
 * OAuth 业务处理服务对象, 事务拦截也加在这一层
 *
 * @author cary
 */
@Service("oauthService")
public class OauthServiceImpl implements OauthService{

	private static final Logger LOG = LoggerFactory.getLogger(OauthServiceImpl.class);

	@Autowired
	private DaoServiceImpl d;

	@Resource
	private IRedisClient redisService;

	@Autowired
	private ClientDetailsService clientDetailsService;
	@Autowired
	private AuthorizationServerTokenServices tokenServices;

	@Autowired
	private ResourceServerTokenServices resourceServerTokenServices;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private OAuth2RequestFactory oAuth2RequestFactory;

	private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();
	private WebResponseExceptionTranslator providerExceptionHandler = new DefaultWebResponseExceptionTranslator();

	private AccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();

	@Override
	public OauthClientDetails loadOauthClientDetails(String clientId) {
		return d.findUniqueByProperty(OauthClientDetails.class, "clientId", clientId);
	}

	@Override
	public void archiveOauthClientDetails(String clientId) {
		OauthClientDetails o = d.findUniqueByProperty(OauthClientDetails.class, "clientId", clientId);
		o.archived(true);
		d.update(o);
	}

	/**
	 * Verify access_token
	 * <p/>
	 * Ext. from CheckTokenEndpoint
	 *
	 * @param value
	 *            token
	 * @param clientId
	 *            client_id
	 * @return Map
	 * @see org.springframework.security.oauth2.provider.endpoint
	 *      CheckTokenEndpoint
	 * @since 1.0
	 */
	public Map<String, ?> checkToken(String value, String clientId) {

		if (org.apache.commons.lang3.StringUtils.isBlank(clientId))
			clientId = "kongque_cloud";
		OAuth2AccessToken token = resourceServerTokenServices.readAccessToken(value);
		if (token == null) {
			return null;
		}

		if (token.isExpired()) {
			LOG.error("Token has expired");
			return null;
		}

		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
		if (clientDetails == null) {
			LOG.error("client_id was not recognised");
			return null;
		}

		OAuth2Authentication authentication = resourceServerTokenServices.loadAuthentication(token.getValue());
		final String authClientId = authentication.getOAuth2Request().getClientId();
		if (!clientId.equals(authClientId)) {
			LOG.error("Given client ID does not match authenticated client");
			return null;
		}
		return accessTokenConverter.convertAccessToken(token, authentication);
	}

	/**
	 * Restful API for get access_token
	 *
	 * @param parameters
	 *            Map
	 * @return OAuth2AccessToken
	 */
	public OAuth2AccessToken accessToken(Map<String, String> parameters) {

		// 固定的值
		parameters.put("client_id", "kongque_cloud");
		parameters.put("client_secret", "kongque_cloud");
		parameters.put("grant_type", "password");
		parameters.put("scope", "read");

		String clientId = getClientId(parameters);
		ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(clientId);

		TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(parameters, authenticatedClient);

		if (clientId != null && !"".equals(clientId)) {
			// Only validate the client details if a client authenticated during
			// this
			// request.
			if (!clientId.equals(tokenRequest.getClientId())) {
				// double check to make sure that the client ID in the token
				// request is the same as that in the
				// authenticated client
				throw new InvalidClientException("Given client ID does not match authenticated client");
			}
		}

		if (authenticatedClient != null) {
			oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
		}

		final String grantType = tokenRequest.getGrantType();
		if (!StringUtils.hasText(grantType)) {
			throw new InvalidRequestException("Missing grant type");
		}
		if ("implicit".equals(grantType)) {
			throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
		}

		if (isAuthCodeRequest(parameters)) {
			// The scope was requested or determined during the authorization
			// step
			if (!tokenRequest.getScope().isEmpty()) {
				LOG.debug("Clearing scope of incoming token request");
				tokenRequest.setScope(Collections.<String> emptySet());
			}
		}

		if (isRefreshTokenRequest(parameters)) {
			// A refresh token has its own default scopes, so we should ignore
			// any added by the factory here.
			tokenRequest.setScope(OAuth2Utils.parseParameterList(parameters.get(OAuth2Utils.SCOPE)));
		}

		OAuth2AccessToken token = getTokenGranter(grantType).grant(grantType, tokenRequest);
		if (token == null) {
			throw new UnsupportedGrantTypeException("Unsupported grant type: " + grantType);
		}

		return token;

	}

	protected TokenGranter getTokenGranter(String grantType) {

		/*
		 * if ("authorization_code".equals(grantType)) { return new
		 * AuthorizationCodeTokenGranter(tokenServices,
		 * authorizationCodeServices, clientDetailsService,
		 * this.oAuth2RequestFactory); } else if
		 * ("refresh_token".equals(grantType)) { return new
		 * RefreshTokenGranter(tokenServices, clientDetailsService,
		 * this.oAuth2RequestFactory); } else if
		 * ("client_credentials".equals(grantType)) { return new
		 * ClientCredentialsTokenGranter(tokenServices, clientDetailsService,
		 * this.oAuth2RequestFactory); } else if ("implicit".equals(grantType))
		 * { return new ImplicitTokenGranter(tokenServices,
		 * clientDetailsService, this.oAuth2RequestFactory); }
		 */
		if ("password".equals(grantType)) {
			return new ResourceOwnerPasswordTokenGranter(getAuthenticationManager(), tokenServices,
					clientDetailsService, this.oAuth2RequestFactory);
		} else {
			throw new UnsupportedGrantTypeException("Unsupport grant_type: " + grantType);
		}
	}

	private boolean isRefreshTokenRequest(Map<String, String> parameters) {
		return "refresh_token".equals(parameters.get("grant_type")) && parameters.get("refresh_token") != null;
	}

	private boolean isAuthCodeRequest(Map<String, String> parameters) {
		return "authorization_code".equals(parameters.get("grant_type")) && parameters.get("code") != null;
	}

	protected String getClientId(Map<String, String> parameters) {
		return parameters.get("client_id");
	}

	private AuthenticationManager getAuthenticationManager() {
		return this.authenticationManager;
	}

	protected WebResponseExceptionTranslator getExceptionTranslator() {
		return providerExceptionHandler;
	}

	@Override
	public boolean checkoutUrl(String targetUri, String token, String method) {
		String securityAll = redisService.get(Constants.REDIS_HASH_KEY.SECURITY_ALL_KEY);
		if (org.apache.commons.lang3.StringUtils.isNotBlank(securityAll)) {
			@SuppressWarnings("unchecked")
			List<String> setAll = JSONArray.fromObject(securityAll);
			if (setAll.size() > 0) {
				if (setAll.stream().anyMatch(s -> new AntPathMatcher().match(s.split("\\+")[0], targetUri))) {
					String security = redisService.get(Constants.REDIS_HASH_KEY.SECURITY_KEY + token);
					if (org.apache.commons.lang3.StringUtils.isNotBlank(security)) {
						@SuppressWarnings("unchecked")
						List<String> set = JSONArray.fromObject(security);
						if (!set.stream().anyMatch(s -> (new AntPathMatcher().match(s.split("\\+")[0], targetUri)
								&& ((s.split("\\+").length==1)||(("读").equals(s.split("\\+")[1]) && (!("GET").equals(method))))))) {
							return false;
						}
					} else {
						return false;
					}
				}
			}
		}
		return true;
	}

}