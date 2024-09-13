package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.Entity.DetailTransactions;
import RazahDev.WarungAPI.Entity.Transaction;
import RazahDev.WarungAPI.DTO.Transaction.GetTotalSalesResponse;
import RazahDev.WarungAPI.DTO.Transaction.TransactionRequest;
import RazahDev.WarungAPI.DTO.Transaction.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface TransactionService {
    TransactionResponse create(TransactionRequest request);
    TransactionResponse get(String idBill);
    Page<TransactionResponse> getList(Integer size, Integer page,
                                      String receiptNumber, String startDate, String endDate,
                                      String transType, String productName);
    GetTotalSalesResponse getTotalSales(String startDate, String endDate);
    TransactionResponse TransactionsToResponseTransaction(Transaction transactions);
    List<TransactionResponse> DetailTransactionToTransaction(List<DetailTransactions> detailTransactionsList);
    Specification<DetailTransactions> specificationTransaction(String receiptNumber, String startDate, String endDate,
                                                                      String transType, String productName);
    Specification<Transaction> specificationSales(String startDate, String endDate);
    List<TransactionResponse> getAllPerCustomer(String idCustomer);
}
