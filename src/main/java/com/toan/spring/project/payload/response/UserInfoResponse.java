package com.toan.spring.project.payload.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class UserInfoResponse {
	private Long id;
	private String username;
	private String email;
	private String name;
	private List<String> roles;

	public UserInfoResponse(Long id, String username, String email, String name, List<String> roles) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.name = name;
		this.roles = roles;
	}

}