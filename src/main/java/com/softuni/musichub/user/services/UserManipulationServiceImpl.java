package com.softuni.musichub.user.services;

import com.softuni.musichub.user.entities.Role;
import com.softuni.musichub.user.entities.User;
import com.softuni.musichub.user.models.bindingModels.EditUser;
import com.softuni.musichub.user.models.bindingModels.RegisterUser;
import com.softuni.musichub.user.models.viewModels.RoleView;
import com.softuni.musichub.user.repositories.UserRepository;
import com.softuni.musichub.user.staticData.AccountConstants;
import com.softuni.musichub.util.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserManipulationServiceImpl implements UserManipulationService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final MapperUtil mapperUtil;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserManipulationServiceImpl(UserRepository userRepository,
                                       RoleService roleService,
                                       MapperUtil mapperUtil,
                                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.mapperUtil = mapperUtil;
        this.passwordEncoder = passwordEncoder;
    }

    private Set<RoleView> getRolesByNames(Set<String> roleNames) {
        Set<RoleView> roleViews = new HashSet<>();
        for (String roleName : roleNames) {
            RoleView roleView = this.roleService.findByName(roleName);
            if (roleView == null) {
                continue;
            }

            roleViews.add(roleView);
        }

        return roleViews;
    }

    @Override
    public User registerUser(RegisterUser registerUser) {
        User user = this.mapperUtil.getModelMapper().map(registerUser, User.class);
        String password = user.getPassword();
        String hashedPassword = this.passwordEncoder.encode(password);
        user.setPassword(hashedPassword);

        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);

        RoleView roleView = this.roleService.findByName(AccountConstants.ROLE_USER);
        Role role = this.mapperUtil.getModelMapper().map(roleView, Role.class);
        user.getAuthorities().add(role);
        User savedUser = this.userRepository.save(user);
        return this.mapperUtil.getModelMapper().map(savedUser, User.class);
    }

    @Override
    public User edit(EditUser editUser, String username) {
        User user = this.userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        Set<String> roleNames = editUser.getRoleNames();
        Set<RoleView> roleViews = this.getRolesByNames(roleNames);
        if (roleViews.isEmpty()) {
            return null;
        }

        List<Role> roleList = this.mapperUtil.convertAll(roleViews, Role.class);
        Set<Role> newRoles = new HashSet<>(roleList);
        user.setAuthorities(newRoles);
        return this.mapperUtil.getModelMapper().map(user, User.class);
    }

    @Override
    public boolean deleteByUsername(String username) {
        User user = this.userRepository.findByUsername(username);
        if (user == null) {
            return false;
        }

        this.userRepository.delete(user);
        return true;
    }
}
