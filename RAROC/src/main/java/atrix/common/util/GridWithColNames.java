/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

/**
 *
 * @author vaio
 */
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vaio
 */
@XmlRootElement
public class GridWithColNames<T> {

    private List<T> rows;
    private Map<String,Object> userdata;
    private int page;
    private int max;
    private int total;       

    public GridWithColNames() {
    }

    public GridWithColNames(List<T> rows, Map<String,Object> userdata, int page, int max, int total) {
        this.rows = rows;
        this.userdata = userdata;
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

    public Object getUserdata() {
        return userdata;
    }

    public void setUserdata(Map<String,Object> userdata) {
        this.userdata = userdata;
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