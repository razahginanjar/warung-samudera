package RazahDev.WarungAPI.Repository;

import RazahDev.WarungAPI.Entity.ProductPrice;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, String>, JpaSpecificationExecutor<ProductPrice> {
    Optional<ProductPrice> findFirstByPrice(@Param("price") Long price);

    Boolean existsByPrice(@Param("price") Long price);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Override
    <S extends ProductPrice> S save(S entity);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Override
    void delete(ProductPrice entity);
}
