package RazahDev.WarungAPI.Controller;

import RazahDev.WarungAPI.Constant.APIUrl;
import RazahDev.WarungAPI.DTO.GenericResponse;
import RazahDev.WarungAPI.DTO.Product.ProductRequest;
import RazahDev.WarungAPI.DTO.Product.ProductResponse;
import RazahDev.WarungAPI.DTO.Product.UpdateProductRequest;
import RazahDev.WarungAPI.DTO.ResponsePaging;
import RazahDev.WarungAPI.Service.Impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = APIUrl.PRODUCT_API)
public class ProductController {

    private final ProductServiceImpl productServicesImpl;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<ProductResponse>> create(@RequestBody ProductRequest request)
    {
        ProductResponse productResponse = productServicesImpl.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GenericResponse.<ProductResponse>builder()
                        .data(productResponse)
                        .message("success")
                        .status(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<List<ProductResponse>>> getList(@RequestParam(name = "size", defaultValue = "10") Integer size,
                                                          @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                          @RequestParam(name = "code", required = false) String Code,
                                                          @RequestParam(name = "name", required = false) String Name,
                                                          @RequestParam(name = "minPrice", required = false) Long minPrice,
                                                          @RequestParam(name = "maxPrice", required = false) Long maxPrice )
    {
        Page<ProductResponse> list = productServicesImpl.getList(size, page, Code, Name, minPrice, maxPrice);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<List<ProductResponse>>builder()
                        .data(list.getContent())
                        .responsePaging(
                                ResponsePaging.builder()
                                        .count(list.getNumberOfElements())
                                        .page(list.getNumber())
                                        .size(list.getSize())
                                        .totalPage(list.getTotalPages())
                                        .build()
                        )
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping(
            path = "{id_branch}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<List<ProductResponse>>> getListByBranch(@PathVariable(name = "id_branch") String idBranch,
                                                                  @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                                  @RequestParam(name = "page", defaultValue = "0") Integer page)
    {
        Page<ProductResponse> listByBranch = productServicesImpl.getListByBranch(idBranch, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<List<ProductResponse>>builder()
                        .data(listByBranch.getContent())
                        .responsePaging(
                                ResponsePaging.builder()
                                        .totalPage(listByBranch.getTotalPages())
                                        .size(listByBranch.getSize())
                                        .count(listByBranch.getNumberOfElements())
                                        .page(listByBranch.getNumber())
                                        .build()
                        )
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<ProductResponse>> update(@RequestBody UpdateProductRequest request)
    {
        ProductResponse update = productServicesImpl.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<ProductResponse>builder()
                        .data(update)
                        .message("success")
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping(
            path = "{id_product}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable(name = "id_product") String idProduct)
    {
        productServicesImpl.delete(idProduct);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<String>builder()
                        .data("OK")
                        .message("success")
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }


}
