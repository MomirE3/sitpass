package svt.projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import svt.projekat.service.AnalyticsService;
import svt.projekat.service.UserService;
import svt.projekat.model.entity.User;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserService userService;

    @GetMapping("/facility/{facilityId}/weekly")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getWeeklyAnalytics(
            @PathVariable Long facilityId,
            Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        if (!isAdminOrManager(userDetails, facilityId)) {
            return ResponseEntity.status(403).body(null);
        }

        Map<String, Object> analyticsData = analyticsService.getWeeklyAnalytics(facilityId);
        return ResponseEntity.ok(analyticsData);
    }

    @GetMapping("/facility/{facilityId}/monthly")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMonthlyAnalytics(
            @PathVariable Long facilityId,
            Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        if (!isAdminOrManager(userDetails, facilityId)) {
            return ResponseEntity.status(403).body(null);
        }

        Map<String, Object> analyticsData = analyticsService.getMonthlyAnalytics(facilityId);
        return ResponseEntity.ok(analyticsData);
    }

    @GetMapping("/facility/{facilityId}/yearly")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getYearlyAnalytics(
            @PathVariable Long facilityId,
            Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        if (!isAdminOrManager(userDetails, facilityId)) {
            return ResponseEntity.status(403).body(null);
        }

        Map<String, Object> analyticsData = analyticsService.getYearlyAnalytics(facilityId);
        return ResponseEntity.ok(analyticsData);
    }

    @GetMapping("/facility/{facilityId}/custom")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCustomAnalytics(
            @PathVariable Long facilityId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername());

        if (!isAdminOrManager(userDetails, facilityId)) {
            return ResponseEntity.status(403).body(null);
        }

        Map<String, Object> analyticsData = analyticsService.getCustomAnalytics(
                facilityId,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );

        return ResponseEntity.ok(analyticsData);
    }

    private boolean isAdminOrManager(UserDetails userDetails, Long facilityId) {
        User user = userService.findByEmail(userDetails.getUsername());
        boolean isAdmin = analyticsService.isAdmin(user.getId());
        boolean isManager = analyticsService.isManagerOfFacility(facilityId, user.getId());

        return isAdmin || isManager;
    }
}
