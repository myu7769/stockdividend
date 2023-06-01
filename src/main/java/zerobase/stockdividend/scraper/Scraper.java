package zerobase.stockdividend.scraper;

import zerobase.stockdividend.model.Company;
import zerobase.stockdividend.model.ScrapedResult;

public interface Scraper {

    ScrapedResult scrap(Company company);
    Company scrapCompanyByTicker(String ticker);

}
