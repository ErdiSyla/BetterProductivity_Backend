package com.erdi.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userModelSeq")
	@SequenceGenerator(name = "userModelSeq", sequenceName = "user_model_sequence");
	private Integer id;

	private String username;

	private String email;

	private String password;
}
