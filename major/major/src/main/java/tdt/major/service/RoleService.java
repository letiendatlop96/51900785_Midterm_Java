package tdt.major.service;

import org.springframework.stereotype.Service;

import tdt.major.model.Role;

import java.util.List;
import java.util.Optional;

@Service
public interface RoleService  {
    List<Role> getAllRole();
    Optional<Role> findRoleById(int id);

}
