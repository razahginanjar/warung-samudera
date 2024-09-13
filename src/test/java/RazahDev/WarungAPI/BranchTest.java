package RazahDev.WarungAPI;

import RazahDev.WarungAPI.Entity.Branch;
import RazahDev.WarungAPI.DTO.Branch.BranchRequest;
import RazahDev.WarungAPI.DTO.Branch.BranchResponse;
import RazahDev.WarungAPI.DTO.GenericResponse;
import RazahDev.WarungAPI.Repository.BranchRepository;
import RazahDev.WarungAPI.Service.Impl.BranchServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class BranchTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    BranchServiceImpl branchService;
    @BeforeEach
    void setUp()
    {
        branchRepository.deleteAll();
    }
    @Test
    void createTestFailedBlankData() throws Exception
    {
        BranchRequest request = new BranchRequest();

        mockMvc.perform(
                post("/api/branch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<BranchResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void createTestSuccess() throws Exception
    {
        BranchRequest request = new BranchRequest();
        request.setAddress("jln. cibulakan rt/rw 07/03");
        request.setCode("005");
        request.setPhone("+6282127");
        request.setName("Cabang 1");

        BranchResponse branchResponse = branchService.create(request);

//        mockMvc.perform(
//                post("/api/branch")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request))
//        ).andExpectAll(
//                status().isOk()
//        ).andDo(
//                result ->
//                {
//                    GenericResponse<BranchResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
//                            new TypeReference<>() {
//                            });
//                    Assert.isNull(response.getMessage(), "There is an Error");
//                    Assertions.assertEquals(response.getData().getAddress(), request.getAddress());
//                    Assertions.assertEquals(response.getData().getCode(),request.getCode());
//                    Assertions.assertEquals(response.getData().getName(), request.getName());
//                    Assertions.assertEquals(response.getData().getPhone(), request.getPhone());
//                }
//        );
    }

    @Test
    void getTestFailed() throws Exception {
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID().toString());
        branch.setAddress("cibulakan");
        branch.setCode("002");
        branch.setPhone("+6282");
        branch.setName("Cabang 1");
        branchRepository.save(branch);
        mockMvc.perform(
                get("/api/branch/id_branch")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<BranchResponse> branchResponseGenericResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(branchResponseGenericResponse.getMessage());
                    log.info(branchResponseGenericResponse.getMessage());
                }
        );
    }

    @Test
    void getTestSuccess() throws Exception {
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID().toString());
        branch.setAddress("cibulakan");
        branch.setCode("002");
        branch.setPhone("+6282");
        branch.setName("Cabang 1");
        branchRepository.save(branch);
        mockMvc.perform(
                get("/api/branch/" + branch.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<BranchResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals(response.getData().getAddress(), branch.getAddress());
                    Assertions.assertEquals(response.getData().getCode(), branch.getCode());
                    Assertions.assertEquals(response.getData().getPhone(), branch.getPhone());
                    Assertions.assertEquals(response.getData().getName(), branch.getName());
                }
        );
    }

    @Test
    void updateFailedWrongIdTest() throws Exception
    {
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID().toString());
        branch.setAddress("cibulakan");
        branch.setCode("002");
        branch.setPhone("+6282");
        branch.setName("Cabang 1");
        branchRepository.save(branch);

        BranchRequest request = new BranchRequest();
        request.setAddress("jln. cibulakan rt/rw 07/03");
        request.setCode("005");
        request.setPhone("+6282127");
        request.setName("Cabang 1");

        mockMvc.perform(
                put("/api/branch/id_branch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<BranchResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void updateFailedDataIsEmpty() throws Exception
    {
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID().toString());
        branch.setAddress("cibulakan");
        branch.setCode("002");
        branch.setPhone("+6282");
        branch.setName("Cabang 1");
        branchRepository.save(branch);

        BranchRequest request = new BranchRequest();

        mockMvc.perform(
                put("/api/branch/"+branch.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<BranchResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void updateSuccessTest() throws Exception
    {
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID().toString());
        branch.setAddress("cibulakan");
        branch.setCode("002");
        branch.setPhone("+6282");
        branch.setName("Cabang 1");
        branchRepository.save(branch);

        BranchRequest request = new BranchRequest();
        request.setAddress("jln. cibulakan rt/rw 07/03");
        request.setCode("005");
        request.setPhone("+6282127");
        request.setName("Cabang 1");

        mockMvc.perform(
                put("/api/branch/" + branch.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<BranchResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an Error");
                    Assertions.assertEquals(response.getData().getAddress(), request.getAddress());
                    Assertions.assertEquals(response.getData().getCode(),request.getCode());
                    Assertions.assertEquals(response.getData().getName(), request.getName());
                    Assertions.assertEquals(response.getData().getPhone(), request.getPhone());
                }
        );
    }

    @Test
    void deleteFailedTestIdIsNotFound() throws Exception {
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID().toString());
        branch.setAddress("cibulakan");
        branch.setCode("002");
        branch.setPhone("+6282");
        branch.setName("Cabang 1");
        branchRepository.save(branch);

        mockMvc.perform(
                delete("/api/branch/gsrestdg" )
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result ->
                {
                    GenericResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assertions.assertNotNull(response.getMessage());
                    log.info(response.getMessage());
                }
        );
    }

    @Test
    void deleteSuccessTest() throws Exception {
        Branch branch = new Branch();
        branch.setId(UUID.randomUUID().toString());
        branch.setAddress("cibulakan");
        branch.setCode("002");
        branch.setPhone("+6282");
        branch.setName("Cabang 1");
        branchRepository.save(branch);
        mockMvc.perform(
                delete("/api/branch/" + branch.getId())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result ->
                {
                    GenericResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                            new TypeReference<>() {
                            });
                    Assert.isNull(response.getMessage(), "There is an error");
                    Assertions.assertEquals("OK", response.getData());
                }
        );
    }

}
