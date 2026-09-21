package com.altis.library_backend.dashboard.controllers;

import com.altis.library_backend.dashboard.models.dtos.AdminDashboardResponseDTO;
import com.altis.library_backend.dashboard.models.dtos.UserDashboardResponseDTO;
import com.altis.library_backend.dashboard.services.DashboardService;
import com.altis.library_backend.users.models.entities.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDashboardResponseDTO> getUserDashboard(
            @AuthenticationPrincipal UserEntity loggedUser
    ) {
        UserDashboardResponseDTO response =
                dashboardService.getUserDashboard(loggedUser.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponseDTO> getAdminDashboard() {
        AdminDashboardResponseDTO response =
                dashboardService.getAdminDashboard();

        return ResponseEntity.ok(response);
    }
}