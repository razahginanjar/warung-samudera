package RazahDev.WarungAPI.Service.Impl;

import RazahDev.WarungAPI.Entity.Branch;
import RazahDev.WarungAPI.Entity.ProductPrice;
import RazahDev.WarungAPI.Entity.Products;
import RazahDev.WarungAPI.DTO.Branch.BranchResponse;
import RazahDev.WarungAPI.DTO.Product.ProductRequest;
import RazahDev.WarungAPI.DTO.Product.ProductResponse;
import RazahDev.WarungAPI.DTO.Product.UpdateProductRequest;
import RazahDev.WarungAPI.Repository.ProductRepository;
import RazahDev.WarungAPI.Service.ProductService;
import RazahDev.WarungAPI.Util.ValidationService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final ValidationService validator;

    private final PriceServiceImpl priceServiceImpl;

    private final BranchServiceImpl branchServiceImpl;

    @Transactional(rollbackFor = Exception.class)
    public ProductResponse create(ProductRequest request)
    {
        validator.validate(request);
        Branch branch = branchServiceImpl.getById(request.getBranchId());

        ProductPrice price = priceServiceImpl.getPrice(request.getPrice());

        Products products = new Products();
        products.setId(UUID.randomUUID().toString());
        products.setCode(request.getCode());
        products.setName(request.getName());
        products.setBranch(branch);
        products.setProductPrice(price);
        productRepository.save(products);

        BranchResponse branchResponse = branchServiceImpl.toBranchResponse(products.getBranch());

        return ProductResponse.builder()
                .productPriceId(price.getId())
                .productId(products.getId())
                .name(products.getName())
                .code(products.getCode())
                .price(price.getPrice())
                .branch(branchResponse)
                .build();
    }

    @Transactional(readOnly = true)
    public Products getProduct(String id)
    {
        return productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
        );
    }
    @Transactional(readOnly = true)
    public Page<ProductResponse> getList(Integer size, Integer page, String Code, String Name, Long minPrice, Long maxPrice)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("name")));

        Specification<Products> specification = specificationList(Code, Name, minPrice, maxPrice);
        Page<Products> productsPage = productRepository.findAll(specification, pageable);
        List<ProductResponse> responseList = productsPage.getContent().stream().map(
                this::ProductToProductResponse
        ).toList();

        return new PageImpl<>(responseList, pageable, productsPage.getTotalElements());
    }
    @Transactional(readOnly = true)
    public Page<ProductResponse> getListByBranch(String idBranch, Integer page, Integer size)
    {
        if(branchServiceImpl.checkBranch(idBranch))
        {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc("name")));
            Page<Products> allByBranchId = productRepository.findAllByBranch_Id(idBranch, pageable);
            List<ProductResponse> productResponseList = allByBranchId.stream().map(
                    this::ProductToProductResponse
            ).toList();
            return new PageImpl<>(productResponseList, pageable, allByBranchId.getTotalElements());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Branch is not found");
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts()
    {
        Pageable pageable = PageRequest.of(1, 50, Sort.by(Sort.Order.asc("name")));
        Page<Products> productsPage = productRepository.findAll(pageable);
        List<ProductResponse> responseList = productsPage.getContent().stream().map(
                this::ProductToProductResponse
        ).toList();
        return new PageImpl<>(responseList, pageable, productsPage.getTotalElements());
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductResponse update(UpdateProductRequest request)
    {
        validator.validate(request);

        Products products = productRepository.findById(request.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Products is not found")
        );

        ProductPrice price = priceServiceImpl.getPrice(request.getPrice());

        Branch branch = branchServiceImpl.getById(request.getBranchId());
        products.setCode(request.getCode());
        products.setName(request.getName());
        products.setBranch(branch);
        products.setProductPrice(price);
        productRepository.save(products);

        BranchResponse branchResponse = branchServiceImpl.toBranchResponse(products.getBranch());

        return ProductResponse.builder()
                .name(products.getName())
                .code(products.getCode())
                .productId(products.getId())
                .productPriceId(products.getProductPrice().getId())
                .price(products.getProductPrice().getPrice())
                .branch(branchResponse)
                .build();

    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String idProduct)
    {
        Products products = productRepository.findById(idProduct).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
        );
        productRepository.delete(products);
    }
    public Specification<Products> specificationList(String Code, String Name, Long minPrice, Long maxPrice)
    {
        return (root, query, criteriaBuilder) ->
        {
            Join<Products, ProductPrice> productPriceJoin = root.join("productPrice");
            List<Predicate> predicates = new ArrayList<>();

            if(Objects.nonNull(Code) && Objects.nonNull(Name) && Objects.nonNull(minPrice) && Objects.nonNull(maxPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.like(root.get("name"), "%"+Name+"%"),
                                criteriaBuilder.equal(root.get("code"), Code),
                                criteriaBuilder.greaterThanOrEqualTo(productPriceJoin.get("price"), minPrice),
                                criteriaBuilder.lessThanOrEqualTo(productPriceJoin.get("price"), maxPrice)
                        )
                );

            }
            else if(Objects.nonNull(Code) && Objects.nonNull(Name) && Objects.nonNull(minPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.like(root.get("name"), "%"+Name+"%"),
                                criteriaBuilder.equal(root.get("code"), Code),
                                criteriaBuilder.greaterThanOrEqualTo(productPriceJoin.get("price"), minPrice)
                        )
                );
            }
            else if(Objects.nonNull(Code) && Objects.nonNull(Name) && Objects.nonNull(maxPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.like(root.get("name"), "%"+Name+"%"),
                                criteriaBuilder.equal(root.get("code"), Code),
                                criteriaBuilder.lessThanOrEqualTo(productPriceJoin.get("price"), maxPrice)
                        )
                );
            }
            else if(Objects.nonNull(Code) && Objects.nonNull(minPrice) && Objects.nonNull(maxPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("code"), Code),
                                criteriaBuilder.greaterThanOrEqualTo(productPriceJoin.get("price"), minPrice),
                                criteriaBuilder.lessThanOrEqualTo(productPriceJoin.get("price"), maxPrice)
                        )
                );
            }
            else if(Objects.nonNull(Name) && Objects.nonNull(minPrice) && Objects.nonNull(maxPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.like(root.get("name"), "%"+Name+"%"),
                                criteriaBuilder.greaterThanOrEqualTo(productPriceJoin.get("price"), minPrice),
                                criteriaBuilder.lessThanOrEqualTo(productPriceJoin.get("price"), maxPrice)
                        )
                );
            }

            else if(Objects.nonNull(Code) && Objects.nonNull(Name))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.like(root.get("name"), "%"+Name+"%"),
                                criteriaBuilder.equal(root.get("code"), Code)
                        )
                );
            }
            else if(Objects.nonNull(Code) && Objects.nonNull(minPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("code"), Code),
                                criteriaBuilder.greaterThanOrEqualTo(productPriceJoin.get("price"), minPrice)
                        )
                );
            }
            else if(Objects.nonNull(Name) && Objects.nonNull(minPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.like(root.get("name"), "%"+Name+"%"),
                                criteriaBuilder.greaterThanOrEqualTo(productPriceJoin.get("price"), minPrice)
                        )
                );
            }

            else if(Objects.nonNull(Code) && Objects.nonNull(maxPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get("code"), Code),
                                criteriaBuilder.lessThanOrEqualTo(productPriceJoin.get("price"), maxPrice)
                        )
                );
            }
            else if(Objects.nonNull(Name) && Objects.nonNull(maxPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.like(root.get("name"), "%"+Name+"%"),
                                criteriaBuilder.lessThanOrEqualTo(productPriceJoin.get("price"), maxPrice)
                        )
                );
            }

            else if(Objects.nonNull(minPrice) && Objects.nonNull(maxPrice))
            {
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(productPriceJoin.get("price"), minPrice),
                                criteriaBuilder.lessThanOrEqualTo(productPriceJoin.get("price"), maxPrice)
                        )
                );
            }

            else if(Objects.nonNull(Code))
            {
                predicates.add(
                        criteriaBuilder.equal(root.get("code"), Code)
                );
            }
            else if(Objects.nonNull(Name))
            {
                predicates.add(
                        criteriaBuilder.like(root.get("name"), "%"+Name+"%")
                );
            }
            else if(Objects.nonNull(minPrice))
            {
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(productPriceJoin.get("price"), minPrice)
                );
            }
            else if(Objects.nonNull(maxPrice))
            {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(productPriceJoin.get("price"), maxPrice)
                );
            }
            assert query != null;
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductResponse ProductToProductResponse(Products products)
    {
        BranchResponse branchResponse = branchServiceImpl.toBranchResponse(products.getBranch());

        return ProductResponse.builder()
                .branch(branchResponse)
                .productPriceId(products.getProductPrice().getId())
                .price(products.getProductPrice().getPrice())
                .productId(products.getId())
                .code(products.getCode())
                .name(products.getName())
                .build();
    }

}

