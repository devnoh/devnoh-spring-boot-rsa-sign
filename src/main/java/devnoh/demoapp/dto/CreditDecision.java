package devnoh.demoapp.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreditDecision {

    @NotNull
    private String decisionId;
}
