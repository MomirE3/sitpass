package svt.projekat.service;

import svt.projekat.model.dto.UserDTO;
import svt.projekat.model.entity.User;

import java.util.List;

public interface UserService {
    User findByEmail(String email);

    User createUser(UserDTO userDTO);

    List<User> findAll();
    List<User> findAllUsersWithUserType();
}
