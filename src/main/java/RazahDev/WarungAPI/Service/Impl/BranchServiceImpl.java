package RazahDev.WarungAPI.Service.Impl;

import RazahDev.WarungAPI.Entity.Branch;
import RazahDev.WarungAPI.DTO.Branch.BranchRequest;
import RazahDev.WarungAPI.DTO.Branch.BranchResponse;
import RazahDev.WarungAPI.Repository.BranchRepository;
import RazahDev.WarungAPI.Service.BranchService;
import RazahDev.WarungAPI.Util.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    private final ValidationService validator;

    @Transactional(rollbackFor = Exception.class)
    public BranchResponse create(BranchRequest request)
    {
        validator.validate(request);
        Branch branch = new Branch();
        branch.setAddress(request.getAddress());
        while(true)
        {
            branch.setId(UUID.randomUUID().toString());
            if(!branchRepository.existsById(branch.getId()))
            {
                break;
            }
        }
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setPhone(request.getPhone());

        branchRepository.save(branch);

        return BranchResponse.builder()
                .id(branch.getId())
                .address(branch.getAddress())
                .code(branch.getCode())
                .name(branch.getName())
                .phone(branch.getPhone())
                .build();
    }

    @Transactional(readOnly = true)
    public BranchResponse get(String idbranch)
    {
        Branch branch = branchRepository.findById(idbranch).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "branch is not found")
        );
        return BranchResponse.builder()
                .phone(branch.getPhone())
                .id(branch.getId())
                .name(branch.getName())
                .code(branch.getCode())
                .address(branch.getAddress())
                .build();
    }
    @Transactional(readOnly = true)
    public Branch getById(String idBranch)
    {
        return branchRepository.findById(idBranch).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "branch is not found")
        );
    }
    @Transactional(readOnly = true)
    public Boolean checkBranch(String idBranch)
    {
        return branchRepository.existsById(idBranch);
    }

    @Transactional(rollbackFor = Exception.class)
    public BranchResponse update(BranchRequest request, String idBranch)
    {
        validator.validate(request);
        Branch branch = branchRepository.findById(idBranch).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "branch is not found")
        );

        branch.setAddress(request.getAddress());
        branch.setCode(request.getCode());
        branch.setName(request.getName());
        branch.setPhone(request.getPhone());

        branchRepository.save(branch);

        return BranchResponse.builder()
                .id(branch.getId())
                .address(branch.getAddress())
                .code(branch.getCode())
                .name(branch.getName())
                .phone(branch.getPhone())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String idbranch)
    {
        Branch branch = branchRepository.findById(idbranch).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "branch is not found")
        );
        branchRepository.delete(branch);
    }

    public BranchResponse toBranchResponse(Branch branch)
    {
        return BranchResponse.builder()
                .address(branch.getAddress())
                .name(branch.getName())
                .code(branch.getCode())
                .id(branch.getId())
                .phone(branch.getPhone())
                .build();
    }
}
