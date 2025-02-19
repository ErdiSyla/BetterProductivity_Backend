package com.erdi.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Table(name = "token_key")
@AllArgsConstructor
@NoArgsConstructor
public class TokenKeyModel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokenKeyModelSeq")
	@SequenceGenerator(name = "tokenKeyModelSeq", sequenceName = "token_key_model_sequence")
	private Integer keyId;

	@Column(nullable = false, length = 5000)
	private String publicKey;

	@Column(nullable = false, length = 5000)
	private String privateKey;

	@Enumerated(EnumType.STRING)
	private KeyActivity keyActivity;

	@CreationTimestamp
	@Column(nullable = false,updatable = false)
	private Instant timeOfCreation;
}
