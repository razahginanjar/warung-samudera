package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.Entity.Products;
import RazahDev.WarungAPI.DTO.Product.ProductRequest;
import RazahDev.WarungAPI.DTO.Product.ProductResponse;
import RazahDev.WarungAPI.DTO.Product.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    Products getProduct(String id);
    Page<ProductResponse> getList(Integer size, Integer page, String Code, String Name, Long minPrice, Long maxPrice);
    Page<ProductResponse> getListByBranch(String idBranch, Integer page, Integer size);
    Page<ProductResponse> getProducts();
    ProductResponse update(UpdateProductRequest request);
    void delete(String idProduct);
    Specification<Products> specificationList(String Code, String Name, Long minPrice, Long maxPrice);
    ProductResponse ProductToProductResponse(Products products);
}
