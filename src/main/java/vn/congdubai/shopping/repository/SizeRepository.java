package vn.congdubai.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import vn.congdubai.shopping.domain.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long>, JpaSpecificationExecutor<Size> {
    boolean existsByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Size c SET c.isDeleted = true WHERE c.id = :id")
    void softDeleteSize(@Param("id") Long id);
}
