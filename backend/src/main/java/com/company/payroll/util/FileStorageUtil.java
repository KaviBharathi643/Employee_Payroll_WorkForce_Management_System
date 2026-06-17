package com.company.payroll.util;

import com.company.payroll.exception.BusinessRuleException;
import com.company.payroll.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png"
    );
    private static final long MAX_PROFILE_SIZE = 2 * 1024 * 1024;

    @Value("${app.upload.profile-dir:uploads/profile}")
    private String profileUploadDir;

    @Value("${app.upload.payslip-dir:uploads/payslips}")
    private String payslipUploadDir;

    public Path getProfileUploadPath() {
        return ensureDirectory(profileUploadDir);
    }

    public Path getPayslipUploadPath() {
        return ensureDirectory(payslipUploadDir);
    }

    public String storeProfilePhoto(MultipartFile file) {
        validateProfilePhoto(file);
        String extension = resolveExtension(file.getContentType());
        String filename = UUID.randomUUID() + extension;
        Path target = getProfileUploadPath().resolve(filename);
        try {
            Files.copy(file.getInputStream(), target);
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to store profile photo");
        }
        return "/uploads/profile/" + filename;
    }

    public ProfilePhotoContent readProfilePhoto(String profilePhotoUrl) {
        if (profilePhotoUrl == null || profilePhotoUrl.isBlank()) {
            throw new ResourceNotFoundException("Profile Photo Not Found");
        }
        String filename = profilePhotoUrl.substring(profilePhotoUrl.lastIndexOf('/') + 1);
        Path path = getProfileUploadPath().resolve(filename);
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("Profile Photo Not Found");
        }
        try {
            byte[] content = Files.readAllBytes(path);
            return new ProfilePhotoContent(content, resolveContentType(filename));
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to read profile photo");
        }
    }

    public void deleteProfilePhoto(String profilePhotoUrl) {
        if (profilePhotoUrl == null || profilePhotoUrl.isBlank()) {
            return;
        }
        String filename = profilePhotoUrl.substring(profilePhotoUrl.lastIndexOf('/') + 1);
        Path path = getProfileUploadPath().resolve(filename);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to delete profile photo");
        }
    }

    private void validateProfilePhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("Profile photo is required");
        }
        if (file.getSize() > MAX_PROFILE_SIZE) {
            throw new BusinessRuleException("Profile photo must not exceed 2 MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessRuleException("Only JPG, JPEG, and PNG formats are allowed");
        }
    }

    private String resolveContentType(String filename) {
        if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        }
        return "image/jpeg";
    }

    public record ProfilePhotoContent(byte[] content, String contentType) {
    }

    private String resolveExtension(String contentType) {
        if (contentType == null) {
            return ".jpg";
        }
        return switch (contentType.toLowerCase()) {
            case "image/png" -> ".png";
            default -> ".jpg";
        };
    }

    private Path ensureDirectory(String dir) {
        Path path = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new BusinessRuleException("Failed to initialize upload directory");
        }
        return path;
    }
}
