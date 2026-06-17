package com.company.payroll.repository;

import com.company.payroll.constants.RoleConstants;
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

public final class EmployeeSpecifications {

    private EmployeeSpecifications() {
    }

    public static Specification<UserProfile> activeEmployees(
            String search,
            String department,
            String designation) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<UserProfile, User> userJoin = root.join("user");

            predicates.add(cb.equal(userJoin.get("role"), RoleConstants.EMPLOYEE));
            predicates.add(cb.equal(userJoin.get("status"), StatusConstants.ACTIVE));

            Subquery<Long> employmentSubquery = query.subquery(Long.class);
            var employmentRoot = employmentSubquery.from(UserEmployment.class);
            employmentSubquery.select(employmentRoot.get("id"));
            List<Predicate> employmentPredicates = new ArrayList<>();
            employmentPredicates.add(cb.equal(employmentRoot.get("user"), userJoin));
            employmentPredicates.add(cb.equal(employmentRoot.get("employmentStatus"), StatusConstants.ACTIVE));

            if (department != null && !department.isBlank()) {
                employmentPredicates.add(cb.equal(employmentRoot.get("department"), department));
            }
            if (designation != null && !designation.isBlank()) {
                employmentPredicates.add(cb.equal(employmentRoot.get("designation"), designation));
            }
            employmentSubquery.where(employmentPredicates.toArray(new Predicate[0]));
            predicates.add(cb.exists(employmentSubquery));

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("employeeCode")), pattern),
                        cb.like(cb.lower(root.get("fullName")), pattern),
                        cb.like(cb.lower(userJoin.get("email")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<UserProfile> activeHrAccounts() {
        return (root, query, cb) -> {
            Join<UserProfile, User> userJoin = root.join("user");
            return cb.and(
                    cb.equal(userJoin.get("role"), RoleConstants.HR),
                    cb.equal(userJoin.get("status"), StatusConstants.ACTIVE)
            );
        };
    }
}
