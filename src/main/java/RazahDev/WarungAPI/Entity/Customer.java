package RazahDev.WarungAPI.Entity;



import RazahDev.WarungAPI.Constant.ConstantTable;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = ConstantTable.CUSTOMER)
@Builder
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "mobile_phone_no")
	private String mobilePhoneNo;

	@Column(name = "birth_date")
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd") // format date json
	private Date birthDate; // Date dari java.util

	@Column(name = "status")
	private Boolean status;

	@OneToMany(mappedBy = "customer")
	List<Transaction> transactionList;

	@OneToOne
	@JoinColumn(name = "user_account_id")
	private UserAccount userAccount;
}
