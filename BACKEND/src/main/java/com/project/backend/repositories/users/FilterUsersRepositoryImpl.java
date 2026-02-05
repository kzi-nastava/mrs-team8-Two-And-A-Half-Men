package com.project.backend.repositories.users;

import com.project.backend.DTO.users.UserFilterDTO;
import com.project.backend.DTO.users.UserListDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FilterUsersRepositoryImpl implements FilterUsersRepository{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<UserListDTO> findAllWithFilters(UserFilterDTO filters, Pageable pageable) {
        // Build the query string dynamically
        String jpql = buildJpqlQuery(filters, pageable.getSort());

        // Create typed query
        TypedQuery<UserListDTO> query = entityManager.createQuery(jpql, UserListDTO.class);

        // Set parameters
        setQueryParameters(query, filters);

        // Apply pagination
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Execute query
        List<UserListDTO> results = query.getResultList();

        // Get total count
        long total = getCount(filters);

        return new PageImpl<>(results, pageable, total);
    }

    private String buildJpqlQuery(UserFilterDTO filters, Sort sort) {
        StringBuilder jpql = new StringBuilder();

        // SELECT clause with constructor expression
        jpql.append("SELECT new com.project.backend.DTO.users.UserListDTO(u, ur) ");
        jpql.append("FROM AppUser u ");
        jpql.append("LEFT JOIN UpdateRequest ur ON u.id = ur.driver.id ");

        // WHERE clause
        List<String> conditions = buildWhereConditions(filters);
        if (!conditions.isEmpty()) {
            jpql.append("WHERE ");
            jpql.append(String.join(" AND ", conditions));
            jpql.append(" ");
        }

        // ORDER BY clause
        String orderBy = buildOrderByClause(sort);
        if (!orderBy.isEmpty()) {
            jpql.append(orderBy);
        }

        return jpql.toString();
    }

    private List<String> buildWhereConditions(UserFilterDTO filters) {
        List<String> conditions = new ArrayList<>();

        if (filters.getId() != null) {
            conditions.add("u.id = :id");
        }

        if (filters.getEmail() != null && !filters.getEmail().isEmpty()) {
            conditions.add("LOWER(u.email) LIKE LOWER(:email)");
        }

        if (filters.getFirstName() != null && !filters.getFirstName().isEmpty()) {
            conditions.add("LOWER(u.firstName) LIKE LOWER(:firstName)");
        }

        if (filters.getLastName() != null && !filters.getLastName().isEmpty()) {
            conditions.add("LOWER(u.lastName) LIKE LOWER(:lastName)");
        }

        if (filters.getIsBlocked() != null) {
            conditions.add("u.isBlocked = :isBlocked");
        }

        if (filters.getRole() != null) {
            conditions.add("TYPE(u) = :roleType");
        }

        if (filters.getDriverStatus() != null) {
            conditions.add("u.driverStatus = :driverStatus");
        }

        if (filters.getHasPendingRequests() != null) {
            if (filters.getHasPendingRequests()) {
                conditions.add("ur.id IS NOT NULL");
            } else {
                conditions.add("ur.id IS NULL");
            }
        }

        return conditions;
    }

    private String buildOrderByClause(Sort sort) {
        if (sort.isUnsorted()) {
            return "";
        }

        List<String> orderByClauses = new ArrayList<>();

        for (Sort.Order order : sort) {
            String property = order.getProperty();
            String direction = order.getDirection().name();

            String clause = switch (property) {
                case "id" -> "u.id " + direction;
                case "firstName" -> "u.firstName " + direction;
                case "lastName" -> "u.lastName " + direction;
                case "email" -> "u.email " + direction;
                case "isBlocked" -> "u.isBlocked " + direction;
                case "driverStatus" -> "u.driverStatus " + direction;
                case "hasPendingRequests" -> "CASE WHEN ur.id IS NOT NULL THEN 1 ELSE 0 END " + direction;
                case "role" -> "TYPE(u) " + direction;
                default -> null;
            };

            if (clause != null) {
                orderByClauses.add(clause);
            }
        }

        if (orderByClauses.isEmpty()) {
            return "";
        }

        return "ORDER BY " + String.join(", ", orderByClauses);
    }

    private void setQueryParameters(TypedQuery<?> query, UserFilterDTO filters) {
        if (filters.getId() != null) {
            query.setParameter("id", filters.getId());
        }

        if (filters.getEmail() != null && !filters.getEmail().isEmpty()) {
            query.setParameter("email", "%" + filters.getEmail() + "%");
        }

        if (filters.getFirstName() != null && !filters.getFirstName().isEmpty()) {
            query.setParameter("firstName", "%" + filters.getFirstName() + "%");
        }

        if (filters.getLastName() != null && !filters.getLastName().isEmpty()) {
            query.setParameter("lastName", "%" + filters.getLastName() + "%");
        }

        if (filters.getIsBlocked() != null) {
            query.setParameter("isBlocked", filters.getIsBlocked());
        }

        if (filters.getRole() != null) {
            query.setParameter("roleType", getRoleClass(filters.getRole()));
        }

        if (filters.getDriverStatus() != null) {
            query.setParameter("driverStatus", filters.getDriverStatus());
        }
    }

    private long getCount(UserFilterDTO filters) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT COUNT(u) ");
        jpql.append("FROM AppUser u ");
        jpql.append("LEFT JOIN UpdateRequest ur ON u.id = ur.driver.id ");

        List<String> conditions = buildWhereConditions(filters);
        if (!conditions.isEmpty()) {
            jpql.append("WHERE ");
            jpql.append(String.join(" AND ", conditions));
        }

        TypedQuery<Long> countQuery = entityManager.createQuery(jpql.toString(), Long.class);
        setQueryParameters(countQuery, filters);

        return countQuery.getSingleResult();
    }

    private Class<?> getRoleClass(com.project.backend.models.enums.UserRole role) {
        return switch (role) {
            case DRIVER -> com.project.backend.models.Driver.class;
            case ADMIN -> com.project.backend.models.Admin.class;
            case CUSTOMER -> com.project.backend.models.Customer.class;
            default -> com.project.backend.models.AppUser.class;
        };
    }
}
