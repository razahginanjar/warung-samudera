package RazahDev.WarungAPI.Controller;

import RazahDev.WarungAPI.Constant.APIUrl;
import RazahDev.WarungAPI.DTO.GenericResponse;
import RazahDev.WarungAPI.DTO.ResponsePaging;
import RazahDev.WarungAPI.DTO.Transaction.GetTotalSalesResponse;
import RazahDev.WarungAPI.DTO.Transaction.TransactionRequest;
import RazahDev.WarungAPI.DTO.Transaction.TransactionResponse;
import RazahDev.WarungAPI.Service.Impl.TransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = APIUrl.TRANSACTION_API)
public class TransactionController {

    private final TransactionServiceImpl transactionServiceImpl;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<TransactionResponse>> create(@RequestBody TransactionRequest request)
    {
        TransactionResponse transactionResponse = transactionServiceImpl.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GenericResponse.<TransactionResponse>builder()
                        .data(transactionResponse)
                        .status(HttpStatus.CREATED.value())
                        .message("success")
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping(
            path = "/{id_bill}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<TransactionResponse>> get(@PathVariable(name = "id_bill") String idBill)
    {
        TransactionResponse transactionResponse = transactionServiceImpl.get(idBill);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<TransactionResponse>builder()
                        .data(transactionResponse)
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .build()
        );
    }

    @GetMapping(
          path = "/{id_customer}",
          produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<List<TransactionResponse>>> getAllTransactionByCustomer(
            @PathVariable("id_customer")String idCustomer
    )
    {
        List<TransactionResponse> list = transactionServiceImpl.getAllPerCustomer(idCustomer);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<List<TransactionResponse>>builder()
                        .data(list)
                        .status(HttpStatus.OK.value())
                        .message("Succcess get all transaction customer")
                        .build()
        );
    }

    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<List<TransactionResponse>>>getList(@RequestParam(name = "size", defaultValue = "10") Integer size,
                                                              @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(name = "receiptNumber", required = false) String receiptNumber,
                                                              @RequestParam(name = "startDate", required = false) String startDate,
                                                              @RequestParam(name = "endDate", required = false) String endDate,
                                                              @RequestParam(name = "transType", required = false) String transType,
                                                              @RequestParam(name = "productName", required = false) String productName)
    {

        Page<TransactionResponse> list = transactionServiceImpl.getList(size, page, receiptNumber, startDate, endDate, transType, productName);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<List<TransactionResponse>>builder()
                        .data(list.getContent())
                        .status(HttpStatus.OK.value())
                        .responsePaging(
                                ResponsePaging.builder()
                                        .page(list.getNumber())
                                        .count(list.getNumberOfElements())
                                        .size(list.getSize())
                                        .totalPage(list.getTotalPages())
                                        .build()
                        )
                        .message("Succcess get all transaction customer")
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping(
            path = "/total-sales",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<GetTotalSalesResponse>> getTotalSales(@RequestParam(name = "startDate", required = false) String startDate,
                                                                @RequestParam(name = "endDate", required = false) String endDate)
    {
        GetTotalSalesResponse totalSales = transactionServiceImpl.getTotalSales(startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GenericResponse.<GetTotalSalesResponse>builder()
                        .data(totalSales)
                        .message("success")
                        .status(HttpStatus.OK.value())
                        .build());
    }
}
