package RazahDev.WarungAPI.Entity;

import RazahDev.WarungAPI.Constant.ConstantTable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = ConstantTable.TRANSACTION)
public class Transaction {
    public enum TransactionType{
        EAT_IN,
        ONLINE,
        TAKE_AWAY
    }

    @Id
    @Column(name = "bill_id")
    private String billId;

    @Column(name = "receipt_number", unique = true, nullable = false)
    private Integer receiptNumber;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @OneToMany(mappedBy = "transactionDetailJoins")
    private List<DetailTransactions> detailTransactionsList;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
