package RazahDev.WarungAPI.Controller;

import RazahDev.WarungAPI.Constant.APIUrl;
import RazahDev.WarungAPI.DTO.Branch.BranchRequest;
import RazahDev.WarungAPI.DTO.Branch.BranchResponse;
import RazahDev.WarungAPI.DTO.GenericResponse;
import RazahDev.WarungAPI.Service.Impl.BranchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RequiredArgsConstructor
@RestController
@RequestMapping(path = APIUrl.BRANCH_API)
public class BranchController {

    private final BranchServiceImpl branchServiceImpl;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<BranchResponse>> create(@RequestBody BranchRequest request)
    {
        BranchResponse branchResponse = branchServiceImpl.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                GenericResponse.<BranchResponse>builder()
                        .message("Success")
                        .status(HttpStatus.CREATED.value())
                        .data(branchResponse)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @GetMapping(
            path = "/{id_branch}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<BranchResponse>> get(@PathVariable(name = "id_branch")String idbranch)
    {
        BranchResponse branchResponse = branchServiceImpl.get(idbranch);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<BranchResponse>builder()
                        .message("Success")
                        .status(HttpStatus.OK.value())
                        .data(branchResponse)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @PutMapping(
            path = "{id_branch}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<BranchResponse>> update(@RequestBody BranchRequest request,
                                                  @PathVariable(name = "id_branch") String idBranch)
    {
        BranchResponse update = branchServiceImpl.update(request, idBranch);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<BranchResponse>builder()
                        .message("Success")
                        .status(HttpStatus.OK.value())
                        .data(update)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @DeleteMapping(
            path = "{id_branch}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GenericResponse<String>> delete(@PathVariable(name = "id_branch")String idbranch)
    {
        branchServiceImpl.delete(idbranch);
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.<String>builder()
                        .message("Success")
                        .status(HttpStatus.OK.value())
                        .data("ok")
                        .build()
        );
    }
}
