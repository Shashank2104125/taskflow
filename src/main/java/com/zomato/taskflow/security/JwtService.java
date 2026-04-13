package com.zomato.taskflow.security;

import com.zomato.taskflow.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

	private final SecretKey signingKey;
	private final long expirationHours;

	public JwtService(
			@Value("${taskflow.jwt.secret}") String secret,
			@Value("${taskflow.jwt.expiration-hours:24}") long expirationHours) {
		this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationHours = expirationHours;
	}

	public String createToken(User user) {
		Instant now = Instant.now();
		Instant exp = now.plus(expirationHours, ChronoUnit.HOURS);
		return Jwts.builder()
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(exp))
				.signWith(signingKey)
				.compact();
	}

	public Claims parseSignedClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public long expirationSeconds() {
		return expirationHours * 3600;
	}
}
