package com.zomato.taskflow;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AuthAndTaskIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void register_returns_success_and_access_token() throws Exception {
		String body = """
				{"name":"Alice","email":"alice-paginate@example.com","password":"secretpass12"}
				""";
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("SUCCESS"))
				.andExpect(jsonPath("$.data.auth_response.access_token").exists());
	}

	@Test
	void list_projects_without_bearer_token_returns_unauthorized() throws Exception {
		mockMvc.perform(get("/projects"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void login_with_valid_credentials_returns_token() throws Exception {
		String email = "carol-login@example.com";
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Carol\",\"email\":\"" + email + "\",\"password\":\"secretpass12\"}"))
				.andExpect(status().isOk());

		String loginBody = "{\"email\":\"" + email + "\",\"password\":\"secretpass12\"}";
		MvcResult login = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(loginBody))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("SUCCESS"))
				.andReturn();
		String accessToken = JsonPath.read(login.getResponse().getContentAsString(), "$.data.auth_response.access_token");
		assertThat(accessToken).isNotBlank();
	}
}
