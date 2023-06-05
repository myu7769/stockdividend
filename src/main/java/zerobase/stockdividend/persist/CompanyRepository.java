package zerobase.stockdividend.persist;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.stockdividend.persist.CompanyEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


@Repository
public interface CompanyRepository extends JpaRepository<zerobase.stockdividend.persist.CompanyEntity,Long> {
    boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name); // Optional은 null 포인트 Exception 방지와 값이 없는 경우 처리 수월


    Optional<CompanyEntity> findByTicker(String ticker);
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}
