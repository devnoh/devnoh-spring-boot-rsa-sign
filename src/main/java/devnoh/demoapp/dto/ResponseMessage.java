package devnoh.demoapp.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ResponseMessage {

    @NotNull
    private ResponseHeader header;

    private Object payload;

    private Security security;
}
