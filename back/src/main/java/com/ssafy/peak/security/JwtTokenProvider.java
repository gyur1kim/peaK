package com.ssafy.peak.security;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ssafy.peak.dto.JwtTokenDto;
import com.ssafy.peak.exception.CustomException;
import com.ssafy.peak.exception.CustomExceptionType;
import com.ssafy.peak.repository.UserRepository;
import com.ssafy.peak.util.RedisUtil;
import com.ssafy.peak.util.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider implements InitializingBean {

	@Value("${jwt.secret}")
	private String secretKey;
	private long accessTokenValidTime;
	private long refreshTokenValidTime;
	private UserRepository userRepository;
	private RedisUtil redisUtil;
	private Key key;

	public JwtTokenProvider(
		@Value("${jwt.access-token-valid-time}") long accessTokenValidTime,
		@Value("${jwt.refresh-token-valid-time}") long refreshTokenValidTime,
		UserRepository userRepository,
		RedisUtil redisUtil) {
		this.accessTokenValidTime = accessTokenValidTime * 1000;
		this.refreshTokenValidTime = refreshTokenValidTime * 1000;
		this.userRepository = userRepository;
		this.redisUtil = redisUtil;
	}

	/**
	 * secret key 설정
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * AccessToken 생성
	 *
	 * @return AccessToken
	 */
	public String createAccessToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();

		Date now = new Date();
		Date expiration = new Date(now.getTime() + accessTokenValidTime);

		String accessToken = Jwts.builder()
			.setSubject(userPrincipal.getName()) // user id
			.claim(Utils.ROLE, userPrincipal.getRole()) // ROLE_USER 권한
			.setIssuedAt(now) // 액세스 토큰 발행 시간
			.setExpiration(expiration) // 액세스 토큰 유효 시간
			.signWith(SignatureAlgorithm.HS512, key) // 사용할 암호화 알고리즘 (HS512), signature 에 들어갈 secret key 세팅
			.compact();

		return accessToken;
	}

	/**
	 * RefreshToken 생성
	 *
	 * @return RefreshToken
	 */
	public void createRefreshToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();

		Date now = new Date();
		Date expiration = new Date(now.getTime() + refreshTokenValidTime);

		String refreshToken = Jwts.builder()
			.setSubject(userPrincipal.getName()) // user id
			.claim(Utils.ROLE, userPrincipal.getRole()) // ROLE_USER 권한
			.setIssuedAt(now) // 리프레시 토큰 발행 시간
			.setExpiration(expiration) // 리프레시 토큰 유효 시간
			.signWith(SignatureAlgorithm.HS512, secretKey) // 사용할 암호화 알고리즘 (HS512), signature 에 들어갈 secret key 세팅
			.compact();

		String key = "RT:" + Encoders.BASE64.encode(userPrincipal.getName().getBytes());
		redisUtil.setDataExpire(key, refreshToken, refreshTokenValidTime);
	}

	/**
	 * 인증 정보 조회
	 */
	public Authentication getAuthentication(String token) {
		Claims claims = parseClaims(token);

		if (claims.get(Utils.ROLE) == null) {
			throw new CustomException(CustomExceptionType.AUTHORITY_ERROR);
		}
		UserPrincipal userPrincipal = userRepository.findById(Long.valueOf(claims.getSubject()))
			.map(UserPrincipal::createUserPrincipal)
			.orElseThrow(() -> new CustomException(CustomExceptionType.AUTHORITY_ERROR));

		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(userPrincipal.getRole().toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		return new UsernamePasswordAuthenticationToken(userPrincipal, token, authorities);
	}

	/**
	 * 토큰 유효성 검사
	 * return 유효 토큰 여부
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			log.info("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.info("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT 토큰이 잘못되었습니다.");
		}
		return false;
	}

	/**
	 * Jwt 복호화
	 */
	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(secretKey).build()
				.parseClaimsJws(token)
				.getBody();

		} catch (ExpiredJwtException expiredJwtException) {
			return expiredJwtException.getClaims();
		}
	}

	/**
	 * Jwt 복호화 후 user id 가져오기
	 */
	public long getUserIdFromJwt(String token) {
		try {
			String userId = Jwts.parserBuilder()
				.setSigningKey(secretKey).build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
			return Long.parseLong(userId);

		} catch (ExpiredJwtException expiredJwtException) {
			return expiredJwtException.getClaims().getExpiration().getTime();
		}
	}

	/**
	 * Jwt 복호화 후 token 만료 시간 가져오기
	 */
	public long getExpiration(String token) {
		try {
			long expiration = Jwts.parserBuilder()
				.setSigningKey(secretKey).build()
				.parseClaimsJws(token)
				.getBody()
				.getExpiration()
				.getTime();
			long now = new Date().getTime();
			return expiration - now;

		} catch (ExpiredJwtException expiredJwtException) {
			return expiredJwtException.getClaims().getExpiration().getTime();
		}
	}

	public String resolveToken(HttpServletRequest httpServletRequest) {
		String bearerToken = httpServletRequest.getHeader(Utils.AUTHORIZATION);

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Utils.BEARER_TOKEN_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public JwtTokenDto reissue(String token) {
		Authentication authentication = getAuthentication(token);
		UserPrincipal userPrincipal = (UserPrincipal)authentication.getPrincipal();
		String email = userPrincipal.getUsername();
		String key = "RT:" + Encoders.BASE64.encode(email.getBytes());
		String refreshToken = redisUtil.getData(key);
		if (refreshToken == null) {
			throw new CustomException(CustomExceptionType.REFRESH_TOKEN_ERROR);
		}

		String accessToken = createAccessToken(authentication);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return JwtTokenDto.builder()
			.token(accessToken)
			.expiration(getExpiration(accessToken))
			.build();
	}
}
