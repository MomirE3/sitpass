package svt.projekat.service.implementation;


import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import svt.projekat.model.entity.Administrator;
import svt.projekat.model.entity.User;
import svt.projekat.service.UserService;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    public UserDetailsServiceImpl() {
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("There is no user with email " + email);
        } else {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            String role = "ROLE_";
            if (user instanceof Administrator) {
                role = role + "ADMIN";
            } else {
                role = role + "USER";
            }

            grantedAuthorities.add(new SimpleGrantedAuthority(role));

            System.out.println("User email: " + user.getEmail());
            System.out.println("Assigned role: " + role);

            return new org.springframework.security.core.userdetails.User(user.getEmail().trim(), user.getPassword().trim(), grantedAuthorities);
        }
    }
}
