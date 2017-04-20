package devnoh.demoapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ResponseHeader {

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILURE = "failure";

    @NonNull
    @NotNull
    private String status;

    private List<ErrorDTO> errors = new ArrayList<>();
}
