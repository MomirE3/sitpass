package svt.projekat.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import svt.projekat.model.entity.Administrator;
import svt.projekat.repository.ExerciseRepository;
import svt.projekat.repository.ReviewRepository;
import svt.projekat.repository.FacilityRepository;
import svt.projekat.repository.UserRepository;
import svt.projekat.service.AnalyticsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<String, Object> getWeeklyAnalytics(Long facilityId) {
        LocalDateTime startOfWeek = LocalDate.now().minus(1, ChronoUnit.WEEKS).atStartOfDay();
        LocalDateTime endOfWeek = LocalDateTime.now();

        return getAnalytics(facilityId, startOfWeek, endOfWeek);
    }

    @Override
    public Map<String, Object> getMonthlyAnalytics(Long facilityId) {
        LocalDateTime startOfMonth = LocalDate.now().minus(1, ChronoUnit.MONTHS).atStartOfDay();
        LocalDateTime endOfMonth = LocalDateTime.now();

        return getAnalytics(facilityId, startOfMonth, endOfMonth);
    }

    @Override
    public Map<String, Object> getYearlyAnalytics(Long facilityId) {
        LocalDateTime startOfYear = LocalDate.now().minus(1, ChronoUnit.YEARS).atStartOfDay();
        LocalDateTime endOfYear = LocalDateTime.now();

        return getAnalytics(facilityId, startOfYear, endOfYear);
    }

    @Override
    public Map<String, Object> getCustomAnalytics(Long facilityId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        return getAnalytics(facilityId, start, end);
    }

    private Map<String, Object> getAnalytics(Long facilityId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> analytics = new HashMap<>();

        long userCount = facilityRepository.countUsersByFacilityAndDateRange(facilityId, startDate, endDate);
        long reviewCount = facilityRepository.countReviewsByFacilityAndDateRange(facilityId, startDate, endDate);

        List<Object[]> hourlyActivity = facilityRepository.findUserActivityByHour(facilityId, startDate, endDate);

        analytics.put("userCount", userCount);
        analytics.put("reviewCount", reviewCount);
        analytics.put("hourlyActivity", hourlyActivity);

        return analytics;
    }

    @Override
    public boolean isAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user instanceof Administrator)
                .orElse(false);
    }

    @Override
    public boolean isManagerOfFacility(Long facilityId, Long userId) {
        return facilityRepository.existsByIdAndManagerId(facilityId, userId);
    }
}
