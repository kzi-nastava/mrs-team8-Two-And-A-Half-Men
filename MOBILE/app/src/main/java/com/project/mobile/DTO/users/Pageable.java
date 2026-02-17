package com.project.mobile.DTO.users;

public class Pageable {
    private long offset;
    private int pageNumber;
    private int pageSize;
    private boolean paged;
    private PageableSort sort;
    private boolean unpaged;

    public Pageable() {}

    public long getOffset() { return offset; }
    public void setOffset(long offset) { this.offset = offset; }

    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public boolean isPaged() { return paged; }
    public void setPaged(boolean paged) { this.paged = paged; }

    public PageableSort getSort() { return sort; }
    public void setSort(PageableSort sort) { this.sort = sort; }

    public boolean isUnpaged() { return unpaged; }
    public void setUnpaged(boolean unpaged) { this.unpaged = unpaged; }
}
