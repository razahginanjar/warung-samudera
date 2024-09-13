package RazahDev.WarungAPI;

import RazahDev.WarungAPI.Entity.*;
import RazahDev.WarungAPI.DTO.GenericResponse;
import RazahDev.WarungAPI.DTO.Transaction.BillDetailRequest;
import RazahDev.WarungAPI.DTO.Transaction.GetTotalSalesResponse;
import RazahDev.WarungAPI.DTO.Transaction.TransactionRequest;
import RazahDev.WarungAPI.DTO.Transaction.TransactionResponse;
import RazahDev.WarungAPI.Repository.*;
import RazahDev.WarungAPI.Service.Impl.SequenceGeneratorServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class TransactionTest {
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

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    DetailTransactionRepository detailTransactionRepository;

    @Autowired
    SequenceGeneratorRepository sequenceGeneratorRepository;

    @Autowired
    SequenceGeneratorServiceImpl sequenceGeneratorServiceImpl;

    @BeforeEach
    void setUp()
    {
        sequenceGeneratorRepository.deleteAll();
        detailTransactionRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    void createFailedBlankRequestTest() throws Exception {
        List<BillDetailRequest> billDetailRequestList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            BillDetailRequest billDetailRequest = new BillDetailRequest();
            billDetailRequest.setQuantity(i);
            if( i == 1)
            {
                billDetailRequest.setProductId("0637ba4e-9527-4246-a966-5f0145ab9c58");
            } else if (i == 2) {
                billDetailRequest.setProductId("2244fa5e-cb70-4729-b503-5bc1b393a34e");
            } else {
                billDetailRequest.setProductId("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4");
            }
            billDetailRequestList.add(billDetailRequest);
        }

        TransactionRequest request = new TransactionRequest();

        mockMvc.perform(
                post("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<TransactionResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }
    @Test
    void createFailedProductNotFoundTest() throws Exception {
        List<BillDetailRequest> billDetailRequestList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            BillDetailRequest billDetailRequest = new BillDetailRequest();
            billDetailRequest.setQuantity(i);
            if( i == 1)
            {
                billDetailRequest.setProductId("hgsfhasgfasjf");
            } else if (i == 2) {
                billDetailRequest.setProductId("jsdbhfsbf");
            } else {
                billDetailRequest.setProductId("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4");
            }
            billDetailRequestList.add(billDetailRequest);
        }

        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(Transaction.TransactionType.EAT_IN);
        request.setRequestList(billDetailRequestList);


        mockMvc.perform(
                post("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<TransactionResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }
    @Test
    void createdFailedProductIsNotFromTheSameBranchTest() throws Exception {
        Branch branch = new Branch();
        branch.setName("Ciamis");
        branch.setId(UUID.randomUUID().toString());
        branch.setPhone("+263654");
        branch.setCode("009i");
        branch.setAddress("jlan cimais, gunung prabanan");
        branchRepository.save(branch);

        ProductPrice price = priceRepository.findFirstByPrice(2000L).orElse(null);
        Products products = new Products();
        products.setName("kwetiau");
        products.setId(UUID.randomUUID().toString());
        products.setBranch(branch);
        products.setProductPrice(price);
        products.setCode("997");

        productRepository.save(products);

        List<BillDetailRequest> billDetailRequestList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            BillDetailRequest billDetailRequest = new BillDetailRequest();
            billDetailRequest.setQuantity(i);
            if( i == 1)
            {
                billDetailRequest.setProductId("0637ba4e-9527-4246-a966-5f0145ab9c58");
            } else if (i == 2) {
                billDetailRequest.setProductId(products.getId());
            } else {
                billDetailRequest.setProductId("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4");
            }
            billDetailRequestList.add(billDetailRequest);
        }

        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(Transaction.TransactionType.EAT_IN);
        request.setRequestList(billDetailRequestList);


        mockMvc.perform(
                post("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<TransactionResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void createdSuccessTest() throws Exception {
        List<BillDetailRequest> billDetailRequestList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            BillDetailRequest billDetailRequest = new BillDetailRequest();
            billDetailRequest.setQuantity(i);
            if( i == 1)
            {
                billDetailRequest.setProductId("0637ba4e-9527-4246-a966-5f0145ab9c58");
            } else if (i == 2) {
                billDetailRequest.setProductId("2244fa5e-cb70-4729-b503-5bc1b393a34e");
            } else {
                billDetailRequest.setProductId("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4");
            }
            billDetailRequestList.add(billDetailRequest);
        }

        TransactionRequest request = new TransactionRequest();
        request.setTransactionType(Transaction.TransactionType.EAT_IN);
        request.setRequestList(billDetailRequestList);


        mockMvc.perform(
                post("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<TransactionResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");

                }
        );
    }


    @Test
    void getFailedBillIsNotFoundTest() throws Exception {
        mockMvc.perform(
                get("/api/transactions/id_bill")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<TransactionResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void getSuccessTest() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
        transaction.setBillId(UUID.randomUUID().toString());
        transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        for (int i = 1; i <= 3; i++) {
            DetailTransactions detailTransactions = new DetailTransactions();
            detailTransactions.setBillDetailId(UUID.randomUUID().toString());
            detailTransactions.setQuantity(i);

            Products products = new Products();
            if(i == 1)
            {
                products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                );
            } else if (i == 2) {
                products = productRepository.findById("28e9c7b4-5b3b-4fe8-88d4-cd2352169c5f").orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                );
            }else {
                products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                );
            }

            detailTransactions.setProductsDetail(products);
            detailTransactions.setTransactionDetailJoins(transaction);
            detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());
            detailTransactionRepository.save(detailTransactions);
        }

        mockMvc.perform(
                get("/api/transactions/" + transaction.getBillId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<TransactionResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                }
        );
    }

    @Test
    void getListAll() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("28e9c7b4-5b3b-4fe8-88d4-cd2352169c5f").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(4, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumber() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListStartDate() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate", "14-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(4, response.getResponsePaging().getCount());
                }
        );
    }
    @Test
    void getListEndDate() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("endDate", "14-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(0, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("transType", Transaction.TransactionType.EAT_IN.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListProductName() throws Exception {

        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("productName", "kwetiau")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(0, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("productName", "kwetiau")
                        .param("receiptNumber", "201-2024-4")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(0, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListEndDateAndTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("endDate", "20-08-2024")
                        .param("transType", Transaction.TransactionType.EAT_IN.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListTransTypeAndProductName() throws Exception {

        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("productName", "kwetiau")
                        .param("transType", Transaction.TransactionType.EAT_IN.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(0, response.getResponsePaging().getCount());
                }
        );
    }
    @Test
    void getListEndDateAndProductName() throws Exception {

        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("productName", "Coklat Panas")
                        .param("endDate", "22-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(4, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListStartDateAndTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate", "08-08-2024")
                        .param("transType", Transaction.TransactionType.EAT_IN.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }
    @Test
    void getListStartDateAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate", "08-08-2024")
                        .param("productName", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(4, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListStartDateAndEndDate() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate", "08-08-2024")
                        .param("endDate", "08-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(0, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndEndDate() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("endDate", "20-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndStartDate() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("startDate", "20-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(0, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndStartDateAndEndDate() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("startDate", "10-08-2024")
                        .param("endDate", "20-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndStartDateAndTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("startDate", "10-08-2024")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndStartDateAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("startDate", "10-08-2024")
                        .param("productName", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndEndDateAndTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("endDate", "20-08-2024")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndEndDateAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("endDate", "20-08-2024")
                        .param("productName", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndTransTypeAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
                        .param("productName", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListStartDateAndEndDateAndTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate", "01-08-2024")
                        .param("endDate", "20-08-2024")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(2, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListStartDateAndEndDateAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate", "01-08-2024")
                        .param("endDate", "20-08-2024")
                        .param("productName", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(4, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListStartDateAndProductNameAndTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate", "01-08-2024")
                        .param("productName", "Coklat Panas")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(2, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListEndDateAndProductNameAndTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("productName", "Coklat Panas")
                        .param("endDate", "20-08-2024")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(2, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListStartDateAndEndDateAndTransTypeAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate", "01-08-2024")
                        .param("endDate", "20-08-2024")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
                        .param("productName", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(2, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListReceiptNumberAndEndDateAndTransTypeAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("endDate", "20-08-2024")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
                        .param("productName", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }
    @Test
    void getListReceiptNumberAndStartDateAndTransTypeAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("startDate", "02-08-2024")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
                        .param("productName", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }
    @Test
    void getListReceiptNumberAndStartDateAndEndDateAndProductName() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("endDate", "20-08-2024")
                        .param("startDate", "02-08-2024")
                        .param("productName", "Coklat Panas")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }
    @Test
    void getListReceiptNumberAndStartDateAndEndDateAndTransType() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("endDate", "20-08-2024")
                        .param("startDate", "02-08-2024")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }

    @Test
    void getListStartDateAndEndDateAndTransTypeAndProductNameAndReceiptNumber() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }


        mockMvc.perform(
                get("/api/transactions")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("receiptNumber", "201-2024-4")
                        .param("endDate", "20-08-2024")
                        .param("startDate", "02-08-2024")
                        .param("productName", "Coklat Panas")
                        .param("transType", Transaction.TransactionType.TAKE_AWAY.name())
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<List<TransactionResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(1, response.getResponsePaging().getCount());
                }
        );
    }


    @Test
    void getTotalSalesAll() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }

        mockMvc.perform(
                get("/api/transactions/total-sales")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<GetTotalSalesResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    log.info(response.getData().takeAway().toString());
                    log.info(response.getData().eatIn().toString());
                    log.info(response.getData().Online().toString());
                }
        );

    }

    @Test
    void getTotalSalesAllStartDateCondition() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }

        mockMvc.perform(
                get("/api/transactions/total-sales")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("startDate", "02-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<GetTotalSalesResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    log.info(response.getData().takeAway().toString());
                    log.info(response.getData().eatIn().toString());
                    log.info(response.getData().Online().toString());
                }
        );
    }

    @Test
    void getTotalSalesAllEndDateCondition() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }

        mockMvc.perform(
                get("/api/transactions/total-sales")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("endDate", "22-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<GetTotalSalesResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    log.info(response.getData().takeAway().toString());
                    log.info(response.getData().eatIn().toString());
                    log.info(response.getData().Online().toString());
                }
        );
    }

    @Test
    void getTotalSalesAllStartDateAndEndDateCondition() throws Exception {
        for (int index = 0; index < 4; index++) {
            Transaction transaction = new Transaction();

            if(index == 1)
            {
                transaction.setTransactionType(Transaction.TransactionType.EAT_IN);
            } else if (index == 2) {
                transaction.setTransactionType(Transaction.TransactionType.ONLINE);
            }else {
                transaction.setTransactionType(Transaction.TransactionType.TAKE_AWAY);
            }

            transaction.setBillId(UUID.randomUUID().toString());
            transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            transactionRepository.save(transaction);

            for (int i = 1; i <= 3; i++) {
                DetailTransactions detailTransactions = new DetailTransactions();
                detailTransactions.setBillDetailId(UUID.randomUUID().toString());
                detailTransactions.setQuantity(i);

                Products products = new Products();
                if(i == 1)
                {
                    products = productRepository.findById("2244fa5e-cb70-4729-b503-5bc1b393a34e").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                } else if (i == 2) {
                    products = productRepository.findById("0637ba4e-9527-4246-a966-5f0145ab9c58").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }else {
                    products = productRepository.findById("2fa2ea3d-abf5-4658-b7ce-d956c1a98bc4").orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found")
                    );
                }

                detailTransactions.setProductsDetail(products);
                detailTransactions.setTransactionDetailJoins(transaction);
                detailTransactions.setTotalSales(i * products.getProductPrice().getPrice());

                detailTransactionRepository.save(detailTransactions);
            }
        }

        mockMvc.perform(
                get("/api/transactions/total-sales")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("endDate", "22-08-2024")
                        .param("startDate", "08-08-2024")
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<GetTotalSalesResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    log.info(response.getData().takeAway().toString());
                    log.info(response.getData().eatIn().toString());
                    log.info(response.getData().Online().toString());
                }
        );
    }

}
