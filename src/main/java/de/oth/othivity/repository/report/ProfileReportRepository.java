package de.oth.othivity.repository.report;

import de.oth.othivity.model.report.ProfileReport;
import de.oth.othivity.model.main.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProfileReportRepository extends JpaRepository<ProfileReport, UUID> {
    List<ProfileReport> findByIssuerAndProfile(Profile issuer, Profile profile);
}
