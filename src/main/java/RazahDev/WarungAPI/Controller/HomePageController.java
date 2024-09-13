package RazahDev.WarungAPI.Controller;

import RazahDev.WarungAPI.Constant.APIUrl;
import RazahDev.WarungAPI.DTO.GenericResponse;
import RazahDev.WarungAPI.DTO.Product.ProductResponse;
import RazahDev.WarungAPI.DTO.ResponsePaging;
import RazahDev.WarungAPI.Service.Impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = APIUrl.HOME_API)
@RequiredArgsConstructor
public class HomePageController {
    private final ProductServiceImpl productServiceImpl;

    @GetMapping
    public ResponseEntity<?> homePage()
    {
        Page<ProductResponse> list = productServiceImpl.getProducts();
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder()
                        .data(list)
                        .responsePaging(ResponsePaging.builder()
                                .count(list.getNumberOfElements())
                                .page(list.getNumber())
                                .size(list.getSize())
                                .totalPage(list.getTotalPages())
                                .build())
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .build()
        );
    }
}
