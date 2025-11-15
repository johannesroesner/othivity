package de.oth.othivity.repository.report;

import de.oth.othivity.model.report.ClubReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClubReportRepository extends JpaRepository<ClubReport, UUID> {

}
