package com.geniushyeon.springboot.config.auth.dto;

import java.io.Serializable;

import com.geniushyeon.springboot.domain.user.User;

import lombok.Getter;

@Getter
public class SessionUser implements Serializable { // 직렬화

	private String name;
	private String email;
	private String picture;

	public SessionUser(User user) {
		this.name = user.getName();
		this.email = user.getEmail();
		this.picture = user.getPicture();
	}
}
