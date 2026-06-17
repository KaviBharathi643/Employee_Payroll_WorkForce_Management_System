package com.company.payroll.repository;

import com.company.payroll.constants.ReportConstants;
import com.company.payroll.constants.StatusConstants;
import com.company.payroll.entity.User;
import com.company.payroll.entity.UserEmployment;
import com.company.payroll.entity.UserProfile;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ReportSpecifications {

    private ReportSpecifications() {
    }

    public static Specification<UserProfile> workforceReport(
            String role,
            String employmentStatus,
            String department,
            String designation,
            String employeeCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<UserProfile, User> userJoin = root.join("user");

            predicates.add(cb.equal(userJoin.get("role"), role));

            if (employmentStatus != null && !employmentStatus.isBlank()
                    && !ReportConstants.EMPLOYMENT_STATUS_ALL.equalsIgnoreCase(employmentStatus)) {
                predicates.add(cb.equal(userJoin.get("status"), employmentStatus.toUpperCase()));
            }

            if (employeeCode != null && !employeeCode.isBlank()) {
                predicates.add(cb.equal(root.get("employeeCode"), employeeCode));
            }

            Subquery<Long> employmentSubquery = query.subquery(Long.class);
            var employmentRoot = employmentSubquery.from(UserEmployment.class);
            employmentSubquery.select(employmentRoot.get("id"));
            List<Predicate> employmentPredicates = new ArrayList<>();
            employmentPredicates.add(cb.equal(employmentRoot.get("user"), userJoin));

            if (department != null && !department.isBlank()) {
                employmentPredicates.add(cb.equal(employmentRoot.get("department"), department));
            }
            if (designation != null && !designation.isBlank()) {
                employmentPredicates.add(cb.equal(employmentRoot.get("designation"), designation));
            }
            if (employmentStatus != null && !employmentStatus.isBlank()
                    && !ReportConstants.EMPLOYMENT_STATUS_ALL.equalsIgnoreCase(employmentStatus)) {
                employmentPredicates.add(cb.equal(employmentRoot.get("employmentStatus"), employmentStatus.toUpperCase()));
            }

            employmentSubquery.where(employmentPredicates.toArray(new Predicate[0]));
            predicates.add(cb.exists(employmentSubquery));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
