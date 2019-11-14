/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.model;

/**
 *
 * @author vaio
 */
public class SecurityModel {
    
    private String username;
    private String password;
    private boolean enabled;
    private boolean passExpired;
    private boolean active;
    private String homepage;
    private int sessionTime;
    private String lastLogin;
    private String locale;
    private int unit;
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPassExpired() {
        return passExpired;
    }

    public void setPassExpired(boolean passExpired) {
        this.passExpired = passExpired;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }    
    
    public int getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(int sessionTime) {
        this.sessionTime = sessionTime;
    }    
    
    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }    

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }        
    
}