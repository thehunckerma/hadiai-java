
package com.hadiai.security.jwt;

import com.hadiai.model.User;
import com.hadiai.repository.UserRepository;
import com.hadiai.security.services.UserDetailsImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpServletRequest _request;

	@Value("${hadiai.app.jwtSecret}")
	private String jwtSecret;

	@Value("${hadiai.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public User getUserFromJWT() {
		String token = parseJwt(_request);
		if (validateJwtToken(token)) {
			String username = getUserNameFromJwtToken(token);
			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
			return user;
		} else {
			return null;
		}
	}

	public Long getUserIdFromJWT() {
		User user = getUserFromJWT();
		if (user == null) {
			return null;
		}
		return user.getId();
	}

	public String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader("Authorization");

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}
}