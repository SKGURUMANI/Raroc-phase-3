/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.model;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;

/**
 *
 * @author vaio
 */
@ScriptAssert(lang = "javascript", script = "_this.npassword.equals(_this.cpassword)", message = "{password.notEqual}")
public class ChangePassModel {

    @NotEmpty(message = "{password.o.empty}")
    private String opassword;
    @NotEmpty(message = "{password.n.empty}")
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-zA-Z])(?=.*[~!@#$%^]).{8,20})", message = "{password.incorrect.pattern}")
    private String npassword;
    @NotEmpty(message = "{password.c.empty}")
    private String cpassword;
    private String opassword1;
    private String opassword2;
    private String opassword3;

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }

    public String getNpassword() {
        return npassword;
    }

    public void setNpassword(String npassword) {
        this.npassword = npassword;
    }

    public String getOpassword() {
        return opassword;
    }

    public void setOpassword(String opassword) {
        this.opassword = opassword;
    }

    public String getOpassword1() {
        return opassword1;
    }

    public void setOpassword1(String opassword1) {
        this.opassword1 = opassword1;
    }    
    
    public String getOpassword2() {
        return opassword2;
    }

    public void setOpassword2(String opassword2) {
        this.opassword2 = opassword2;
    }

    public String getOpassword3() {
        return opassword3;
    }

    public void setOpassword3(String opassword3) {
        this.opassword3 = opassword3;
    }        
}