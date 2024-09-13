package RazahDev.WarungAPI.Service.Impl;

import RazahDev.WarungAPI.Entity.Customer;
import RazahDev.WarungAPI.Entity.UserAccount;
import RazahDev.WarungAPI.Mapper.impl.CustomerMapperImpl;
import RazahDev.WarungAPI.DTO.Customer.CustomerRequest;
import RazahDev.WarungAPI.DTO.Customer.CustomerResponse;
import RazahDev.WarungAPI.DTO.Customer.UpdateCustomerRequest;
import RazahDev.WarungAPI.Repository.CustomerRepository;
import RazahDev.WarungAPI.Service.CustomerService;
import RazahDev.WarungAPI.Util.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserServiceImpl userService;
    private final ValidationService validationUtil;
    private final CustomerRepository cusRepo;
    private final CustomerMapperImpl customerMapper;

    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse create(CustomerRequest customer) {
        validationUtil.validate(customer);
        Customer build = Customer.builder()
                .name(customer.getName())
                .birthDate(Date.valueOf(customer.getBirthDate()))
                .mobilePhoneNo(customer.getMobilePhoneNo())
                .build();
        return customerMapper.toResponse(cusRepo.saveAndFlush(build));
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(String id) {
        return findByIdOrThrowNotFound(id);
    }

    @Transactional(readOnly = true)
    public Customer getByID(String id) {
        return cusRepo.findById(id).orElseThrow(() -> new RuntimeException("Customer Not Found"));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getAll() {
        return cusRepo.findAll().stream().map(
                customerMapper::toResponse
        ).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse update(UpdateCustomerRequest updateCustomerRequest) {
        Customer byID = getByID(updateCustomerRequest.getId());

        UserAccount userByContext = userService.getByContext();

        if(!userByContext.getId().equals(byID.getUserAccount().getId()))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not permission to change");
        }

        byID.setName(updateCustomerRequest.getName());
        byID.setBirthDate(Date.valueOf(updateCustomerRequest.getBirthDate()));
        byID.setMobilePhoneNo(updateCustomerRequest.getMobilePhoneNo());

        return customerMapper.toResponse(cusRepo.saveAndFlush(byID));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        Customer customerFound = cusRepo.findById(id).orElseThrow(() -> new RuntimeException("Customer Not Found"));
        cusRepo.delete(customerFound);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateStatusById(String id, Boolean status) {
        cusRepo.updateStatus(status, id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Customer createFromCustomer(Customer customer) {
        return cusRepo.saveAndFlush(customer);
    }

    @Transactional(rollbackFor = Exception.class)
    public CustomerResponse findByIdOrThrowNotFound(String id){
        return customerMapper.toResponse(cusRepo.findById(id).orElseThrow(() -> new RuntimeException("Customer Not Found")));
    }

}
