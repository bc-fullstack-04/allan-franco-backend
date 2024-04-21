package br.com.sysmap.bootcamp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@Builder
@Getter
@Setter
public class WalletDto implements Serializable {
    private String email;
    private BigDecimal value;

    public WalletDto(String email, BigDecimal value) {
        this.email = email;
        this.value = value;
    }
}
