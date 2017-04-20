package devnoh.demoapp.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestMessage {

    @NotNull
    private RequestHeader header;

    private Object payload;

    private Security security;
}
