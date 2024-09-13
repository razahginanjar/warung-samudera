package RazahDev.WarungAPI.DTO.Customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {
    private String id;
    private String name;

    @Pattern(regexp = "^08\\d{9,11}$", message = "Tlp. Number Must Be Valid")
    private String mobilePhoneNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "Date Format Must Be : yyyy-mm-dd")
    private String birthDate;

}
