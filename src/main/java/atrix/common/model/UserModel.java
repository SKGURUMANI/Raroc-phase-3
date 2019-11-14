/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author vaio
 */
public class UserModel {

    @NotEmpty
    @Length(max = 20)
    private String userId;
    @NotEmpty
    @Length(max = 50)
    private String userName;
    @NotEmpty
    @Pattern(regexp = "((?=.*\\d)(?=.*[a-zA-Z])(?=.*[~!@#$%^]).{8,20})", message = "{password.incorrect.pattern}")
    private String password;
    @NotNull
    private int enabled;
    @NotNull
    private int active;
    @NotEmpty
    private String roles;
    @NotEmpty
    @Length(max = 50)
    private String homePage;
    @NotEmpty
    private String locale;
    @Length(max = 100)
    private String email;
    @Length(max = 200)
    private String address;
    @Length(max = 20)
    private String phone;
    @NotEmpty
    private String sessionTime;
    @NotEmpty
    private String failedAttempt;
    @NotEmpty
    private String passExpiry;
    private String passChangeDate;
    private String id;
    private String activeStr;
    private int unit;
    private String manager;
    private String department;

    public String getActiveStr() {
        return activeStr;
    }

    public void setActiveStr(String activeStr) {
        this.activeStr = activeStr;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPassChangeDate() {
        return passChangeDate;
    }

    public void setPassChangeDate(String passChangeDate) {
        this.passChangeDate = passChangeDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFailedAttempt() {
        return failedAttempt;
    }

    public void setFailedAttempt(String failedAttempt) {
        this.failedAttempt = failedAttempt;
    }

    public String getPassExpiry() {
        return passExpiry;
    }

    public void setPassExpiry(String passExpiry) {
        this.passExpiry = passExpiry;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }
    
    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }        
        
}