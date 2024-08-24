package svt.projekat.service;

import svt.projekat.model.dto.ExerciseDTO;
import svt.projekat.model.entity.Exercise;

public interface ExerciseService {
    Exercise createExercise(ExerciseDTO exerciseDTO);
    int getNumberOfVisits(Long userId, Long facilityId);

}
