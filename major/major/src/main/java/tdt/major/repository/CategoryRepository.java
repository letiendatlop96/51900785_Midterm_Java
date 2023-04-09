package tdt.major.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tdt.major.model.Category;

import java.util.List;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
