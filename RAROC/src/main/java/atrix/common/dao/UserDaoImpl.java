/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.dao;

import atrix.common.model.ChangePassModel;
import atrix.common.model.QueryBuilderModel;
import atrix.common.model.UserModel;
import atrix.common.service.FormatterService;
import atrix.common.service.QueryBuilderService;
import atrix.common.util.GridPage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vaio
 */
@Repository("userDao")
public class UserDaoImpl extends JdbcDaoSupport implements UserDao {

    @Autowired
    UserDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;
    
    @Override
    public int changePassword(String userid, String newPassword) {
        String query = "UPDATE CNFG_USERS SET v_password = ?, v_password2 = v_password, v_password3 = v_password2,"
                + " d_password_changed_date = sysdate"                
                + " WHERE v_user_id = ?";
        int result =  getJdbcTemplate().update(query, new Object[]{newPassword, userid});
        return result;
    }

    @Override
    public ChangePassModel getPasswords(String userid) {
        String sql = "SELECT v_password,"
                + " nvl(v_password2,'$2a$10$MQZNzrxmhqjcOTJD2B8wDeOXJGHSzj2/8QKHlWwxgF8490qpIDUMC'),"
                + " nvl(v_password3,'$2a$10$MQZNzrxmhqjcOTJD2B8wDeOXJGHSzj2/8QKHlWwxgF8490qpIDUMC')"
                + " FROM CNFG_USERS WHERE v_user_id = ?";
        ChangePassModel cpm = getJdbcTemplate().queryForObject(
                    sql, new Object[]{userid}, new RowMapper<ChangePassModel>() {
                @Override
                public ChangePassModel mapRow(ResultSet rs, int i) throws SQLException {
                    ChangePassModel cpm = new ChangePassModel();
                    cpm.setOpassword1(rs.getString(1));
                    cpm.setOpassword2(rs.getString(2));                    
                    cpm.setOpassword3(rs.getString(3));
                    return cpm;    
                }
            });        
        return cpm;
    }

    @Override
    public List getHomePages() {
        String query = "SELECT v_value FROM CNFG_MASTER WHERE v_type = ? ORDER BY decode(v_value,'index',1,2)";        
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{"home"});
        List<String> options = new ArrayList<String>();
        for (Map row : rows) {          
            options.add(fmt.ToString(row.get("v_value")));
        }
        return options;
    }    

    @Override
    public List getLocales() {
        String query = "SELECT v_value FROM CNFG_MASTER WHERE v_type = ? ORDER BY decode(v_value,'en',1,2)";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{"locale"});
        List<String> options = new ArrayList<String>();
        for (Map row : rows) {          
            options.add(fmt.ToString(row.get("v_value")));
        }
        return options;
    }
    
    @Override
    public UserModel getCurrentSettings(String userid) {
        String sql = "SELECT nvl(v_home_page,'welcome') v_home, nvl(v_locale,'en') v_locale, v_email,"
                + " v_address, v_phone, n_currency_unit"
                + " FROM CNFG_USERS WHERE v_user_id = ?";
        UserModel usr;
        try {
            usr = getJdbcTemplate().queryForObject(
                    sql, new Object[]{userid}, new RowMapper<UserModel>() {
                @Override
                public UserModel mapRow(ResultSet rs, int i) throws SQLException {
                    UserModel usr = new UserModel();
                    usr.setHomePage(rs.getString(1));
                    usr.setLocale(rs.getString(2));
                    usr.setEmail(rs.getString(3));
                    usr.setAddress(rs.getString(4));
                    usr.setPhone(rs.getString(5));
                    usr.setUnit(rs.getInt(6));
                    return usr;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            usr = null;
        } catch (IncorrectResultSizeDataAccessException e) {
            usr = null;
        }
        return usr;
    }

    @Override
    public int SaveSettings(UserModel usr, String userid) {
        String query = "UPDATE CNFG_USERS SET v_home_page = ?, v_locale = ?, v_email = ?, v_address = ?, "
                + "v_phone = ?, n_currency_unit = ? "
                + "WHERE v_user_id = ?";
        int ret = getJdbcTemplate().update(query, new Object[]{usr.getHomePage(), usr.getLocale(), usr.getEmail(),
            usr.getAddress(), usr.getPhone(), usr.getUnit(), userid});        
        return ret;
    }
    
    @Override
    public int checkUsrIDAvailability(String userid) {
        String query = "SELECT count(*) FROM CNFG_USERS WHERE v_user_id = ?";        
        return getJdbcTemplate().queryForObject(query, new Object[]{userid},Integer.class);
    }        
    
    @Override
    public void CreateUser(UserModel usr) {
        String query = "INSERT INTO CNFG_USERS (v_user_id, v_username, v_password, n_enabled, n_account_active, v_home_page, "
                + "v_locale, v_email, v_address, v_phone, d_password_changed_date, n_session_time, n_allowed_attempt, "
                + "n_pass_expiry_days, n_currency_unit, v_department) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, to_date(?,'dd-mm-yyyy'), ?, ?, ?, ?, ?)";
        getJdbcTemplate().update(query, new Object[]{usr.getUserId(), usr.getUserName(), "$2a$10$uwAaWvAg9czfRT4dDMSrfOhgnapE9k4rx4qyDb7zIYLpC1iGjg7Kq", 
            usr.getEnabled(), usr.getActive(), usr.getHomePage(), usr.getLocale(), usr.getEmail(), usr.getAddress(), 
            usr.getPhone(), usr.getPassChangeDate(), usr.getSessionTime(), usr.getFailedAttempt(), usr.getPassExpiry(), 
            usr.getUnit(), usr.getDepartment()});
    }
    
    @Override
    public void ModifyUser(UserModel usr) {
        String query = "UPDATE CNFG_USERS SET v_username = ?, n_enabled = ?, n_account_active = ?, v_home_page = ?, "
                + "v_locale = ?, v_email = ?, v_address = ?, v_phone = ?, n_session_time = ?, n_allowed_attempt = ?, "
                + "n_pass_expiry_days = ?, n_currency_unit = ?, v_department = ? "
                + "WHERE v_user_id = ?";
        getJdbcTemplate().update(query, new Object[]{usr.getUserName(), usr.getEnabled(), usr.getActive(), usr.getHomePage(), 
            usr.getLocale(), usr.getEmail(), usr.getAddress(), usr.getPhone(), usr.getSessionTime(), usr.getFailedAttempt(),
            usr.getPassExpiry(), usr.getUnit(), usr.getDepartment(), usr.getUserId()});
    }
    
    @Override
    public List<String> getUserRoles(String userid) {
        String sql = "SELECT v_authority FROM CNFG_USER_ROLES WHERE v_user_id = ?";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, new Object[]{userid});
        List<String> l = new ArrayList<String>();
        for (Map row : rows) {
            l.add(fmt.ToString(row.get("v_authority")));
        }
        return l;
    }
    
    @Override
    public void CreateUserRole(String userid, String role) {
        String query = "INSERT INTO CNFG_USER_ROLES (n_user_role_id, v_user_id, v_authority, v_module) "
                + "VALUES (role_id_seq.nextval, ?, ?, 'EVC')";
        getJdbcTemplate().update(query, new Object[]{userid, role});
    }
    
    @Override
    public void DeleteUserRole(String userid, String role) {
        String query = "DELETE FROM CNFG_USER_ROLES WHERE v_user_id = ? AND v_authority = ?";
        getJdbcTemplate().update(query, new Object[]{userid, role});
    }
    
    @Override
    public void CreateUserManager(String userid, String manager) {
        String query = "INSERT INTO CNFG_USER_MANAGER_MAPPING (v_user_id, v_manager_id) VALUES (?, trim(?))";
        getJdbcTemplate().update(query, new Object[]{userid, manager});
    }
    
    @Override
    public void DeleteUserManager(String userid, String manager) {
        String query = "DELETE FROM CNFG_USER_MANAGER_MAPPING WHERE v_user_id = ? AND v_manager_id = trim(?)";
        getJdbcTemplate().update(query, new Object[]{userid, manager});
    }
    
    @Override
    public GridPage<UserModel> listUsers(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_user_id", "v_username",
                "v_email", "v_phone"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_user_id";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM CNFG_USERS " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);        
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_user_id, v_username, v_email, v_phone, case when n_account_active = 1 then 'Active' "
                + "else 'Inactive' end active_flag "
                + "FROM CNFG_USERS "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<UserModel> userattrs = new ArrayList<UserModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), 
            endIdx, startIdx});
        for (Map row : rows) {
            UserModel userattr = new UserModel();
            userattr.setId(fmt.ToString(row.get("rnum")));
            userattr.setUserId(fmt.ToString(row.get("v_user_id")));
            userattr.setUserName(fmt.ToString(row.get("v_username")));
            userattr.setEmail(fmt.ToString(row.get("v_email")));
            userattr.setPhone(fmt.ToString(row.get("v_phone")));
            userattr.setActiveStr(fmt.ToString(row.get("active_flag")));            
            userattrs.add(userattr);
        }
        return new GridPage<UserModel>(userattrs, page, max, rowCount);
    }
    
    @Override
    public UserModel getUserDetails(String userid) {
        String sql = "SELECT v_username, n_enabled, n_account_active, v_locale, v_email, v_address, v_phone, "
                + "n_session_time, n_allowed_attempt, n_pass_expiry_days, n_currency_unit, v_department "
                + "FROM CNFG_USERS WHERE v_user_id = ?";
        UserModel um = getJdbcTemplate().queryForObject(
                    sql, new Object[]{userid}, new RowMapper<UserModel>() {
                @Override
                public UserModel mapRow(ResultSet rs, int i) throws SQLException {
                    UserModel um = new UserModel();                    
                    um.setUserName(rs.getString(1));
                    um.setEnabled(rs.getInt(2));                    
                    um.setActive(rs.getInt(3));
                    um.setLocale(rs.getString(4));
                    um.setEmail(rs.getString(5));
                    um.setAddress(rs.getString(6));
                    um.setPhone(rs.getString(7));
                    um.setSessionTime(rs.getString(8));
                    um.setFailedAttempt(rs.getString(9));
                    um.setPassExpiry(rs.getString(10));
                    um.setUnit(rs.getInt(11));
                    um.setDepartment(rs.getString(12));
                    return um;
                }
            });                
        um.setUserId(userid);
        /*
        sql = "SELECT v_manager_id FROM CNFG_USER_MANAGER_MAPPING WHERE v_user_id = ?";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, new Object[]{userid});
        List<String> l = new ArrayList<String>();
        for (Map row : rows) {
            l.add(fmt.ToString(row.get("v_manager_id")));
        }
        um.setManager(StringUtils.join(l, ','));
        */
        return um;
    }        
    
}