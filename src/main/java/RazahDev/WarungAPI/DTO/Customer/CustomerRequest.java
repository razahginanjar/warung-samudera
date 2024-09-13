package RazahDev.WarungAPI.DTO.Customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CustomerRequest implements Serializable {
    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot empty")
    @NotBlank(message = "cannot be blank")
    private String name;

    @NotNull(message = "cannot be null")
    @NotEmpty(message = "cannot be empty")
    @NotBlank(message = "cannot blank")
    @Pattern(regexp = "^08\\d{9,11}$", message = "Nomor telepon hasus valid dan diawali dengan '08' diikuti oleh " +
            "9 hingga 11 angka.")
    private String mobilePhoneNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "Format tanggal harus 'yyyy-MM-dd'")
    @NotNull(message = "cannot be null")
    private String birthDate;
}