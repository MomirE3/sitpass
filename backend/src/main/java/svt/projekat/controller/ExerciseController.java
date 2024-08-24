package svt.projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import svt.projekat.model.dto.ExerciseDTO;
import svt.projekat.service.ExerciseService;

@RestController
@RequestMapping("api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping("/create")
    public ResponseEntity<String> createExercise(@RequestBody ExerciseDTO exerciseDTO) {
        exerciseService.createExercise(exerciseDTO);
        return new ResponseEntity<>("Exercise created successfully", HttpStatus.CREATED);
    }
}
