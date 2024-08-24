package svt.projekat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import svt.projekat.service.DisciplineService;

import java.util.List;

@RestController
@RequestMapping("/api/disciplines")
public class DisciplineController {

    @Autowired
    private DisciplineService disciplineService;

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllUniqueDisciplineNames() {
        List<String> disciplineNames = disciplineService.getAllUniqueDisciplineNames();
        return ResponseEntity.ok(disciplineNames);
    }
}
