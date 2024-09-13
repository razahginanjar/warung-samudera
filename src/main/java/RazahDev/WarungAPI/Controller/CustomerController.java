package RazahDev.WarungAPI.Controller;

import RazahDev.WarungAPI.Constant.APIUrl;
import RazahDev.WarungAPI.Constant.ConstantMessage;
import RazahDev.WarungAPI.DTO.Customer.CustomerRequest;
import RazahDev.WarungAPI.DTO.Customer.CustomerResponse;
import RazahDev.WarungAPI.DTO.Customer.UpdateCustomerRequest;
import RazahDev.WarungAPI.DTO.GenericResponse;
import RazahDev.WarungAPI.Service.Impl.CustomerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.CUSTOMER_API)
public class CustomerController {
    private final CustomerServiceImpl customerServiceImpl;

    @PostMapping
    public ResponseEntity<GenericResponse<CustomerResponse>> createNewCustomer(@RequestBody CustomerRequest customer){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        GenericResponse.<CustomerResponse>builder()
                                .data(customerServiceImpl.create(customer))
                                .status(HttpStatus.CREATED.value())
                                .message("Customer Successfully Created")
                                .build()
                );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping(path = APIUrl.PATH_VAR_ID)
    public ResponseEntity<GenericResponse<CustomerResponse>>  getCustomerById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(
                        GenericResponse.<CustomerResponse>builder()
                                .data(customerServiceImpl.getById(id))
                                .message("Successfully get Customer")
                                .status(HttpStatus.ACCEPTED.value())
                                .build()
                );
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping
    public ResponseEntity<GenericResponse<List<CustomerResponse>>> getAllCustomer() {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(
                        GenericResponse.<List<CustomerResponse>>builder()
                                .status(HttpStatus.ACCEPTED.value())
                                .message("Successfully get all Customer")
                                .data(customerServiceImpl.getAll())
                                .build()
                );
    }

    @PutMapping
    public ResponseEntity<GenericResponse<CustomerResponse>>  updateCustomer(@RequestBody UpdateCustomerRequest customer) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(
                        GenericResponse.<CustomerResponse>builder()
                                .data(customerServiceImpl.update(customer))
                                .message("Successfully updated product")
                                .status(HttpStatus.ACCEPTED.value())
                                .build()
                );
    }

    @DeleteMapping(path = APIUrl.PATH_VAR_ID)
    public ResponseEntity<GenericResponse<String>>  deleteCustomerById(@PathVariable String id) {
        customerServiceImpl.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        GenericResponse.<String>builder()
                                .data(ConstantMessage.DELETESUCCESS + id)
                                .status(HttpStatus.OK.value())
                                .message("Successfully deleted customer")
                                .build()
                );
    }

    @PutMapping(path = APIUrl.PATH_VAR_ID)
    public ResponseEntity<GenericResponse<String>> updateStatus(
            @PathVariable String id,
            @RequestParam(name = "status") Boolean status
    ){
        customerServiceImpl.updateStatusById(id,status);
        GenericResponse<String> response = GenericResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Oke success update status")
                .build();
        return ResponseEntity.ok(response);
    }

}
