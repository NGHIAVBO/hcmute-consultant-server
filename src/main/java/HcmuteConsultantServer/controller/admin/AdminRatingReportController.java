package HcmuteConsultantServer.controller.admin;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;

import HcmuteConsultantServer.service.implement.admin.AdminRatingReportServiceImpl;
import HcmuteConsultantServer.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${base.url}")
@PreAuthorize(SecurityConstants.PreAuthorize.ADMIN)
@RequiredArgsConstructor
public class AdminRatingReportController {

    private final AdminRatingReportServiceImpl ratingReportService;

    @GetMapping("/admin/ratings/export")
    public ResponseEntity<byte[]> exportRatingReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        byte[] report = ratingReportService.generateRatingReport(fromDate, toDate);
        
        String filename = String.format("bao-cao-danh-gia-%s.csv", 
            LocalDate.now().format(DateTimeFormatter.ISO_DATE));

        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = MediaType.parseMediaType("text/csv;charset=UTF-8");
        report = addBOM(report);
        
        headers.setContentType(mediaType);
        headers.setContentDisposition(
            ContentDisposition.builder("attachment")
                .filename(filename, StandardCharsets.UTF_8)
                .build());

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    private byte[] addBOM(byte[] input) {
        byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        byte[] result = new byte[bom.length + input.length];
        System.arraycopy(bom, 0, result, 0, bom.length);
        System.arraycopy(input, 0, result, bom.length, input.length);
        return result;
    }
} 