package svt.projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import svt.projekat.model.dto.AccountRequestDTO;
import svt.projekat.model.dto.RejectionReasonDTO;
import svt.projekat.model.entity.AccountRequest;
import svt.projekat.model.responseDTO.ApiResponseDTO;
import svt.projekat.service.AccountRequestService;

import java.util.List;

@RestController
@RequestMapping("api/requests")
public class AccountRequestController {
    @Autowired
    private AccountRequestService accountRequestService;

    @PostMapping(value = "/createRequest")
    public ResponseEntity<?> createAccountRequest(@RequestBody @Validated AccountRequestDTO accountRequestDTO) {
        try {
            ApiResponseDTO response = accountRequestService.createRequest(accountRequestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Unable to process account request: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<AccountRequest>> getAllRequests() {
        List<AccountRequest> requests = accountRequestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/rejectRequest/{id}")
    public ResponseEntity<?> rejectAccountRequest(@PathVariable Long id, @RequestBody(required = false) RejectionReasonDTO rejectionRequestDTO) {
        try {
            String rejectionReason = (rejectionRequestDTO != null) ? rejectionRequestDTO.getRejectionReason() : "";
            ApiResponseDTO response = accountRequestService.rejectRequest(id, rejectionReason);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else if (response.getMessage().equals("Account request not found.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (response.getMessage().equals("Only pending requests can be rejected.")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Unable to reject account request: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/acceptRequest/{id}")
    public ResponseEntity<?> acceptAccountRequest(@PathVariable Long id) {
        try {
            ApiResponseDTO response = accountRequestService.acceptRequest(id);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else if (response.getMessage().equals("Account request not found.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (response.getMessage().equals("Only pending requests can be accepted.")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Unable to accept account request: " + e.getMessage());
        }
    }

}
