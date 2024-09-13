package RazahDev.WarungAPI.DTO.Product;

import RazahDev.WarungAPI.DTO.Branch.BranchResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private String productId;

    private String code;

    private String name;

    private Long price;

    private String productPriceId;

    private BranchResponse branch;
}
