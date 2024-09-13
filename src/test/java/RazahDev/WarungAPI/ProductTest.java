package RazahDev.WarungAPI;

import RazahDev.WarungAPI.Entity.Branch;
import RazahDev.WarungAPI.Entity.ProductPrice;
import RazahDev.WarungAPI.Entity.Products;
import RazahDev.WarungAPI.DTO.GenericResponse;
import RazahDev.WarungAPI.DTO.Product.ProductRequest;
import RazahDev.WarungAPI.DTO.Product.ProductResponse;
import RazahDev.WarungAPI.DTO.Product.UpdateProductRequest;
import RazahDev.WarungAPI.Repository.BranchRepository;
import RazahDev.WarungAPI.Repository.ProductPriceRepository;
import RazahDev.WarungAPI.Repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class ProductTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductPriceRepository priceRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp()
    {
        productRepository.deleteAll();
        priceRepository.deleteAll();
    }

    @Test
    void createProductFailedEmptyReq() throws Exception
    {
        Branch branch = new Branch();

        branch.setId(UUID.randomUUID().toString());
        branch.setAddress("RT/RW 002/001 CIbulakan");
        branch.setPhone("+6281425232");
        branch.setCode("201");
        branch.setName("Cabang Menarik");
        branchRepository.save(branch);

        ProductRequest request = new ProductRequest();

        mockMvc.perform(
                post("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void createProductSuccess() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);

        ProductRequest request = new ProductRequest();
        request.setName("Pisang Manis");
        request.setCode("002");
        request.setPrice(12000L);
        request.setBranchId(branch.getId());

        mockMvc.perform(
                post("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<GenericResponse<ProductResponse>>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(response.getData().getName(), request.getName());
                    Assertions.assertEquals(response.getData().getPrice(), request.getPrice());
                    Assertions.assertEquals(response.getData().getCode(), request.getCode());
                }
        );
    }

    @Test
    void getListAll() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12< i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(10, response.getResponsePaging().getSize());
                }
        );
    }

    @Test
    void getListProductName() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12< i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Pisang Keju")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(5, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListProductCode() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12< i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("code", "007")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListAllHavingMInPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("minPrice", "3000")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(7, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListAllHavingMaxPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("maxPrice", "1000")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(5, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListProductNameAndCode() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Pisang Keju")
                        .param("code", "001")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListProductNameAndMinPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Keju")
                        .param("minPrice", "4000")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(4, response.getResponsePaging().getCount());
                }
        );
    }
    @Test
    void getListProductNameAndMaxPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Pisang Keju")
                        .param("maxPrice", "1000")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(5, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListProductCodeAndMinPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("code", "0018")
                        .param("minPrice", "4000")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }
    @Test
    void getListProductCodeAndMaxPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("code", "003")
                        .param("minPrice", "1000")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListMinPriceAndMaxPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("maxPrice", "4000")
                        .param("minPrice", "4000")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(4, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListProductNameAndCodeAndMinPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Coklat Panas")
                        .param("minPrice", "2000")
                        .param("code", "005")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }
    @Test
    void getListProductNameAndCodeAndMaxPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Coklat Panas")
                        .param("maxPrice", "2000")
                        .param("code", "005")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListProductNameAndMinPriceAndMaxPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("name", "Coklat Panas")
                        .param("minPrice", "2000")
                        .param("maxPrice", "2000")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(8, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListProductCodeAndMinPriceAndMaxPrice() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("maxPrice", "2000")
                        .param("minPrice", "2000")
                        .param("code", "005")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListProductCodeAndMinPriceAndMaxPriceAndProductName() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("maxPrice", "2000")
                        .param("minPrice", "2000")
                        .param("code", "005")
                        .param("name", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListByBranchFailedTest() throws Exception {
        mockMvc.perform(
                get("/api/products/id_branch")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<GenericResponse<List<ProductResponse>>>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void getListByBranchSuccessTest() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);
        List<ProductPrice> priceList = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            ProductPrice price = new ProductPrice();
            price.setPrice(1000L * i);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
            priceList.add(price);
        }

        for (int i = 0; i < 20; i++) {
            Products products = new Products();

            products.setBranch(branch);
            if(i <= 4)
            {
                products.setName("Pisang Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(0));
            } else if (4 < i && i <= 12) {
                products.setName("Coklat Panas");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(1));
            } else if ( 12 < i && i < 16) {
                products.setName("Jus");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(2));
            }else {
                products.setName("Keju");
                products.setCode("00" + i);
                products.setId(UUID.randomUUID().toString());
                products.setProductPrice(priceList.get(3));
            }
            productRepository.save(products);
        }

        mockMvc.perform(
                get("/api/products/" + branch.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<ProductResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertEquals(10, response.getResponsePaging().getSize());
                }
        );
    }

    @Test
    void updateFailedTestBlankReq() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);

        ProductPrice price = new ProductPrice();
        price.setPrice(1000L);
        price.setId(UUID.randomUUID().toString());
        priceRepository.save(price);

        Products products = new Products();

        products.setBranch(branch);
        products.setName("Pisang Keju");
        products.setCode("006");
        products.setId(UUID.randomUUID().toString());
        products.setProductPrice(price);
        productRepository.save(products);

        UpdateProductRequest updateProductRequest = new UpdateProductRequest();

        mockMvc.perform(
               put("/api/products")
                       .accept(MediaType.APPLICATION_JSON)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(updateProductRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void updateFailedProductNotExist() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);

        ProductPrice price = new ProductPrice();
        price.setPrice(1000L);
        price.setId(UUID.randomUUID().toString());
        priceRepository.save(price);

        Products products = new Products();

        products.setBranch(branch);
        products.setName("Pisang Keju");
        products.setCode("006");
        products.setId(UUID.randomUUID().toString());
        products.setProductPrice(price);
        productRepository.save(products);

        UpdateProductRequest updateProductRequest = new UpdateProductRequest();
        updateProductRequest.setCode("0023");
        updateProductRequest.setName("Racing");
        updateProductRequest.setId("sygdhvfjahsjhbfasgbf");
        updateProductRequest.setPrice(70000L);
        updateProductRequest.setBranchId(branch.getId());

        mockMvc.perform(
                put("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProductRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void updateSuccess() throws Exception {
        Branch branch = branchRepository.findById("10aff4d6-6ec0-479c-901f-6edb7460d9bc").orElse(null);

        ProductPrice price = new ProductPrice();
        price.setPrice(1000L);
        price.setId(UUID.randomUUID().toString());

        priceRepository.save(price);

        Products products = new Products();

        products.setBranch(branch);
        products.setName("Pisang Keju");
        products.setCode("006");
        products.setId(UUID.randomUUID().toString());
        products.setProductPrice(price);

        productRepository.save(products);

        UpdateProductRequest updateProductRequest = new UpdateProductRequest();
        updateProductRequest.setCode("0023");
        updateProductRequest.setName("Racing");
        updateProductRequest.setId(products.getId());
        updateProductRequest.setPrice(8000L);
        updateProductRequest.setBranchId(branch.getId());

        mockMvc.perform(
                put("/api/products")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProductRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<ProductResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(response.getData().getCode(), updateProductRequest.getCode());
                    Assertions.assertEquals(response.getData().getName(), updateProductRequest.getName());
                    Assertions.assertEquals(response.getData().getPrice(), updateProductRequest.getPrice());
                    Assertions.assertEquals(response.getData().getBranch().getId(), updateProductRequest.getBranchId());
                }
        );
    }

    @Test
    void deleteFailedProductNotFound() throws Exception {
        mockMvc.perform(
                delete("/api/products/id_product")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<String> stringGenericResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<GenericResponse<String>>() {
                            });
                    Assertions.assertNotNull(stringGenericResponse.getMessage());
                    log.info(stringGenericResponse.getMessage());
                }
        );
    }

    @Test
    void deleteSuccess() throws Exception {
        Branch branch = branchRepository.findById("40330af6-caf0-4b62-9e9a-6a6f96127f3e").orElse(null);

        ProductPrice price = new ProductPrice();
        price.setPrice(1000L);
        price.setId(UUID.randomUUID().toString());
        priceRepository.save(price);

        Products products = new Products();

        products.setBranch(branch);
        products.setName("Pisang Keju");
        products.setCode("006");
        products.setId(UUID.randomUUID().toString());
        products.setProductPrice(price);
        productRepository.save(products);

        mockMvc.perform(
                delete("/api/products/" + products.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<String> stringGenericResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(stringGenericResponse.getMessage(), "There is an error");
                    Assertions.assertEquals("OK", stringGenericResponse.getData());
                }
        );
    }
}

