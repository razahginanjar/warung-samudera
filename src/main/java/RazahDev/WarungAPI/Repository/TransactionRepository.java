package RazahDev.WarungAPI.Repository;

import RazahDev.WarungAPI.Entity.Customer;
import RazahDev.WarungAPI.Entity.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>,
        JpaSpecificationExecutor<Transaction> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    <S extends Transaction> S save(S entity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    void delete(Transaction entity);

    List<Transaction> findAllByCustomer(Customer customer);
}
