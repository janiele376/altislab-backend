package com.altis.library_backend.dashboard.controllers;

import com.altis.library_backend.dashboard.models.dtos.AdminDashboardResponseDTO;
import com.altis.library_backend.dashboard.models.dtos.UserDashboardResponseDTO;
import com.altis.library_backend.dashboard.services.DashboardService;
import com.altis.library_backend.users.models.entities.UserEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
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
            @AuthenticationPrincipal UserEntity loggedUser,
            @ParameterObject Pageable pageable
    ) {

        UserDashboardResponseDTO response =
                dashboardService.getUserDashboard(
                        loggedUser.getId(),
                        pageable
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponseDTO> getAdminDashboard(
            @ParameterObject Pageable pageable
    ) {

        AdminDashboardResponseDTO response =
                dashboardService.getAdminDashboard(pageable);

        return ResponseEntity.ok(response);
    }
}