package com.project.backend.DTO.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPageableDTO {
        private Integer page = 0;
        private Integer size = 10;
        private String sortBy = "id";
        private String sortDirection = "ASC";

        public Pageable toPageable() {
            this.setNullsToDefaults();
            Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            return PageRequest.of(page, size, Sort.by(direction, sortBy));
        }

        private void setNullsToDefaults() {
            if (page == null) page = 0;
            if (size == null) size = 10;
            if (sortBy == null || sortBy.isBlank()) sortBy = "id";
            if (sortDirection == null || sortDirection.isBlank()) sortDirection = "ASC";
        }
}
