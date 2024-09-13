package RazahDev.WarungAPI.DTO.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class CustomerResponse implements Serializable {
    private String id;
    private String name;
    private String mobilePhoneNo;
    private Boolean status;
    private String userAccountId;

}