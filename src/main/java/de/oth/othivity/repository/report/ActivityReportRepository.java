package de.oth.othivity.repository.report;

import de.oth.othivity.model.report.ActivityReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ActivityReportRepository extends JpaRepository<ActivityReport, UUID> {

}
