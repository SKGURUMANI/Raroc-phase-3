/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.service;

import atrix.common.model.QueryBuilderModel;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author vaio
 */
@Service("queryBuilder")
public class QueryBuilderService {

    public QueryBuilderModel SearchAnd(String searchOper, String searchField, String searchString,
            List<String> columns) {
        String condition = "AND 1 = ?", regex = "1";
        QueryBuilderModel obj = new QueryBuilderModel();
        if (searchOper != null && columns.contains(searchField)) {
            if (searchField.substring(0, 2).equals("d_")) {
                searchField = "to_char(" + searchField + ", 'yyyy-mm-dd hh:mm:ss')";
            }
            if (searchOper.equals("cn")) {
                condition = "AND upper(" + searchField + ") like upper(?)";
                regex = "%" + searchString + "%";
            } else if (searchOper.equals("nc")) {
                condition = "AND upper(" + searchField + ") not like upper(?)";
                regex = "%" + searchString + "%";
            } else if (searchOper.equals("bw")) {
                condition = "AND upper(" + searchField + ") like upper(?)";
                regex = searchString + "%";
            } else if (searchOper.equals("ew")) {
                condition = "AND upper(" + searchField + ") like upper(?)";
                regex = "%" + searchString;
            } else if (searchOper.equals("eq")) {
                if (searchField.substring(0, 2).equals("n_")) {
                    condition = "AND " + searchField + " = ?";
                } else {
                    condition = "AND upper(" + searchField + ") = upper(?)";
                }
                regex = searchString;
            } else if (searchOper.equals("ne")) {
                if (searchField.substring(0, 2).equals("n_")) {
                    condition = "AND " + searchField + " <> ?";
                } else {
                    condition = "AND upper(" + searchField + ") <> upper(?)";
                }
                regex = searchString;
            } else if (searchOper.equals("gt")) {
                condition = "AND " + searchField + " > ?";
                regex = searchString;
            } else if (searchOper.equals("ge")) {
                condition = "AND " + searchField + " >= ?";
                regex = searchString;
            } else if (searchOper.equals("lt")) {
                condition = "AND " + searchField + " < ?";
                regex = searchString;
            } else if (searchOper.equals("le")) {
                condition = "AND " + searchField + " <= ?";
                regex = searchString;
            } else if (searchOper.equals("nu")) {
                condition = "AND " + searchField + " is null AND 1 = ?";
                regex = "1";
            } else if (searchOper.equals("nn")) {
                condition = "AND " + searchField + " is not null AND 1 = ?";
                regex = "1";
            }
        }
        obj.setCondition(condition);
        obj.setRegex(regex);
        return obj;
    }

    public QueryBuilderModel SearchWhere(String searchOper, String searchField, String searchString,
            List<String> columns) {
        String condition = "WHERE 1 = ?", regex = "1";
        QueryBuilderModel obj = new QueryBuilderModel();
        if (searchOper != null && columns.contains(searchField)) {
            if (searchField.substring(0, 2).equals("d_")) {
                searchField = "to_char(" + searchField + ", 'yyyy-mm-dd hh:mm:ss')";
            }
            if (searchOper.equals("cn")) {
                condition = "WHERE upper(" + searchField + ") like upper(?)";
                regex = "%" + searchString + "%";
            } else if (searchOper.equals("nc")) {
                condition = "WHERE upper(" + searchField + ") not like upper(?)";
                regex = "%" + searchString + "%";
            } else if (searchOper.equals("bw")) {
                condition = "WHERE upper(" + searchField + ") like upper(?)";
                regex = searchString + "%";
            } else if (searchOper.equals("ew")) {
                condition = "WHERE upper(" + searchField + ") like upper(?)";
                regex = "%" + searchString;
            } else if (searchOper.equals("eq")) {
                if (searchField.substring(0, 2).equals("n_")) {
                    condition = "WHERE " + searchField + " = ?";
                } else {
                    condition = "WHERE upper(" + searchField + ") = upper(?)";
                }
                regex = searchString;
            } else if (searchOper.equals("ne")) {
                if (searchField.substring(0, 2).equals("n_")) {
                    condition = "WHERE " + searchField + " <> ?";
                } else {
                    condition = "WHERE upper(" + searchField + ") <> upper(?)";
                }
                regex = searchString;
            } else if (searchOper.equals("gt")) {
                condition = "WHERE " + searchField + " > ?";
                regex = searchString;
            } else if (searchOper.equals("ge")) {
                condition = "WHERE " + searchField + " >= ?";
                regex = searchString;
            } else if (searchOper.equals("lt")) {
                condition = "WHERE " + searchField + " < ?";
                regex = searchString;
            } else if (searchOper.equals("le")) {
                condition = "WHERE " + searchField + " <= ?";
                regex = searchString;
            } else if (searchOper.equals("nu")) {
                condition = "WHERE " + searchField + " is null AND 1 = ?";
                regex = "1";
            } else if (searchOper.equals("nn")) {
                condition = "WHERE " + searchField + " is not null AND 1 = ?";
                regex = "1";
            }
        }        
        obj.setCondition(condition);
        obj.setRegex(regex);
        return obj;
    }
}