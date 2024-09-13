package RazahDev.WarungAPI.DTO.Transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDetailRequest {
    @NotBlank
    private String productId;
    @NotNull
    private Integer quantity;
}
