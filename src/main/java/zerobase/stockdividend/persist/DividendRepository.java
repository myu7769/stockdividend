package zerobase.stockdividend.persist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.stockdividend.persist.entity.DividendEntity;

import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity,Long> {

     List<DividendEntity> findAllByCompanyId(Long companyId);
}
