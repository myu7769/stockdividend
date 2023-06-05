package zerobase.stockdividend.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zerobase.stockdividend.model.ScrapedResult;
import zerobase.stockdividend.service.FinanceService;

@Slf4j
@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
//        log.info("company Name + " + companyName);
        var dividendByCompanyName = this.financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(dividendByCompanyName);
    }

}
