package svt.projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import svt.projekat.model.dto.AssignManagerDTO;
import svt.projekat.model.entity.Manages;
import svt.projekat.model.entity.User;
import svt.projekat.repository.ManagesRepository;
import svt.projekat.service.ManagerService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/manager")
public class ManagesController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private ManagesRepository managesRepository;

    @GetMapping("/manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getManagerByFacilityId(@RequestParam Long facilityId) {
        Optional<Manages> managesOpt = managesRepository.findByFacilityId(facilityId);
        if (managesOpt.isPresent()) {
            User manager = managesOpt.get().getUser();
            return ResponseEntity.ok(Map.of("name", manager.getName(), "surname", manager.getSurname()));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "No manager assigned to this facility."));
        }
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignManager(@RequestBody AssignManagerDTO assignManagerDTO) {
        try {
            boolean success = managerService.assignManagerToFacility(assignManagerDTO);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Manager assigned successfully."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid user ID or facility ID."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removeManager(@RequestParam Long facilityId) {
        try {
            boolean success = managerService.removeManagerFromFacility(facilityId);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Manager removed successfully."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid facility ID or no manager assigned to this facility."));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
