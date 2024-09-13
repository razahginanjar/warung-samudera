package RazahDev.WarungAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsePaging {
    private Integer count;
    private Integer totalPage;
    private Integer page;
    private Integer size;
}
