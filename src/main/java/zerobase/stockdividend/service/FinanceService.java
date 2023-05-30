package zerobase.stockdividend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import zerobase.stockdividend.model.Company;
import zerobase.stockdividend.model.Dividend;
import zerobase.stockdividend.model.ScrapedResult;
import zerobase.stockdividend.persist.CompanyEntity;
import zerobase.stockdividend.persist.CompanyRepository;
import zerobase.stockdividend.persist.DividendRepository;
import zerobase.stockdividend.persist.entity.DividendEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    public ScrapedResult getDividendByCompanyName(String companyName) {
        // 회사명 기준으로 회사 정보 조회
        CompanyEntity company = companyRepository.findByName(companyName)
                                            .orElseThrow( () -> new RuntimeException("존재하지 않는 회사명입니다.")); // 값이 없으면 Throw , 있으면  옵셔널을 벗기고 알멩이를 줌

        // 회사 ID로  배당금 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        List<Dividend> dividends = new ArrayList<>();

        for (var entity : dividendEntities) {

            dividends.add(Dividend.builder()
                    .date(entity.getDate())
                    .dividend(entity.getDividend())
                    .build());
        }

/*        dividends = dividendEntities.stream()
                .map( e -> Dividend.builder()
                        .date(e.getDate())
                        .dividend(e.getDividend())
                        .build())
                .collect(Collectors.toList());*/

        // 결과 조합 후 리턴
        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(),
                dividends);
    }
}
