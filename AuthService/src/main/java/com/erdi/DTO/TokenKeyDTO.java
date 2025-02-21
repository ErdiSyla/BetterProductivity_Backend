package com.erdi.DTO;

import com.erdi.Models.KeyActivity;

public record TokenKeyDTO (Integer id, String publicKey, String privateKey,
                           KeyActivity keyActivity){
}
