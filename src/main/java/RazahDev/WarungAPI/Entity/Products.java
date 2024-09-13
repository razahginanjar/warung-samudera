package RazahDev.WarungAPI.Entity;

import RazahDev.WarungAPI.Constant.ConstantTable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = ConstantTable.PRODUCT)
public class Products {
    @Id
    private String id;

    private String code;

    private String name;

    @ManyToOne
    @JoinColumn(name = "product_price_id", referencedColumnName = "id")
    private ProductPrice productPrice;

    @ManyToOne
    @JoinColumn(name = "branch_id", referencedColumnName = "id")
    private Branch branch;

    @OneToMany(mappedBy = "productsDetail")
    private List<DetailTransactions> detailTransactionsList;
}
