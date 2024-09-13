package RazahDev.WarungAPI.Service.Impl;

import RazahDev.WarungAPI.Entity.DetailTransactions;
import RazahDev.WarungAPI.Entity.Products;
import RazahDev.WarungAPI.Entity.Transaction;
import RazahDev.WarungAPI.DTO.Transaction.BillDetailRequest;
import RazahDev.WarungAPI.Repository.DetailTransactionRepository;
import RazahDev.WarungAPI.Service.DetailTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DetailTransactionServiceImpl implements DetailTransactionService {
    private final DetailTransactionRepository detailTransactionRepository;
    private final ProductServiceImpl productServiceImpl;

    @Transactional(rollbackFor = Exception.class)
    public DetailTransactions create(BillDetailRequest billdetail, Transaction transaction)
    {
        DetailTransactions detailTransactions = new DetailTransactions();
        detailTransactions.setBillDetailId(UUID.randomUUID().toString());
        detailTransactions.setQuantity(billdetail.getQuantity());

        Products products = productServiceImpl.getProduct(billdetail.getProductId());

        detailTransactions.setProductsDetail(products);
        detailTransactions.setTransactionDetailJoins(transaction);
        detailTransactions.setTotalSales(billdetail.getQuantity() * products.getProductPrice().getPrice());
        return detailTransactionRepository.save(detailTransactions);
    }

    @Transactional(readOnly = true)
    public Page<DetailTransactions> findAll(Specification<DetailTransactions> specification, Pageable pageable)
    {
        return detailTransactionRepository.findAll(specification, pageable);
    }
}
