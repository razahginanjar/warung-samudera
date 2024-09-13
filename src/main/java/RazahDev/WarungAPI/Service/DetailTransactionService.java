package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.Entity.DetailTransactions;
import RazahDev.WarungAPI.Entity.Transaction;
import RazahDev.WarungAPI.DTO.Transaction.BillDetailRequest;

public interface DetailTransactionService {
    DetailTransactions create(BillDetailRequest billdetail, Transaction transaction);

}
