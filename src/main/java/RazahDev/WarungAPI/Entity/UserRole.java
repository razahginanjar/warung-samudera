package RazahDev.WarungAPI.Entity;

import RazahDev.WarungAPI.Constant.ConstantTable;
import RazahDev.WarungAPI.Constant.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = ConstantTable.USER_ROLE)
@Builder
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
}
