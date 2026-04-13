package com.zomato.taskflow.controller;

import com.zomato.taskflow.dto.GlobalApiResponse;
import com.zomato.taskflow.dto.LoginRequest;
import com.zomato.taskflow.dto.RegisterRequest;
import com.zomato.taskflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public GlobalApiResponse register(@Valid @RequestBody RegisterRequest request) {
		log.info("received user registration request");
		return authService.register(request);
	}

	@PostMapping("/login")
	public GlobalApiResponse login(@Valid @RequestBody LoginRequest request) {
		log.info("received user login request");
		return authService.login(request);
	}
}
