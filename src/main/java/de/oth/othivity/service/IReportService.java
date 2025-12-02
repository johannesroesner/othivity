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
import java.util.UUID;

@Service

public interface IReportService {
    List<ClubReport> getAllClubReports();

    List<ActivityReport> getAllActivityReports();

    List<ProfileReport> getAllProfileReports();

    ClubReport createClubReport(ReportDto reportDto, Profile issuer, Club club);

    ProfileReport createProfileReport(ReportDto reportDto, Profile issuer, Profile profile);

    ActivityReport createActivityReport(ReportDto reportDto, Profile issuer, Activity activity);

    void acceptClubReport(UUID reportId);

    void acceptActivityReport(UUID reportId);

    void acceptProfileReport(UUID reportId);

    void rejectClubReport(UUID reportId);

    void rejectActivityReport(UUID reportId);

    void rejectProfileReport(UUID reportId);

    int countReports();

    boolean isReportableClub(Profile issuer, Club club);
    boolean isReportableActivity(Profile issuer, Activity activity);
    boolean isReportableProfile(Profile issuer, Profile profile);
}
