package pers.project.salesmanagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pers.project.salesmanagement.entity.AppUser;
import pers.project.salesmanagement.entity.Role;
import pers.project.salesmanagement.entity.Tenant;
import pers.project.salesmanagement.entity.status.UserStatus;
import pers.project.salesmanagement.repository.AppUserRepository;
import pers.project.salesmanagement.repository.RoleRepository;
import pers.project.salesmanagement.repository.TenantRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final TenantRepository tenantRepository;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    role.setDescription("Administrator Role");
                    return roleRepository.save(role);
                });

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("USER");
                    role.setDescription("Standard User Role");
                    return roleRepository.save(role);
                });

        // 2. Seed Tenant
        Tenant tenant = tenantRepository.findByCode("TS1")
                .orElseGet(() -> {
                    Tenant newTenant = new Tenant();
                    newTenant.setCode("TS1");
                    newTenant.setName("Tenant Store 1");
                    return tenantRepository.save(newTenant);
                });

        // 3. Seed Admin User (No Tenant, Role ADMIN)
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            AppUser admin = new AppUser();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("Administrator");
            admin.setPhone("0123456789");
            admin.setStatus(UserStatus.ACTIVE);
            admin.setTenant(null); // No tenant
            admin.setRoles(List.of(adminRole));

            userRepository.save(admin);
        }

        // 4. Seed Standard User (Tenant TS1, Role USER)
        if (userRepository.findByEmail("user@gmail.com").isEmpty()) {
            AppUser regularUser = new AppUser();
            regularUser.setEmail("user@gmail.com");
            regularUser.setPassword(passwordEncoder.encode("user1234"));
            regularUser.setName("User");
            regularUser.setPhone("0123456788");
            regularUser.setStatus(UserStatus.ACTIVE);
            regularUser.setTenant(tenant); // Linked to Tenant TS1
            regularUser.setRoles(List.of(userRole));

            userRepository.save(regularUser);
        }
    }
}
