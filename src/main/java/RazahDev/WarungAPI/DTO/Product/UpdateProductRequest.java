package RazahDev.WarungAPI.DTO.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductRequest {

    @NotBlank
    @Size(max = 150)
    private String id;

    @NotBlank
    @Size(max = 150)
    private String code;
    @NotBlank
    @Size(max = 150)
    private String name;
    @NotBlank
    @Size(max = 150)
    private String branchId;

    private Long price;
}
