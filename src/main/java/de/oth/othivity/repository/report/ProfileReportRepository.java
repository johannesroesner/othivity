package de.oth.othivity.repository.report;

import de.oth.othivity.model.report.ProfileReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileReportRepository extends JpaRepository<ProfileReport, UUID> {

}
