package RazahDev.WarungAPI.Repository;

import RazahDev.WarungAPI.Entity.Branch;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, String> {
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Override
    <S extends Branch> S save(S entity);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Override
    void delete(Branch entity);
}
