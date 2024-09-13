package RazahDev.WarungAPI.DTO.Transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TransactionResponse {
    private String customerName;
    private String billId;
    private String receiptNumber;
    private LocalDateTime transDate;
    private String transactionType;
    private List<BillDetailResponse> billDetailResponseList;
}
