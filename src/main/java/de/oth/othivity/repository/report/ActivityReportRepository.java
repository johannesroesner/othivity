package de.oth.othivity.repository.report;

import de.oth.othivity.model.report.ActivityReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityReportRepository extends JpaRepository<ActivityReport, UUID> {

}
