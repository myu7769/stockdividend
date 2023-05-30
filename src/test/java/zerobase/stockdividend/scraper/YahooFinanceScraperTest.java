package zerobase.stockdividend.scraper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import zerobase.stockdividend.model.Company;
import zerobase.stockdividend.model.ScrapedResult;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class YahooFinanceScraperTest {
    YahooFinanceScraper yahooFinanceScraper = new YahooFinanceScraper();
    @Test
    public void scrap(){
        //given
        //when
        ScrapedResult result = yahooFinanceScraper.scrap(Company.builder().ticker("O").build());
        //then

        assertEquals("O",result.getCompany().getTicker());
        System.out.println(result.toString());
       }
       @Test
       public void scrapCompanyByTicker(){
           //given
           //when
           Company company = yahooFinanceScraper.scrapCompanyByTicker("MMM");
           //then

           assertEquals("MMM", company.getTicker());
           System.out.println(company.toString());

          }

}