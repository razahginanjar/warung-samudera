package RazahDev.WarungAPI.Entity;

import RazahDev.WarungAPI.Constant.ConstantTable;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ConstantTable.TRANSACTION_DETAIL)
public class DetailTransactions {
    @Id
    @Column(name = "bill_detail_id")
    private String billDetailId;

    @Column(name = "total_sales")
    private Long totalSales;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Products productsDetail;

    @ManyToOne
    @JoinColumn(name = "transaction_bill_id", referencedColumnName = "bill_id")
    private Transaction transactionDetailJoins;

}
