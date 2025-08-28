package finance_manager.controller;

import finance_manager.security.SecurityUtils;
import finance_manager.service.ReportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(@PathVariable("year") int year,
                                                          @PathVariable("month") int month)
    {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized - Please login first"));
        }
        return ResponseEntity.ok(reportService.generateMonthlyReport(year, month, username));
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<Map<String, Object>> getYearlyReport(@PathVariable("year") int year){
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized - Please login first"));
        }
        return ResponseEntity.ok(reportService.generateYearlyReport(year, username));
    }
}
