/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

import java.util.List;

/**
 *
 * @author vaio
 * @param <T>
 */

public class GridPage<T> {

    private List<T> rows;
    private int page;
    private int max;
    private int total;

    public GridPage() {
    }

    public GridPage(List<T> rows, int page, int max, int total) {
        this.rows = rows;
        this.page = page;
        this.max = max;
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}