package com.example.G4_DailyReport.controller.user;


import com.example.G4_DailyReport.model.Department;
import com.example.G4_DailyReport.model.Project;
import com.example.G4_DailyReport.model.Report;
import com.example.G4_DailyReport.model.User;
import com.example.G4_DailyReport.repository.ProjectRepository;
import com.example.G4_DailyReport.repository.ReportRepository;
import com.example.G4_DailyReport.repository.UserRepository;
import com.example.G4_DailyReport.service.EmailSenderService;
import com.example.G4_DailyReport.util.CurrentUserUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller("UserReportController")
@RequestMapping("/reports")
@SessionAttributes("report")
public class ReportController {
    @ModelAttribute(name = "report")
    public Report report() {
        Report report = new Report();
        report.setReportDate(LocalDate.now());
        return report;
    }

    @Autowired
    private ReportRepository reportRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private ProjectRepository projectRepo;
    @Autowired
    private EmailSenderService emailSenderService;
    
    public ReportController(ReportRepository reportRepo) {
        this.reportRepo = reportRepo;
    }

    @GetMapping("/new")
    public String newReportForm(Model model) {
        //Redirect if today's report already exists
        Report report = reportRepo.findReportByReportDate(LocalDate.now());
        if (report != null) {
            model.addAttribute(report);
            return "/user/reports/edit";
        } else {
            report = new Report();
            report.setReportDate(LocalDate.now());
            List<Project> projects = projectRepo.findAll();
            model.addAttribute(report);
            model.addAttribute("projects", projects);
            return "/user/reports/new";
        }
    }

    @GetMapping("/edit")
    public String edit(@ModelAttribute Report report, Model model) {
        model.addAttribute("report", report);
        return "/user/reports/edit";
    }

    @PostMapping("/")
    public String create(@Valid Report report, Errors errors) {
        User user = userRepo.findByUserName(projectRepo.findProjectById(report.getProject().getId()).getCreatedBy()).orElse(null);
        report.setUser(user);
        if (errors.hasErrors()) {
            return "/user/reports/new";
        }

        Report savedReport = reportRepo.save(report);
        // Send email to manager in the project
        emailSenderService.sendReportNotifyEmailToManager(savedReport);
        return "redirect:/reports/edit";
    }
}
