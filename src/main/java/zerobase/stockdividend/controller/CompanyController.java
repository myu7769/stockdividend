package zerobase.stockdividend.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import zerobase.stockdividend.model.Company;
import zerobase.stockdividend.persist.CompanyEntity;
import zerobase.stockdividend.service.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        return null;
    }

    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable) { // final Pageable pageable 을 사용해 프론트에서 요청한 page만 넘겨준다.( 전체 다 넘기면 비효율적)
        Page<CompanyEntity> companyEntityList = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companyEntityList);
    }

    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();

        if (ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("ticker is Empty!");
        }
        Company company = this.companyService.save(ticker);
        return ResponseEntity.ok(company);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
