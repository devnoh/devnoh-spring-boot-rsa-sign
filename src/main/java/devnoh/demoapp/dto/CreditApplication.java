package devnoh.demoapp.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreditApplication {

    @NotNull
    private String applicationId;
}
