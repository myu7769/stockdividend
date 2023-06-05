package zerobase.stockdividend.service;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zerobase.stockdividend.exception.impl.AlreadycompanyException;
import zerobase.stockdividend.exception.impl.NoCompanyException;
import zerobase.stockdividend.model.Company;
import zerobase.stockdividend.model.ScrapedResult;
import zerobase.stockdividend.persist.CompanyEntity;
import zerobase.stockdividend.persist.CompanyRepository;
import zerobase.stockdividend.persist.DividendRepository;
import zerobase.stockdividend.persist.entity.DividendEntity;
import zerobase.stockdividend.scraper.Scraper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {


    private final Trie trie; //Bean으로 관리 config/Appconfig
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;


    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);

        if (exists) {
            throw new AlreadycompanyException();
        }

        return this.storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 회사를 스크랩핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

        // 해당 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapperResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        // CompanyID 와 같이 저장해야됨
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities = scrapperResult.getDividends().stream()
                                                .map(e -> new DividendEntity(companyEntity.getId(), e))
                                                .collect(Collectors.toList());
        // diviend 저장
        this.dividendRepository.saveAll(dividendEntities);

        return company;


    }

    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        return (List<String>)this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10) // 스트림의 반환 갯수를 정해줘 프론트로 날릴 개수를 정함
                .collect(Collectors.toList());
    }

    public void deleteAutocompleteKeyword(String keyword) {

        this.trie.remove(keyword);
    }

    public String deleteCompany(String ticker) {
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(()-> new NoCompanyException());
        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        this.deleteAutocompleteKeyword(company.getName());


        return company.getName();
    }


    public List<String> getCompanyNamesByKeyword(String keyword) {

        Pageable limit = PageRequest.of(0, 10);

        Page<CompanyEntity> byNameStartingWithIgnoreCase = this.companyRepository.findByNameStartingWithIgnoreCase(keyword , limit);

        return byNameStartingWithIgnoreCase.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());

    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }


}
