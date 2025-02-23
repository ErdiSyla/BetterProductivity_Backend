package com.erdi.DTO;


import java.time.Instant;

public record TokenKeyDTO (Integer id, String publicKey, String privateKey,
						   KeyActivity keyActivity, Instant timeOfCreation){
}
