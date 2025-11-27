package de.oth.othivity.service.impl;

import de.oth.othivity.repository.report.ClubReportRepository;
import de.oth.othivity.repository.main.ClubRepository;
import de.oth.othivity.repository.main.ActivityRepository;
import de.oth.othivity.repository.main.ProfileRepository;
import de.oth.othivity.repository.report.ActivityReportRepository;
import de.oth.othivity.repository.report.ProfileReportRepository;
import de.oth.othivity.service.IReportService;
import lombok.AllArgsConstructor;
import de.oth.othivity.model.report.ActivityReport;
import de.oth.othivity.model.report.ClubReport;
import de.oth.othivity.model.report.ProfileReport;
import de.oth.othivity.dto.ReportDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.main.Club;
import de.oth.othivity.model.main.Activity;


import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ReportServiceImpl implements IReportService {

    private final ClubReportRepository clubReportRepository;
    private final ActivityReportRepository activityReportRepository;
    private final ProfileReportRepository profileReportRepository;

    @Override
    public List<ClubReport> getAllClubReports() {
        return clubReportRepository.findAll();
    }

    @Override
    public List<ActivityReport> getAllActivityReports() {
        return activityReportRepository.findAll();
    }

    @Override
    public List<ProfileReport> getAllProfileReports() {
        return profileReportRepository.findAll();
    }

    @Override
    public ClubReport createClubReport(ReportDto reportDto, Profile issuer, Club club) {
        ClubReport clubReport = new ClubReport();
        clubReport.setIssuer(issuer);
        clubReport.setClub(club);
        clubReport.setComment(reportDto.getComment());
        return clubReportRepository.save(clubReport);
    }
    @Override
    public ProfileReport createProfileReport(ReportDto reportDto, Profile issuer, Profile profile) {
        ProfileReport profileReport = new ProfileReport();
        profileReport.setIssuer(issuer);
        profileReport.setProfile(profile);
        profileReport.setComment(reportDto.getComment());
        return profileReportRepository.save(profileReport);
    }
    @Override
    public ActivityReport createActivityReport(ReportDto reportDto, Profile issuer, Activity activity) {
        ActivityReport activityReport = new ActivityReport();
        activityReport.setIssuer(issuer);
        activityReport.setActivity(activity);
        activityReport.setComment(reportDto.getComment());
        return activityReportRepository.save(activityReport);
    }
    @Override
    public void acceptClubReport(UUID reportId) {
        clubReportRepository.deleteById(reportId);
    }
    @Override
    public void acceptActivityReport(UUID reportId) {
        activityReportRepository.deleteById(reportId);
    }
    @Override
    public void acceptProfileReport(UUID reportId) {
        profileReportRepository.deleteById(reportId);
    }
    @Override
    public void rejectClubReport(UUID reportId) {
        clubReportRepository.deleteById(reportId);
    }
    @Override
    public void rejectActivityReport(UUID reportId) {
        activityReportRepository.deleteById(reportId);
    }
    @Override
    public void rejectProfileReport(UUID reportId) {
        profileReportRepository.deleteById(reportId);
    }
    @Override
    public int countReports() {
        int clubReports = (int) clubReportRepository.count();
        int activityReports = (int) activityReportRepository.count();
        int profileReports = (int) profileReportRepository.count();
        return clubReports + activityReports + profileReports;
    }
}