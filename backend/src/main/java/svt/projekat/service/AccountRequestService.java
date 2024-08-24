package svt.projekat.service;

import svt.projekat.model.dto.AccountRequestDTO;
import svt.projekat.model.entity.AccountRequest;
import svt.projekat.model.responseDTO.ApiResponseDTO;

import java.util.List;

public interface AccountRequestService {
    ApiResponseDTO createRequest(AccountRequestDTO accountRequestDTO) throws Exception;
    List<AccountRequest> getAllRequests();
    ApiResponseDTO rejectRequest(Long id, String rejectionReason) throws Exception;
    ApiResponseDTO acceptRequest(Long id) throws Exception;


}
