package br.gov.mme.auth.util;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class CustomPageable {
    private int pageNumber = 0;
    private int pageSize = 10;
    private String sortBy = "id";
    private String sortDirection = "asc";

    public CustomPageable() {
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return this.sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Pageable toPageable() {
        if (this.pageSize < 1) {
            this.pageSize = 10;
        }

        if (this.pageNumber < 0) {
            this.pageNumber = 0;
        }

        Sort sort = Sort.by(Direction.fromString(this.sortDirection), new String[]{this.sortBy});
        return PageRequest.of(this.pageNumber, this.pageSize, sort);
    }
}
