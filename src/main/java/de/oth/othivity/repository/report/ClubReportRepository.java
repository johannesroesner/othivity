package de.oth.othivity.repository.report;

import de.oth.othivity.model.report.ClubReport;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.main.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClubReportRepository extends JpaRepository<ClubReport, UUID> {
    List<ClubReport> findByIssuerAndClub(Profile issuer, Club club);
}
