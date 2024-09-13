package RazahDev.WarungAPI.Mapper.impl;


import RazahDev.WarungAPI.Entity.Customer;
import RazahDev.WarungAPI.Mapper.CustomerMapper;
import RazahDev.WarungAPI.DTO.Customer.CustomerResponse;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapperImpl implements CustomerMapper {
    @Override
    public CustomerResponse toResponse(Customer customer) {
        String idUser = null;
        if(customer.getUserAccount() != null)
        {
            idUser = customer.getUserAccount().getId();
        }
        return CustomerResponse.builder()
                .id(customer.getId())
                .mobilePhoneNo(customer.getMobilePhoneNo())
                .name(customer.getName())
                .status(customer.getStatus())
                .userAccountId(idUser)
                .build();
    }

    @Override
    public Customer toEntity(CustomerResponse customerResponse) {
        return null;
    }
}
