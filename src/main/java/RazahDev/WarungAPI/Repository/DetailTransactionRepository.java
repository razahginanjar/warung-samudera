package RazahDev.WarungAPI.Repository;

import RazahDev.WarungAPI.Entity.DetailTransactions;
import RazahDev.WarungAPI.Entity.ProductPrice;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailTransactionRepository extends JpaRepository<DetailTransactions, String>
        , JpaSpecificationExecutor<DetailTransactions> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    <S extends DetailTransactions> S save(S entity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    void delete(DetailTransactions entity);
}
