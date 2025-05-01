package com.erdi.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "user_model")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CustomerModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userModelSeq")
	@SequenceGenerator(name = "userModelSeq", sequenceName = "user_model_sequence")
	private Integer id;

	@Column(name = "username",nullable = false, unique = false)
	private String username;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "password", nullable = false)
	private String password;

}
