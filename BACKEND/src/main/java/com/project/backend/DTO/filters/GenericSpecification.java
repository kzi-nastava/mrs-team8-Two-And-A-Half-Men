package com.project.backend.DTO.filters;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class GenericSpecification<T> {

    public static <T> Specification<T> fieldEquals(String field, Object value) {
        return (root, query, cb) ->
                value == null ? null : cb.equal(root.get(field), value);
    }

    public static <T> Specification<T> fieldLike(String field, String value) {
        return (root, query, cb) ->
                value == null ? null : cb.like(
                        cb.lower(root.get(field)),
                        "%" + value.toLowerCase() + "%"
                );
    }

    public static <T> Specification<T> fieldGreaterThan(String field, Comparable value) {
        return (root, query, cb) ->
                value == null ? null : cb.greaterThanOrEqualTo(root.get(field), value);
    }

    public static <T> Specification<T> fieldLessThan(String field, Comparable value) {
        return (root, query, cb) ->
                value == null ? null : cb.lessThanOrEqualTo(root.get(field), value);
    }

    public static <T> Specification<T> fieldIn(String field, List<?> values) {
        return (root, query, cb) ->
                values == null || values.isEmpty() ? null : root.get(field).in(values);
    }
}
