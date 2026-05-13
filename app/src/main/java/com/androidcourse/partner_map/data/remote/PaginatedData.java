package com.androidcourse.partner_map.data.remote;

import java.util.List;

public class PaginatedData<T> {
    private long total;
    private int page;
    private int size;
    private List<T> items;

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public List<T> getItems() { return items; }
    public void setItems(List<T> items) { this.items = items; }
}
