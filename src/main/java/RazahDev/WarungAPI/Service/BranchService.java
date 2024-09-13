package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.Entity.Branch;
import RazahDev.WarungAPI.DTO.Branch.BranchRequest;
import RazahDev.WarungAPI.DTO.Branch.BranchResponse;

public interface BranchService {
    BranchResponse create(BranchRequest request);
    BranchResponse get(String idbranch);
    Branch getById(String idBranch);
    Boolean checkBranch(String idBranch);
    BranchResponse update(BranchRequest request, String idBranch);
    void delete(String idbranch);
    BranchResponse toBranchResponse(Branch branch);
}
