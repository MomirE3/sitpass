package svt.projekat.service.implementation;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import svt.projekat.model.dto.AccountRequestDTO;
import svt.projekat.model.entity.AccountRequest;
import svt.projekat.model.entity.RequestStatus;
import svt.projekat.model.entity.User;
import svt.projekat.model.responseDTO.ApiResponseDTO;
import svt.projekat.repository.AccountRequestRepository;
import svt.projekat.repository.UserRepository;
import svt.projekat.service.AccountRequestService;
import svt.projekat.service.EmailService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountRequestServiceImpl implements AccountRequestService {

    @Autowired
    private AccountRequestRepository accountRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ApiResponseDTO createRequest(AccountRequestDTO accountRequestDTO) throws Exception {
        try {
            Optional<AccountRequest> existingRequest = accountRequestRepository.findByEmail(accountRequestDTO.getEmail());

            if (existingRequest.isPresent()) {
                RequestStatus status = existingRequest.get().getStatus();
                if (status == RequestStatus.PENDING) {
                    return new ApiResponseDTO(false, "A request for this account email is already pending.");
                } else if (status == RequestStatus.ACCEPTED) {
                    return new ApiResponseDTO(false, "An account with this email already exists.");
                }
            }

            AccountRequest accountRequest = new AccountRequest();
            if (Objects.equals(accountRequestDTO.getEmail(), "")) {
                return new ApiResponseDTO(false, "You must write an email for your request.");
            }
            boolean valid = isValidEmailAddress(accountRequestDTO.getEmail());
            if(!valid){
                return new ApiResponseDTO(false, "You must write valid email address. ");
            }
            accountRequest.setEmail(accountRequestDTO.getEmail());
            if (Objects.equals(accountRequestDTO.getAddress(), "")) {
                return new ApiResponseDTO(false, "You must write an address for your request.");
            }
            accountRequest.setAddress(accountRequestDTO.getAddress());
            accountRequest.setStatus(RequestStatus.PENDING);
            accountRequest.setCreatedAt(LocalDate.now());
            accountRequest.setRejectionReason("");
            accountRequest.setPassword("");

            String generatedPassword = UUID.randomUUID().toString();
            accountRequest.setPassword(passwordEncoder.encode(generatedPassword));

            accountRequestRepository.save(accountRequest);
            return new ApiResponseDTO(true, "You have successfully sent a request for creating an account.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to create account request: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AccountRequest> getAllRequests() {
        List<AccountRequest> requests = accountRequestRepository.findAll();
        return requests.stream().map(request -> {
            AccountRequest accountRequest = new AccountRequest();
            accountRequest.setId(request.getId());
            accountRequest.setEmail(request.getEmail());
            accountRequest.setAddress(request.getAddress());
            accountRequest.setStatus(request.getStatus());
            accountRequest.setRejectionReason(request.getRejectionReason());
            return accountRequest;
        }).collect(Collectors.toList());
    }

    @Override
    public ApiResponseDTO rejectRequest(Long id, String rejectionReason) throws Exception {
        try {
            Optional<AccountRequest> existingRequest = accountRequestRepository.findById(id);

            if (existingRequest.isPresent()) {
                AccountRequest accountRequest = existingRequest.get();
                if (accountRequest.getStatus() == RequestStatus.PENDING) {
                    accountRequest.setStatus(RequestStatus.REJECTED);
                    accountRequest.setRejectionReason(rejectionReason != null ? rejectionReason : "");
                    accountRequestRepository.save(accountRequest);
                    return new ApiResponseDTO(true, "The account request has been rejected.");
                } else {
                    return new ApiResponseDTO(false, "Only pending requests can be rejected.");
                }
            } else {
                return new ApiResponseDTO(false, "Account request not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to reject account request: " + e.getMessage(), e);
        }
    }

    @Override
    public ApiResponseDTO acceptRequest(Long id) throws Exception {
        try {
            Optional<AccountRequest> existingRequest = accountRequestRepository.findById(id);

            if (existingRequest.isPresent()) {
                AccountRequest accountRequest = existingRequest.get();
                if (accountRequest.getStatus() == RequestStatus.PENDING) {
                    User user = new User();
                    user.setEmail(accountRequest.getEmail());
                    String generatedPassword = UUID.randomUUID().toString();
                    user.setPassword(passwordEncoder.encode(generatedPassword));
                    user.setAddress(accountRequest.getAddress());
                    user.setName("");
                    user.setSurname("");
                    user.setCreatedAt(LocalDate.now());
                    user.setPhoneNumber("");

                    // Use a default date or a specific date if required
                    LocalDate defaultBirthday = LocalDate.of(1970, 1, 1); // Default birthday
                    user.setBirthday(defaultBirthday);

                    user.setCity("");
                    user.setZipCode("");

                    userRepository.save(user);

                    accountRequest.setStatus(RequestStatus.ACCEPTED);
                    accountRequestRepository.save(accountRequest);

                    emailService.sendSimpleMessage(
                            accountRequest.getEmail(),
                            "Your account request has been accepted, welcome to the Fitpass!",
                            "Your password is: " + generatedPassword
                    );

                    return new ApiResponseDTO(true, "The account request has been accepted and a user has been created.");
                } else {
                    return new ApiResponseDTO(false, "Only pending requests can be accepted.");
                }
            } else {
                return new ApiResponseDTO(false, "Account request not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to accept account request: " + e.getMessage(), e);
        }
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}