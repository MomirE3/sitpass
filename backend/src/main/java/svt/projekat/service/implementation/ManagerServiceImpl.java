package svt.projekat.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import svt.projekat.model.dto.AssignManagerDTO;
import svt.projekat.model.entity.Facility;
import svt.projekat.model.entity.Manages;
import svt.projekat.model.entity.User;
import svt.projekat.repository.FacilityRepository;
import svt.projekat.repository.ManagesRepository;
import svt.projekat.repository.UserRepository;
import svt.projekat.service.ManagerService;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private ManagesRepository managesRepository;

    @Override
    public boolean assignManagerToFacility(AssignManagerDTO assignManagerDTO) {
        Optional<User> userOpt = userRepository.findById(assignManagerDTO.getUserId());
        Optional<Facility> facilityOpt = facilityRepository.findById(assignManagerDTO.getFacilityId());

        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (!facilityOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid facility ID.");
        }

        User user = userOpt.get();
        Facility facility = facilityOpt.get();

        if (assignManagerDTO.getEndDate().isBefore(assignManagerDTO.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        if (assignManagerDTO.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be before today's date.");
        }

        boolean hasManager = managesRepository.existsByFacilityIdAndStartDateBeforeAndEndDateAfter(
                assignManagerDTO.getFacilityId(),
                assignManagerDTO.getEndDate(),
                assignManagerDTO.getStartDate()
        );

        if (hasManager) {
            throw new IllegalArgumentException("This facility already has a manager during the specified period.");
        }

        Manages manages = new Manages();
        manages.setUser(user);
        manages.setFacility(facility);
        manages.setStartDate(assignManagerDTO.getStartDate());
        manages.setEndDate(assignManagerDTO.getEndDate());
        facility.setActive(true);
        facilityRepository.save(facility);

        managesRepository.save(manages);
        return true;
    }

    @Override
    public boolean removeManagerFromFacility(Long facilityId) {
        Optional<Facility> facilityOpt = facilityRepository.findById(facilityId);

        if (!facilityOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid facility ID.");
        }

        Facility facility = facilityOpt.get();
        Optional<Manages> managesOpt = managesRepository.findByFacilityId(facilityId);

        if (!managesOpt.isPresent()) {
            throw new IllegalArgumentException("No manager assigned to this facility.");
        }

        managesRepository.delete(managesOpt.get());
        facility.setActive(false);
        facilityRepository.save(facility);

        return true;
    }
}
