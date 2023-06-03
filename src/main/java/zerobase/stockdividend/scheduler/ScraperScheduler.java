package zerobase.stockdividend.scheduler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zerobase.stockdividend.model.Company;
import zerobase.stockdividend.model.ScrapedResult;
import zerobase.stockdividend.model.constants.CacheKey;
import zerobase.stockdividend.persist.CompanyEntity;
import zerobase.stockdividend.persist.CompanyRepository;
import zerobase.stockdividend.persist.DividendRepository;
import zerobase.stockdividend.persist.entity.DividendEntity;
import zerobase.stockdividend.scraper.Scraper;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final Scraper YahooFinanceScraper;

    private final DividendRepository dividendRepository;
    //일정 주기 마다 배당금 조회
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) // 스케쥴러 주기마다 캐시 비움
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {

//        log.info("scraping scheduler is started!");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = this.companyRepository.findAll(); // String batch를 사용해 많은 데이터를 받아서 처리하면 좋음

        // 회사마다 배당금 정보를 새로 스크래핑

        for (var company : companies) {
            log.info("scraping scheduler is started this company -> :" + company.getName());
            ScrapedResult scrap = this.YahooFinanceScraper.scrap(new Company(company.getName(),company.getTicker()));

            // 스크래핑한 배당금 정보 중 데이트에 없는 값은 저장
            scrap.getDividends().stream()
                    // 디비든모델을 디비든 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 디비든 레파지토리에 삽입
                    .forEach( e -> {
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists) {
                            this.dividendRepository.save(e);
                        }
                    });
            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시 정지 ( 서버에 과부하 방지)
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }

            System.out.println(" now -> : " + System.currentTimeMillis());

        }

    }
}
