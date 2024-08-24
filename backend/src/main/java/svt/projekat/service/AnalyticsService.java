package svt.projekat.service;

import java.time.LocalDate;
import java.util.Map;

public interface AnalyticsService {
    Map<String, Object> getWeeklyAnalytics(Long facilityId);
    Map<String, Object> getMonthlyAnalytics(Long facilityId);
    Map<String, Object> getYearlyAnalytics(Long facilityId);
    Map<String, Object> getCustomAnalytics(Long facilityId, LocalDate startDate, LocalDate endDate);
    boolean isAdmin(Long userId);
    boolean isManagerOfFacility(Long facilityId, Long userId);
}
