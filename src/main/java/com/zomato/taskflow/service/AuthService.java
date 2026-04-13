package com.zomato.taskflow.service;

import com.zomato.taskflow.dto.*;
import com.zomato.taskflow.entity.User;
import com.zomato.taskflow.exception.ConflictException;
import com.zomato.taskflow.repository.UserRepository;
import com.zomato.taskflow.security.JwtService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public GlobalApiResponse register(RegisterRequest request) {
		log.info("Received registration request for email={}", request.email());
		try {
			String email = normalizeEmail(request.email());
			log.info("Normalized email={}", email);

			validateRegistrationRequest(email);

			User user = new User();
			user.setName(request.name().trim());
			user.setEmail(email);
			user.setPasswordHash(passwordEncoder.encode(request.password()));

			userRepository.save(user);
			log.info("User registered successfully with id={}", user.getId());

			return buildAuthResponse(user);

		} catch (ConflictException ce) {
			log.warn("Registration conflict for email={}", request.email(), ce);
			throw new ConflictException("Invalid registration request");

		} catch (Exception e) {
			log.error("Unexpected error during registration for email={}", request.email(), e);
			throw new ConflictException("User registration failed");
		}
	}

	public GlobalApiResponse login(LoginRequest request) {
		String email = request.email();
		log.info("Received login request for email={}", email);

		try {
			User user = validateLoginRequest(email, request.password());
			log.info("Login successful for userId={}", user.getId());

			return buildAuthResponse(user);

		} catch (ConflictException ce) {
			log.warn("Invalid login attempt for email={}", email, ce);
			throw new ConflictException("Invalid login request");

		} catch (Exception e) {
			log.error("Unexpected error during login for email={}", email, e);
			return GlobalApiResponse.failure(null, "404", null, "not found", null);
		}
	}

	private static String normalizeEmail(String email) {
		return email.trim().toLowerCase();
	}

	private GlobalApiResponse buildAuthResponse(User user) {
		try {
			log.info("Generating auth response for userId={}", user.getId());

			String token = jwtService.createToken(user);

			AuthResponse authResponse = new AuthResponse(
					token,
					"Bearer",
					jwtService.expirationSeconds(),
					new UserSummaryResponse(user.getId(), user.getName(), user.getEmail())
			);

			Map<String, Object> data = new HashMap<>();
			data.put("authResponse", authResponse);

			log.info("Auth response created successfully for userId={}", user.getId());

			return GlobalApiResponse.success(user.getId().toString(), data);

		} catch (Exception e) {
			log.error("Error building auth response for userId={}", user.getId(), e);
			throw e;
		}
	}

	private void validateRegistrationRequest(String email) {
		try {
			log.info("Validating registration for email={}", email);

			if (userRepository.existsByEmailIgnoreCase(email)) {
				log.warn("Email already registered: {}", email);
				throw new ConflictException("Email already registered");
			}

		} catch (ConflictException ce) {
			throw ce;
		} catch (Exception e) {
			log.error("Error during registration validation for email={}", email, e);
			throw e;
		}
	}

	private User validateLoginRequest(String email, String password) {
		try {
			log.info("Validating login for email={}", email);

			User user = userRepository.findByEmailIgnoreCase(email)
					.orElseThrow(() -> {
						log.warn("User not found for email={}", email);
						return new RuntimeException("Invalid credentials");
					});

			if (!passwordEncoder.matches(password, user.getPasswordHash())) {
				log.warn("Password mismatch for email={}", email);
				throw new ConflictException("Invalid credentials");
			}

			return user;

		} catch (ConflictException ce) {
			throw ce;
		} catch (Exception e) {
			log.error("Error during login validation for email={}", email, e);
			throw e;
		}
	}
}