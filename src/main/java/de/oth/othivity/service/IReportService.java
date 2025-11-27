package de.oth.othivity.service;

import org.springframework.stereotype.Service;

import de.oth.othivity.model.report.ActivityReport;
import de.oth.othivity.model.report.ClubReport;
import de.oth.othivity.model.report.ProfileReport;
import de.oth.othivity.dto.ReportDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Activity;

import java.util.List;

@Service

public interface IReportService {
    List<ClubReport> getAllClubReports();

    List<ActivityReport> getAllActivityReports();

    List<ProfileReport> getAllProfileReports();

    ClubReport createClubReport(ReportDto reportDto, Profile issuer, Club club);

    ProfileReport createProfileReport(ReportDto reportDto, Profile issuer, Profile profile);

    ActivityReport createActivityReport(ReportDto reportDto, Profile issuer, Activity activity);
}
