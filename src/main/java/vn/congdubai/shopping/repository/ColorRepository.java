package vn.congdubai.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import vn.congdubai.shopping.domain.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long>, JpaSpecificationExecutor<Color> {
    boolean existsByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Color c SET c.isDeleted = true WHERE c.id = :id")
    void softDeleteColor(@Param("id") Long id);
}
