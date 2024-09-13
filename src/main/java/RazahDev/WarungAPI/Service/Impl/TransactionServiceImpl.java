package RazahDev.WarungAPI.Service.Impl;

import RazahDev.WarungAPI.Entity.*;
import RazahDev.WarungAPI.DTO.Product.ProductResponse;
import RazahDev.WarungAPI.DTO.Transaction.BillDetailResponse;
import RazahDev.WarungAPI.DTO.Transaction.GetTotalSalesResponse;
import RazahDev.WarungAPI.DTO.Transaction.TransactionRequest;
import RazahDev.WarungAPI.DTO.Transaction.TransactionResponse;
import RazahDev.WarungAPI.Repository.TransactionRepository;
import RazahDev.WarungAPI.Service.TransactionService;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final ValidationService validator;

    private final ProductServiceImpl productServiceImpl;

    private final TransactionRepository transactionRepository;

    private final SequenceGeneratorServiceImpl sequenceGeneratorServiceImpl;

    private final DetailTransactionServiceImpl detailTransactionServiceImpl;

    private final UserServiceImpl userServiceImpl;

    private final CustomerServiceImpl customerServiceImpl;

    @Transactional
    public TransactionResponse create(TransactionRequest request)
    {
        validator.validate(request);
        UserAccount byContext = userServiceImpl.getByContext();
        Customer customerID = byContext.getCustomerID();

        Transaction transaction = new Transaction();
        transaction.setTransactionType(request.getTransactionType());
        transaction.setBillId(UUID.randomUUID().toString());
        transaction.setReceiptNumber(sequenceGeneratorServiceImpl.getReceiptNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setCustomer(customerID);
        transactionRepository.save(transaction);

        String BranchCode = "";

        List<BillDetailResponse> billDetailResponsesList = new ArrayList<>();
        List<String> idBranchList = new ArrayList<>();

        for (var billdeatil : request.getRequestList())
        {
            DetailTransactions detailTransactions = detailTransactionServiceImpl.create(billdeatil, transaction);

            ProductResponse response = productServiceImpl.ProductToProductResponse(detailTransactions.getProductsDetail());

            BillDetailResponse billDetailResponse = new BillDetailResponse();
            billDetailResponse.setBillDetailId(detailTransactions.getBillDetailId());
            billDetailResponse.setQuantity(detailTransactions.getQuantity());
            billDetailResponse.setProducts(response);
            billDetailResponse.setBillId(transaction.getBillId());
            billDetailResponse.setTotalSales(detailTransactions.getTotalSales());

            billDetailResponsesList.add(billDetailResponse);
            idBranchList.add(response.getBranch().getId());
            BranchCode = response.getBranch().getCode();
        }

        transactionRepository.flush();
        String idBranch = idBranchList.getFirst();
        for (var id : idBranchList)
        {
            if (!id.equals(idBranch))
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product got from different Branch");
            }
        }

        return TransactionResponse.builder()
                .transactionType(transaction.getTransactionType().toString())
                .receiptNumber(BranchCode+ "-" +
                        transaction.getTransactionDate().getYear() + "-" + transaction.getReceiptNumber())
                .billId(transaction.getBillId())
                .billDetailResponseList(billDetailResponsesList)
                .transDate(transaction.getTransactionDate())
                .build();
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(String idBill)
    {
        Transaction transactions = transactionRepository.findById(idBill).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bill is not found")
        );

        return TransactionsToResponseTransaction(transactions);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getList(Integer size, Integer page,
                                             String receiptNumber, String startDate, String endDate,
                                             String transType, String productName)
    {
        Specification<DetailTransactions> transactionSpecification = specificationTransaction(receiptNumber, startDate,
                endDate, transType, productName);
        Pageable pageable = PageRequest.of(page, size);

        Page<DetailTransactions> detailTransactionsPage = detailTransactionServiceImpl.findAll(transactionSpecification, pageable);

        List<TransactionResponse> transactionResponseList = DetailTransactionToTransaction(detailTransactionsPage.getContent());

        return new PageImpl<>(transactionResponseList, pageable, transactionResponseList.size());
    }

    @Transactional(readOnly = true)
    public GetTotalSalesResponse getTotalSales(String startDate, String endDate)
    {
        Specification<Transaction> transactionSpecification = specificationSales(startDate, endDate);

        List<Transaction> transactionList = transactionRepository.findAll(transactionSpecification);

        long salesOnline = 0L;
        long salesTakeAway = 0L;
        long salesEatIn = 0L;
        for (var transaction : transactionList)
        {
            if(transaction.getTransactionType() == Transaction.TransactionType.ONLINE)
            {
                for (var detail: transaction.getDetailTransactionsList())
                {
                    salesOnline += (detail.getTotalSales() * detail.getQuantity());
                }
            } else if (transaction.getTransactionType() == Transaction.TransactionType.EAT_IN) {
                for (var detail: transaction.getDetailTransactionsList())
                {
                    salesEatIn += (detail.getTotalSales() * detail.getQuantity());
                }
            }else {
                for (var detail: transaction.getDetailTransactionsList())
                {
                    salesTakeAway += (detail.getTotalSales() * detail.getQuantity());
                }
            }
        }

        return GetTotalSalesResponse.builder()
                .eatIn(salesEatIn)
                .Online(salesOnline)
                .takeAway(salesTakeAway)
                .build();
    }

    public TransactionResponse TransactionsToResponseTransaction(Transaction transactions)
    {
        List<BillDetailResponse> billDetailResponsesList = new ArrayList<>();
        List<String> idBranchList = new ArrayList<>();

        String BranchCode = "";

        for (var transaction: transactions.getDetailTransactionsList())
        {

            BillDetailResponse billDetailResponse = new BillDetailResponse();
            billDetailResponse.setBillDetailId(transaction.getBillDetailId());
            billDetailResponse.setQuantity(transaction.getQuantity());

            ProductResponse response = productServiceImpl.ProductToProductResponse(transaction.getProductsDetail());

            billDetailResponse.setProducts(response);
            billDetailResponse.setBillId(transactions.getBillId());
            billDetailResponse.setTotalSales(transaction.getTotalSales());

            billDetailResponsesList.add(billDetailResponse);
            idBranchList.add(transaction.getProductsDetail().getBranch().getId());
            BranchCode =transaction.getProductsDetail().getBranch().getCode();
        }

        String id = idBranchList.getFirst();

        return TransactionResponse.builder()
                .customerName(transactions.getCustomer().getId())
                .billDetailResponseList(billDetailResponsesList)
                .billId(transactions.getBillId())
                .receiptNumber(BranchCode + "-" +
                        transactions.getTransactionDate().getYear() + "-" + transactions.getReceiptNumber())
                .transactionType(transactions.getTransactionType().toString())
                .transDate(transactions.getTransactionDate())
                .build();
    }

    public List<TransactionResponse> DetailTransactionToTransaction(List<DetailTransactions> detailTransactionsList)
    {
        List<String> idTransactions= new ArrayList<>();

        for (var detailList : detailTransactionsList)
        {
            if(!idTransactions.isEmpty())
            {
                boolean existInList =
                        idTransactions.stream().anyMatch(detailList.getTransactionDetailJoins().getBillId()::equals);
                if(!existInList)
                {
                    idTransactions.add(detailList.getTransactionDetailJoins().getBillId());
                }
            }else {
                idTransactions.add(detailList.getTransactionDetailJoins().getBillId());
            }
        }

        List<Transaction> transactionList = new ArrayList<>();

        for (var id : idTransactions)
        {
            Transaction transaction = transactionRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction Not Found")
            );
            transactionList.add(transaction);
        }

        return transactionList.stream().map(
                this::TransactionsToResponseTransaction
        ).toList();
    }

    public Specification<DetailTransactions> specificationTransaction(String receiptNumber, String startDate, String endDate,
                                                                      String transType, String productName)
    {
        return (root, query, criteriaBuilder) ->
        {
            Join<DetailTransactions, Transaction> detailTransactionsTransactionJoin = root.join("transactionDetailJoins");
            Join<DetailTransactions, Products> detailTransactionsProductsJoin = root.join("productsDetail");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");


            List<Predicate> predicates = new ArrayList<>();
            if(Objects.nonNull(receiptNumber) && Objects.nonNull(startDate) && Objects.nonNull(endDate) && Objects.nonNull(transType)
            && Objects.nonNull(productName))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }

            //4-condition
            else if(Objects.nonNull(startDate) && Objects.nonNull(endDate) && Objects.nonNull(transType)
                    && Objects.nonNull(productName))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }
            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(endDate) && Objects.nonNull(transType)
                    && Objects.nonNull(productName))
            {
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }
            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(startDate) && Objects.nonNull(transType)
                    && Objects.nonNull(productName))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }
            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(startDate) && Objects.nonNull(endDate)
                    && Objects.nonNull(productName))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName)
                ));
            }
            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(startDate) && Objects.nonNull(endDate) && Objects.nonNull(transType))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }

            //3-condition
            else if(Objects.nonNull(endDate) && Objects.nonNull(transType)
                    && Objects.nonNull(productName))
            {
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }
            else if(Objects.nonNull(startDate) && Objects.nonNull(transType)
                    && Objects.nonNull(productName))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }
            else if(Objects.nonNull(startDate) && Objects.nonNull(endDate)
                    && Objects.nonNull(productName))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName)
                ));
            }
            else if(Objects.nonNull(startDate) && Objects.nonNull(endDate) && Objects.nonNull(transType))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }

            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(transType)
                    && Objects.nonNull(productName))
            {
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }
            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(endDate)
                    && Objects.nonNull(productName))
            {
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName)
                ));
            }
            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(endDate) && Objects.nonNull(transType))
            {
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }

            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(startDate)
                    && Objects.nonNull(productName))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName)
                ));
            }
            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(startDate) && Objects.nonNull(transType))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }

            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(startDate) && Objects.nonNull(endDate))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal)
                ));
            }

            //2 condition
            else if(Objects.nonNull(endDate) && Objects.nonNull(transType))
            {
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }
            else if(Objects.nonNull(transType) && Objects.nonNull(productName))
            {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }
            else if(Objects.nonNull(endDate) && Objects.nonNull(productName))
            {
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName)
                ));
            }

            else if(Objects.nonNull(startDate) && Objects.nonNull(transType))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }
            else if(Objects.nonNull(startDate) && Objects.nonNull(productName))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName)
                ));
            }

            else if(Objects.nonNull(startDate) && Objects.nonNull(endDate))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal)
                ));
            }

            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(productName))
            {
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName)
                ));
            }
            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(transType))
            {
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                ));
            }

            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(endDate))
            {
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal)
                ));
            }

            else if(Objects.nonNull(receiptNumber) && Objects.nonNull(startDate))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                String[] split = receiptNumber.split("-");

                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2]),
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal)
                ));
            }

            else if(Objects.nonNull(receiptNumber))
            {
                String[] split = receiptNumber.split("-");

                predicates.add(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("receiptNumber"), split[2])
                );
            }
            else if(Objects.nonNull(startDate))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), startDateLocal)
                );
            }
            else if(Objects.nonNull(endDate))
            {
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(detailTransactionsTransactionJoin.get("transactionDate"), endDateLocal)
                );
            }
            else if(Objects.nonNull(transType))
            {
                predicates.add(
                        criteriaBuilder.equal(detailTransactionsTransactionJoin.get("transactionType"), transType)
                );
            }
             else if(Objects.nonNull(productName))
            {
                predicates.add(
                        criteriaBuilder.equal(detailTransactionsProductsJoin.get("name"), productName)
                );
            }
            assert query != null;
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }

    public Specification<Transaction> specificationSales(String startDate, String endDate)
    {
        return (root, query, criteriaBuilder) ->
        {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            List<Predicate> predicates = new ArrayList<>();
            if(Objects.nonNull(startDate) && Objects.nonNull(endDate))
            {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);

                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                predicates.add(
                        criteriaBuilder.and(
                                criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endDateLocal),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDateLocal)
                        )
                );
            } else if (Objects.nonNull(startDate)) {
                LocalDateTime startDateLocal = LocalDateTime.parse(startDate + " 00:00", dateTimeFormatter);
                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDateLocal)
                );
            } else if (Objects.nonNull(endDate)) {
                LocalDateTime endDateLocal = LocalDateTime.parse(endDate + " 00:00", dateTimeFormatter);
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endDateLocal)
                );
            }
            assert query != null;
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllPerCustomer(String idCustomer) {
        UserAccount byContext = userServiceImpl.getByContext();
        Customer byID = customerServiceImpl.getByID(idCustomer);
        if(byID.getUserAccount().getId().equals(byContext.getId()))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden The customer is in different account");
        }
        List<Transaction> allByCustomer = transactionRepository.findAllByCustomer(byID);
        return allByCustomer.stream().map(
                this::TransactionsToResponseTransaction
        ).toList();
    }
}
