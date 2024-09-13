package RazahDev.WarungAPI.Repository;

import RazahDev.WarungAPI.Entity.Products;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Products, String>, JpaSpecificationExecutor<Products> {

    Page<Products> findAllByBranch_Id(@Param("id") String id, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    <S extends Products> S save(S entity);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Override
    void delete(Products entity);

}
