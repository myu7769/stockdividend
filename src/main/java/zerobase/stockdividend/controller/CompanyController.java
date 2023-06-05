package zerobase.stockdividend.controller;

import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import zerobase.stockdividend.model.Company;
import zerobase.stockdividend.model.constants.CacheKey;
import zerobase.stockdividend.persist.CompanyEntity;
import zerobase.stockdividend.service.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    private final CacheManager redisCacheManager;

    @GetMapping("autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {

//        var autocomplete = companyService.autocomplete(keyword);
        var companyNamesByKeyword = this.companyService.getCompanyNamesByKeyword(keyword);

        return ResponseEntity.ok(companyNamesByKeyword);
    }

    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) { // final Pageable pageable 을 사용해 프론트에서 요청한 page만 넘겨준다.( 전체 다 넘기면 비효율적)
        Page<CompanyEntity> companyEntityList = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companyEntityList);
    }

    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();

        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is Empty!");
        }
        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());

        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {

        String companyName = this.companyService.deleteCompany(ticker);
        //캐시에서도 삭제해야됨
        this.clearFinanceCache(companyName);
        return ResponseEntity.ok(companyName);
    }

    public void clearFinanceCache(String companyName) {
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }
}
