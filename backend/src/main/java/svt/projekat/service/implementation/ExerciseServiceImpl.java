package svt.projekat.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import svt.projekat.model.dto.ExerciseDTO;
import svt.projekat.model.entity.Exercise;
import svt.projekat.model.entity.Facility;
import svt.projekat.model.entity.WorkDay;
import svt.projekat.repository.ExerciseRepository;
import svt.projekat.repository.FacilityRepository;
import svt.projekat.repository.UserRepository;
import svt.projekat.service.ExerciseService;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Override
    public Exercise createExercise(ExerciseDTO exerciseDTO) {
        if (exerciseDTO.getFacilityId() == null) {
            throw new IllegalArgumentException("Facility ID must not be null");
        }

        if (exerciseDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        Optional<Facility> facilityOpt = facilityRepository.findById(exerciseDTO.getFacilityId());
        if (!facilityOpt.isPresent()) {
            throw new IllegalArgumentException("Invalid facility ID");
        }

        Facility facility = facilityOpt.get();

        if (!facility.isActive()) {
            throw new IllegalArgumentException("Cannot create exercise for an inactive facility");
        }

        LocalDateTime fromDateTime = exerciseDTO.getFrom();
        LocalDateTime untilDateTime = exerciseDTO.getUntil();

        if (fromDateTime == null || untilDateTime == null) {
            throw new IllegalArgumentException("From and Until times must not be null");
        }

        LocalDateTime now = LocalDateTime.now();
        if (fromDateTime.isBefore(now)) {
            throw new IllegalArgumentException("Cannot create exercise for a past date");
        }

        if (untilDateTime.isBefore(fromDateTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        DayOfWeek dayOfWeek = fromDateTime.getDayOfWeek();
        Set<WorkDay> workDays = facility.getWorkDays();

        Optional<WorkDay> workDayOpt = workDays.stream()
                .filter(workDay -> workDay.getDay().name().equals(dayOfWeek.name()))
                .findFirst();

        if (!workDayOpt.isPresent()) {
            throw new IllegalArgumentException("Facility is not open on the selected day");
        }

        WorkDay workDay = workDayOpt.get();
        LocalTime fromTime = fromDateTime.toLocalTime();
        LocalTime untilTime = untilDateTime.toLocalTime();

        if (fromTime.isBefore(workDay.getFrom()) || untilTime.isAfter(workDay.getUntil())) {
            throw new IllegalArgumentException("Gym works from " + workDay.getFrom() + " to " + workDay.getUntil());
        }

        Exercise exercise = new Exercise();
        exercise.setWentBy(userRepository.findById(exerciseDTO.getUserId()).orElseThrow(() -> new IllegalArgumentException("Invalid user ID")));
        exercise.setAtFacility(facility);
        exercise.setFrom(fromDateTime);
        exercise.setUntil(untilDateTime);

        return exerciseRepository.save(exercise);
    }

    @Override
    public int getNumberOfVisits(Long userId, Long facilityId) {
        return exerciseRepository.countByUserIdAndFacilityIdAndUntilDateTimeBefore(userId, facilityId, LocalDateTime.now());
    }
}
