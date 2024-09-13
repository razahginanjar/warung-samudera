package RazahDev.WarungAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericResponse<T> {
    private T data;

    private String message;

    private ResponsePaging responsePaging;

    private Integer status;
}
