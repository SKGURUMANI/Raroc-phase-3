/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.model;

/**
 *
 * @author vaio
 */
public class QueryBuilderModel {
 
    private String condition;
    private String regex;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }        
    
}
