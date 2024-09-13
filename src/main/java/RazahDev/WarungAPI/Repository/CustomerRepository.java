package RazahDev.WarungAPI.Repository;


import RazahDev.WarungAPI.Entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Modifying
    @Query(value = "update Customer c set c.status= :status where c.id= :id")
    void updateStatus(@Param("status") Boolean status, @Param(value = "id") String id);
}
