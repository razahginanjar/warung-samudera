package RazahDev.WarungAPI.Entity;

import RazahDev.WarungAPI.Constant.ConstantTable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = ConstantTable.USER_ACCOUNT)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "is_Enabled")
    private Boolean isEnabled;

    public Customer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Customer customerID) {
        this.customerID = customerID;
    }

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customerID;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<UserRole> userRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRole.stream().map(
                userRole1 ->  new SimpleGrantedAuthority(userRole1.getRole().name())
        ).toList();
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
