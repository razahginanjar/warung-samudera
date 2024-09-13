package RazahDev.WarungAPI.DTO.Transaction;

import RazahDev.WarungAPI.Entity.Transaction;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TransactionRequest {
    @NotNull(message = "Must have the type for transaction")
    private Transaction.TransactionType transactionType;
    @NotEmpty(message = "Must have a detail bill")
    private List<BillDetailRequest> requestList;
}
