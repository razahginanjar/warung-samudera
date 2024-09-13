package RazahDev.WarungAPI.DTO.Transaction;

import RazahDev.WarungAPI.DTO.Product.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillDetailResponse {

    private String billDetailId;
    private String billId;
    private ProductResponse products;
    private Long totalSales;
    private Integer quantity;
}
