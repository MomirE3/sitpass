package svt.projekat.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import svt.projekat.model.dto.UserDTO;
import svt.projekat.model.entity.User;
import svt.projekat.repository.UserRepository;
import svt.projekat.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl() {
    }

    public User findByEmail(String email) {
        Optional<User> user = this.userRepository.findFirstByEmail(email);
        return user.orElse(null);    }

    public User createUser(UserDTO userDTO) {
        Optional<User> user = this.userRepository.findFirstByEmail(userDTO.getEmail());
        if (user.isPresent()) {
            return null;
        } else {
            User newUser = new User();
            newUser = (User)this.userRepository.save(newUser);
            return newUser;
        }
    }

    public List<User> findAll() {
        return this.userRepository.findAll();
    }
    public List<User> findAllUsersWithUserType() {
        return userRepository.findAllUsersWithUserType();
    }
}
