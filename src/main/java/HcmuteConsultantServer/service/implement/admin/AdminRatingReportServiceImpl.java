package HcmuteConsultantServer.service.implement.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import HcmuteConsultantServer.model.entity.RatingEntity;
import HcmuteConsultantServer.model.entity.UserInformationEntity;
import HcmuteConsultantServer.repository.actor.RatingRepository;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class AdminRatingReportServiceImpl {
    private static final String EXCEL_DIR = "src/main/resources/excel";
    
    private final RatingRepository ratingRepository;

    @Deprecated
    public byte[] generateRatingReport(String format, LocalDate fromDate, LocalDate toDate) {
        return generateRatingReport(fromDate, toDate);
    }

    public byte[] generateRatingReport(LocalDate fromDate, LocalDate toDate) {
        List<RatingEntity> ratings = ratingRepository.findBySubmittedAtBetween(fromDate, toDate);

        Map<UserInformationEntity, List<RatingEntity>> ratingsByConsultant = ratings.stream()
            .collect(Collectors.groupingBy(RatingEntity::getConsultant));

        byte[] reportContent = generateCsvReport(ratingsByConsultant);
        
        try {
            Files.createDirectories(Paths.get(EXCEL_DIR));
            
            String filename = String.format("bao-cao-danh-gia-%s.csv", 
                LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            Path filePath = Paths.get(EXCEL_DIR, filename);
            
            Files.write(filePath, reportContent);
            
            return reportContent;
        } catch (IOException e) {
            throw new RuntimeException("Không thể lưu file báo cáo", e);
        }
    }

    private String formatRoleName(String roleName) {
        switch (roleName) {
            case "ROLE_TUVANVIEN":
                return "Tư vấn viên";
            case "ROLE_TRUONGTUVAN":
                return "Trưởng ban tư vấn";
            default:
                return roleName;
        }
    }

    private byte[] generateCsvReport(Map<UserInformationEntity, List<RatingEntity>> ratingsByConsultant) {
        StringBuilder csv = new StringBuilder();
        csv.append("BÁO CÁO ĐÁNH GIÁ TƯ VẤN VIÊN\n");
        csv.append(String.format("Thời gian xuất báo cáo: %s\n\n", 
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        csv.append("STT,Họ và tên,MSSV/MSGV,Email,Phòng/Khoa,Chức vụ,Số lượng đánh giá,");
        csv.append("Mức hài lòng chung,Kiến thức chuyên môn,Thái độ phục vụ,Tốc độ phản hồi,Khả năng nắm bắt vấn đề\n");

        int stt = 1;
        for (Map.Entry<UserInformationEntity, List<RatingEntity>> entry : ratingsByConsultant.entrySet()) {
            UserInformationEntity consultant = entry.getKey();
            List<RatingEntity> ratings = entry.getValue();

            csv.append(String.format("%d,", stt++));
            csv.append(String.format("\"%s %s\",", consultant.getLastName(), consultant.getFirstName()));
            csv.append(String.format("\"%s\",", consultant.getStudentCode())); 
            csv.append(String.format("\"%s\",", consultant.getAccount().getEmail())); 
            csv.append(String.format("\"%s\",", consultant.getAccount().getDepartment().getName()));
            csv.append(String.format("\"%s\",", formatRoleName(consultant.getAccount().getRole().getName())));
            csv.append(String.format("%d,", ratings.size()));
            
            csv.append(String.format("%.2f,", getAverageRating(ratings, RatingEntity::getGeneralSatisfaction) * 2));
            csv.append(String.format("%.2f,", getAverageRating(ratings, RatingEntity::getExpertiseKnowledge) * 2));
            csv.append(String.format("%.2f,", getAverageRating(ratings, RatingEntity::getAttitude) * 2));
            csv.append(String.format("%.2f,", getAverageRating(ratings, RatingEntity::getResponseSpeed) * 2));
            csv.append(String.format("%.2f\n", getAverageRating(ratings, RatingEntity::getUnderstanding) * 2));
        }

        csv.append("\nThống kê chung:\n");
        csv.append(String.format("Tổng số tư vấn viên: %d\n", ratingsByConsultant.size()));
        csv.append(String.format("Tổng số đánh giá: %d\n", 
            ratingsByConsultant.values().stream().mapToInt(List::size).sum()));
        
        double avgOverall = ratingsByConsultant.values().stream()
            .flatMap(List::stream)
            .mapToDouble(RatingEntity::getGeneralSatisfaction)
            .average()
            .orElse(0.0) * 2;
        csv.append(String.format("Điểm đánh giá trung bình: %.2f/10\n", avgOverall));

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private double getAverageRating(List<RatingEntity> ratings, Function<RatingEntity, Integer> ratingExtractor) {
        return ratings.stream()
            .mapToInt(ratingExtractor::apply)
            .average()
            .orElse(0.0);
    }
} 