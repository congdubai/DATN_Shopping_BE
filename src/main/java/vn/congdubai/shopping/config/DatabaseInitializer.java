package vn.congdubai.shopping.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.congdubai.shopping.domain.Role;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.repository.RoleRepository;
import vn.congdubai.shopping.repository.UserRepository;
import vn.congdubai.shopping.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countRoles == 0) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Admin thì full permissions");
            this.roleRepository.save(adminRole);

            Role clientRole = new Role();
            clientRole.setName("USER");
            clientRole.setDescription("USER");
            this.roleRepository.save(clientRole);

            Role staffRole = new Role();
            staffRole.setName("STAFF");
            staffRole.setDescription("STAFF");
            this.roleRepository.save(staffRole);
        }
        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hn");
            adminUser.setAge(25);
            adminUser.setGender(GenderEnum.Nam);
            adminUser.setName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }
            this.userRepository.save(adminUser);

            User clientUser = new User();
            clientUser.setEmail("user@gmail.com");
            clientUser.setAddress("hn");
            clientUser.setAge(25);
            clientUser.setGender(GenderEnum.Nam);
            clientUser.setName("I'm client");
            clientUser.setPassword(this.passwordEncoder.encode("123456"));

            Role clientRole = this.roleRepository.findByName("USER");
            if (clientRole != null) {
                clientUser.setRole(clientRole);
            }
            this.userRepository.save(clientUser);

            User staffUser = new User();
            staffUser.setEmail("staff@gmail.com");
            staffUser.setAddress("hn");
            staffUser.setAge(25);
            staffUser.setGender(GenderEnum.Nam);
            staffUser.setName("I'm staff");
            staffUser.setPassword(this.passwordEncoder.encode("123456"));

            Role staffRole = this.roleRepository.findByName("STAFF");
            if (staffRole != null) {
                staffUser.setRole(staffRole);
            }
            this.userRepository.save(staffUser);
        }

        if (countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }

}