package tdt.major.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tdt.major.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
