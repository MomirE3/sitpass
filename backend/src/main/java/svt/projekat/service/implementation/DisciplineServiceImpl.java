package svt.projekat.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import svt.projekat.repository.DisciplineRepository;
import svt.projekat.service.DisciplineService;

import java.util.List;

@Service
public class DisciplineServiceImpl implements DisciplineService {

    @Autowired
    private DisciplineRepository disciplineRepository;

    @Override
    public List<String> getAllUniqueDisciplineNames() {
        return disciplineRepository.findAllUniqueDisciplineNames();
    }
}
