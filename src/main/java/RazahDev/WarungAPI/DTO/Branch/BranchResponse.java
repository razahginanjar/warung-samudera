package RazahDev.WarungAPI.DTO.Branch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchResponse {
    private String id;

    private String code;

    private String name;

    private String address;

    private String phone;
}
