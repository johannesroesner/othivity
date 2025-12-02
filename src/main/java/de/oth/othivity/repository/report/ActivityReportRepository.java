package de.oth.othivity.repository.report;

import de.oth.othivity.model.report.ActivityReport;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.main.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityReportRepository extends JpaRepository<ActivityReport, UUID> {
    List<ActivityReport> findByIssuerAndActivity(Profile issuer, Activity activity);
}
