package de.oth.othivity.repository.report;

import de.oth.othivity.model.report.ProfileReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileReportRepository extends JpaRepository<ProfileReport, UUID> {

}
