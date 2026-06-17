package com.company.payroll.controller;

import com.company.payroll.dto.employee.EmployeeResponseDto;
import com.company.payroll.service.EmployeeService;
import com.company.payroll.util.ApiResponse;
import com.company.payroll.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final EmployeeService employeeService;

    @GetMapping("/photo/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    public ResponseEntity<byte[]> downloadProfilePhoto(@PathVariable Long userId) {
        FileStorageUtil.ProfilePhotoContent photo = employeeService.downloadProfilePhoto(userId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.contentType()))
                .body(photo.content());
    }

    @PostMapping("/photo")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    public ApiResponse<EmployeeResponseDto> uploadProfilePhoto(@RequestPart("profilePhoto") MultipartFile profilePhoto) {
        EmployeeResponseDto response = employeeService.uploadProfilePhoto(profilePhoto);
        return ApiResponse.success("Profile photo uploaded successfully", response);
    }

    @DeleteMapping("/photo")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    public ApiResponse<EmployeeResponseDto> deleteProfilePhoto() {
        EmployeeResponseDto response = employeeService.deleteProfilePhoto();
        return ApiResponse.success("Profile photo deleted successfully", response);
    }
}
