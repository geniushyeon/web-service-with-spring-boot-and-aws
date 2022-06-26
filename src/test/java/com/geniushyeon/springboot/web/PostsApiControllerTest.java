package com.geniushyeon.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geniushyeon.springboot.domain.posts.Posts;
import com.geniushyeon.springboot.domain.posts.PostsRepository;
import com.geniushyeon.springboot.web.dto.PostsSaveRequestDto;
import com.geniushyeon.springboot.web.dto.PostsUpdateRequestDto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private PostsRepository postsRepository;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}

	@AfterEach
	public void tearDown() throws Exception {
		postsRepository.deleteAll();
	}

	@Test
	@WithMockUser(roles = "USER")
		// 인증된 모의 사용자 만들어 추가
	void posts_등록된다() throws Exception {

		// given
		String title = "title";
		String content = "content";
		PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
				.title(title)
				.content(content)
				.author("author")
				.build();

		String url = "http://localhost:" + port + "/api/v1/posts";

		// when
		mockMvc.perform(post(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(requestDto)))
				.andExpect(status().isOk());

		// then
		List<Posts> all = postsRepository.findAll();
		assertThat(all.get(0).getTitle()).isEqualTo(title);
		assertThat(all.get(0).getContent()).isEqualTo(content);
	}

	@Test
	@WithMockUser(roles = "USER")
	void posts_수정된다() throws Exception {

		// given
		Posts savedPosts = postsRepository.save(Posts.builder()
				.title("title")
				.content("content")
				.author("author")
				.build());

		Long updateId = savedPosts.getId();
		String expectedTitle = "title2";
		String expectedContent = "content2";

		PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
				.title(expectedTitle)
				.content(expectedContent)
				.build();

		String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

		HttpEntity<PostsUpdateRequestDto> requestDtoHttpEntity = new HttpEntity<>(requestDto);

		// when
		mockMvc.perform(put(url)
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(requestDto)))
				.andExpect(status().isOk());

		// then
		List<Posts> findAll = postsRepository.findAll();
		assertThat(findAll.get(0).getTitle()).isEqualTo(expectedTitle);
		assertThat(findAll.get(0).getContent()).isEqualTo(expectedContent);
	}
}
