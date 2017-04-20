package devnoh.demoapp.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Security {

    @NotNull
    private String sig;
}
