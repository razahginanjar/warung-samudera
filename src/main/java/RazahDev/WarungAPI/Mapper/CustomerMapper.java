package RazahDev.WarungAPI.Mapper;


import RazahDev.WarungAPI.Entity.Customer;
import RazahDev.WarungAPI.DTO.Customer.CustomerResponse;

public interface CustomerMapper {
    CustomerResponse toResponse(Customer customer);
    Customer toEntity(CustomerResponse customerResponse);
}
