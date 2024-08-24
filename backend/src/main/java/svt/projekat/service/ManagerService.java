package svt.projekat.service;

import svt.projekat.model.dto.AssignManagerDTO;

import java.time.LocalDate;

public interface ManagerService {
    boolean assignManagerToFacility(AssignManagerDTO assignManagerDTO);
    boolean removeManagerFromFacility(Long facilityId);

}
