package RazahDev.WarungAPI.Repository;

import RazahDev.WarungAPI.Entity.SequenceGenerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SequenceGeneratorRepository extends JpaRepository<SequenceGenerator, String> {

}
