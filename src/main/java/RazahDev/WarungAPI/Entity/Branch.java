package RazahDev.WarungAPI.Entity;

import RazahDev.WarungAPI.Constant.ConstantTable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = ConstantTable.BRANCH)
public class Branch {
    @Id
    private String id;

    private String code;

    private String name;

    private String address;

    private String phone;

    @OneToMany(mappedBy = "branch")
    private List<Products> productsList;
}
