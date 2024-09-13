package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.Entity.Customer;
import RazahDev.WarungAPI.DTO.Customer.CustomerRequest;
import RazahDev.WarungAPI.DTO.Customer.CustomerResponse;
import RazahDev.WarungAPI.DTO.Customer.UpdateCustomerRequest;

import java.util.List;

public interface CustomerService {
    CustomerResponse create(CustomerRequest customer);
    CustomerResponse getById(String id);
    Customer getByID(String id);
    List<CustomerResponse> getAll();
    CustomerResponse update(UpdateCustomerRequest updateCustomerRequest);
    void deleteById(String id);
    void updateStatusById(String id, Boolean status);
    Customer createFromCustomer(Customer customer);
    CustomerResponse findByIdOrThrowNotFound(String id);
}
