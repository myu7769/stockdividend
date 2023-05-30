package zerobase.stockdividend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.stockdividend.model.ScrapedResult;
import zerobase.stockdividend.service.FinanceService;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName) {
        var dividendByCompanyName = this.financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(dividendByCompanyName);
    }

}
