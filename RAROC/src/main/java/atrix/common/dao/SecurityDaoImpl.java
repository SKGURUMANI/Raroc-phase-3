/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.dao;

import atrix.common.model.*;
import atrix.common.service.FormatterService;
import atrix.common.service.QueryBuilderService;
import atrix.common.util.GridPage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vaio
 */
@Repository("securityDao")
public class SecurityDaoImpl extends JdbcDaoSupport implements SecurityDao {

    private @Value("${jdbc.username}")
    String schemaName;

    @Autowired
    SecurityDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;
    @Value("${user.dormantDays}")
    private String dormantDays;

    @Override
    public SecurityModel getUserByUsername(String userid) {
        String sql = "UPDATE cnfg_users SET n_account_active = 0, d_last_login = sysdate "
                + "WHERE v_user_id = ? AND (sysdate - nvl(d_last_login,sysdate)) > ?";
        getJdbcTemplate().update(sql, new Object[]{userid, dormantDays});
        sql = "SELECT v_user_id, v_password, n_enabled,"
                + " case when (sysdate - nvl(d_password_changed_date,sysdate)) > n_pass_expiry_days"
                + " then 0 else 1 end n_pass_expired, n_account_active "
                + " FROM cnfg_users WHERE v_user_id = ?";
        SecurityModel sec;
        try {
            sec = getJdbcTemplate().queryForObject(
                    sql, new Object[]{userid}, new RowMapper<SecurityModel>() {

                @Override
                public SecurityModel mapRow(ResultSet rs, int i) throws SQLException {
                    SecurityModel sec = new SecurityModel();
                    sec.setUsername(rs.getString(1));
                    sec.setPassword(rs.getString(2));
                    sec.setEnabled(rs.getBoolean(3));
                    sec.setPassExpired(rs.getBoolean(4));
                    sec.setActive(rs.getBoolean(5));
                    return sec;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            sec = null;
        } catch (IncorrectResultSizeDataAccessException e) {
            sec = null;
        }
        return sec;
    }

    @Override
    public List<GrantedAuthority> getAuthorityByUsernameJDBC(String userid) {
        List<Map<String, Object>> rows;
        String sql = "SELECT ur.v_authority"
                + " FROM cnfg_users u, cnfg_user_roles ur"
                + " WHERE u.v_user_id = ur.v_user_id"
                + " AND   u.v_user_id = ?"
                + " AND   u.d_password_changed_date IS NOT NULL"
                + " UNION ALL"
                + " SELECT 'ROLE_PASSWORD_CHANGE' authority"
                + " FROM cnfg_users"
                + " WHERE v_user_id = ?"
                + " AND   d_password_changed_date IS NULL";
        rows = getJdbcTemplate().queryForList(sql, new Object[]{userid, userid});
        List<GrantedAuthority> lauth = new ArrayList<GrantedAuthority>();
        for (Map row : rows) {
            lauth.add(new SimpleGrantedAuthority((String) row.get("v_authority")));
        }
        return lauth;
    }

    @Override
    public List<GrantedAuthority> getAuthorityByUsernameLDAP(String userid) {
        List<Map<String, Object>> rows;
        String sql = "SELECT ur.v_authority"
                + " FROM cnfg_users u, cnfg_user_roles ur"
                + " WHERE u.v_user_id = ur.v_user_id"
                + " AND   u.v_user_id = ?";
        rows = getJdbcTemplate().queryForList(sql, new Object[]{userid});
        List<GrantedAuthority> lauth = new ArrayList<GrantedAuthority>();
        for (Map row : rows) {
            lauth.add(new SimpleGrantedAuthority((String) row.get("v_authority")));
        }
        return lauth;
    }

    @Override
    public void reportFailedLogin(String userid) {
        String query = "UPDATE CNFG_USERS SET n_failed_attempt = nvl(n_failed_attempt,0) + 1,"
                + " n_enabled = case when nvl(n_failed_attempt,0) + 1 >= nvl(n_allowed_attempt,3) then 0 else 1 end"
                + " WHERE v_user_id = ?";
        getJdbcTemplate().update(query, new Object[]{userid});
    }

    @Override
    public SecurityModel getPreferences(String userid) {
        String sql = "SELECT substr(v_username,1,18) , nvl(v_home_page,'welcome'), nvl(v_locale,'en'), d_last_login,\n"
                + "                nvl(n_session_time,900), n_currency_unit, (Select LISTAGG(u_role, ',') WITHIN GROUP (ORDER BY N_USER_ROLE_ID) from ( "
                + "                                                           SELECT CASE WHEN V_AUTHORITY = 'ROLE_RAROC_AUTH' THEN 'ROLE_RAROC_AUTH' "
                + "                                                                       WHEN V_AUTHORITY = 'ROLE_ADMIN' THEN 'ROLE_ADMIN' "
                + "                                                                       WHEN V_AUTHORITY = 'ROLE_USER' THEN 'ROLE_USER' "
                + "                                                                       WHEN V_AUTHORITY = 'ROLE_RAROC_CORP' THEN 'ROLE_RAROC_CORP' end u_role, N_USER_ROLE_ID "
                + "                                                           FROM CNFG_USER_ROLES WHERE V_USER_ID = ? ORDER BY N_USER_ROLE_ID)) u_roles "
                + "                FROM CNFG_USERS WHERE v_user_id = ? ";
        SecurityModel sec;
        try {
            sec = getJdbcTemplate().queryForObject(
                    sql, new Object[]{userid,userid}, new RowMapper<SecurityModel>() {

                @Override
                public SecurityModel mapRow(ResultSet rs, int i) throws SQLException {
                    SecurityModel sec = new SecurityModel();
                    sec.setUsername(rs.getString(1));
                    sec.setHomepage(rs.getString(2));
                    sec.setLocale(rs.getString(3));
                    sec.setLastLogin(rs.getString(4));
                    sec.setSessionTime(rs.getInt(5));
                    sec.setUnit(rs.getInt(6));
                    sec.setRole(rs.getString(7));
                    return sec;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            sec = null;
        } catch (IncorrectResultSizeDataAccessException e) {
            sec = null;
        }
        return sec;
    }

    @Override
    public void reportLoginSuccess(String userid) {
        String query = "UPDATE CNFG_USERS SET n_failed_attempt = 0, d_last_login = sysdate"
                + " WHERE substr(v_user_id,1,30) = ?";
        getJdbcTemplate().update(query, new Object[]{userid});
    }

    @Override
    public void insertSysAudit(String action, String userid, String sessionid, String ip, String remarks) {
        String query = "INSERT INTO AUDIT_SYS_LOG (n_audit_cd, v_action, v_user_id, v_session_id, v_ip_address,"
                + " t_time, v_action_result)"
                + " VALUES(aud_syslog_seq.nextval, ?, substr(?,1,30), ?, ?, systimestamp, ?)";
        getJdbcTemplate().update(query, new Object[]{action, userid, sessionid, ip, remarks});
    }

    @Override
    public GridPage<UserModel> listSysLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String fromDate, String toDate) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("a.v_user_id", "c.v_username",
                "a.v_action", "a.v_action_result", "a.v_ip_address"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "a.t_time";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        try {
            String query = "SELECT count(*) "
                    + "FROM AUDIT_SYS_LOG a LEFT OUTER JOIN CNFG_USERS c ON a.v_user_id = c.v_user_id "
                    + "WHERE to_char(a.t_time,'dd-Mon-yyyy') BETWEEN to_date(nvl(?, '01-Jan-1900'),'dd-Mon-yyyy') "
                    + "AND to_date(nvl(?, '01-Jan-9900'),'dd-Mon-yyyy') "
                    + qObj.getCondition();
            int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{fromDate, toDate, qObj.getRegex()}, Integer.class);
            final int startIdx = ((page - 1) * max) + 1;
            final int endIdx = Math.min(startIdx + max, rowCount);
            query = "SELECT * FROM "
                    + "(SELECT a.*, rownum rnum FROM "
                    + "(SELECT a.v_user_id, c.v_username, a.v_action, a.v_action_result, a.v_ip_address, a.t_time "
                    + "FROM AUDIT_SYS_LOG a LEFT OUTER JOIN CNFG_USERS c ON a.v_user_id = c.v_user_id "
                    + "WHERE to_char(a.t_time,'dd-Mon-yyyy') BETWEEN to_date(nvl(?, '01-Jan-1900'),'dd-Mon-yyyy') "
                    + "AND to_date(nvl(?, '01-Jan-9900'),'dd-Mon-yyyy') "
                    + qObj.getCondition() + " "
                    + "ORDER BY " + sidx + " " + sord + ") a "
                    + "WHERE rownum <= ?) WHERE rnum >= ?";
            List<UserModel> userattrs = new ArrayList<UserModel>();
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{fromDate, toDate,
                qObj.getRegex(), endIdx, startIdx});
            for (Map row : rows) {
                UserModel userattr = new UserModel();
                userattr.setId(fmt.ToString(row.get("rnum")));
                userattr.setUserId(fmt.ToString(row.get("v_user_id")));
                userattr.setUserName(fmt.ToString(row.get("v_username")));
                userattr.setRoles(fmt.ToString(row.get("v_action")));
                userattr.setAddress(fmt.ToString(row.get("v_action_result")));
                userattr.setActiveStr(fmt.ToString(row.get("v_ip_address")));
                userattr.setSessionTime(fmt.ToString(row.get("t_time")));
                userattrs.add(userattr);
            }
            return new GridPage<UserModel>(userattrs, page, max, rowCount);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<UserModel> docSysLog(String fromDate, String toDate, String searchField, String searchOper,
            String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("a.v_user_id", "c.v_username",
                "a.v_action", "a.v_action_result", "a.v_ip_address"));
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT a.v_user_id, c.v_username, a.v_action, a.v_action_result, a.v_ip_address, a.t_time "
                + "FROM AUDIT_SYS_LOG a LEFT OUTER JOIN CNFG_USERS c ON a.v_user_id = c.v_user_id "
                + "WHERE to_char(a.t_time,'dd-Mon-yyyy') BETWEEN to_date(nvl(?, '01-Jan-1900'),'dd-Mon-yyyy') "
                + "AND to_date(nvl(?, '01-Jan-9900'),'dd-Mon-yyyy') "
                + qObj.getCondition() + " "
                + "ORDER BY a.t_time desc ";
        List<UserModel> userattrs = new ArrayList<UserModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{fromDate, toDate,
            qObj.getRegex()});
        for (Map row : rows) {
            UserModel userattr = new UserModel();
            userattr.setId(fmt.ToString(row.get("rnum")));
            userattr.setUserId(fmt.ToString(row.get("v_user_id")));
            userattr.setUserName(fmt.ToString(row.get("v_username")));
            userattr.setRoles(fmt.ToString(row.get("v_action")));
            userattr.setAddress(fmt.ToString(row.get("v_action_result")));
            userattr.setActiveStr(fmt.ToString(row.get("v_ip_address")));
            userattr.setSessionTime(fmt.ToString(row.get("t_time")));
            userattrs.add(userattr);
        }
        return userattrs;
    }

    @Override
    public String getAuditSequence() {
        String sql = "SELECT 'AUD'||aud_seq.nextval FROM DUAL";
        String sequence = getJdbcTemplate().queryForObject(sql, String.class);
        return sequence;
    }

    @Override
    public void insertOperAudit(String audcd, String type, String userid, String status, String desc) {
        String query = "INSERT INTO AUDIT_DETAILS (v_audit_cd, v_change_type, v_maker_cd, v_change_status, d_change_dt, "
                + "v_change_description) "
                + "VALUES(?, ?, ?, ?, sysdate, ?)";
        getJdbcTemplate().update(query, new Object[]{audcd, type, userid, status, desc});
    }

    @Override
    public GridPage<TaskMonitorModel> listOpsLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String fromDate, String toDate) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("a.v_change_type", "a.v_maker_cd", "c.v_username",
                "a.v_change_description", "a.v_change_status"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "d_change_dt";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM AUDIT_DETAILS a, CNFG_USERS c "
                + "WHERE to_char(a.d_change_dt,'dd-Mon-yyyy') BETWEEN to_date(nvl(?, '01-Jan-1900'),'dd-Mon-yyyy') "
                + "AND to_date(nvl(?, '01-Jan-9900'),'dd-Mon-yyyy') "
                + "AND a.v_maker_cd = c.v_user_id "
                + "AND a.v_audit_cd <> 'SYS' "
                + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{fromDate, toDate, qObj.getRegex()}, Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT a.d_change_dt, a.v_maker_cd, c.v_username, a.v_change_type, a.v_change_description, "
                + "a.v_change_status "
                + "FROM AUDIT_DETAILS a, CNFG_USERS c "
                + "WHERE to_char(a.d_change_dt,'dd-Mon-yyyy') BETWEEN to_date(nvl(?, '01-Jan-1900'),'dd-Mon-yyyy') "
                + "AND to_date(nvl(?, '01-Jan-9900'),'dd-Mon-yyyy') "
                + "AND a.v_maker_cd = c.v_user_id "
                + "AND   a.v_audit_cd <> 'SYS' "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + " nulls last) a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<TaskMonitorModel> ids = new ArrayList<TaskMonitorModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{fromDate, toDate,
            qObj.getRegex(), endIdx, startIdx});
        for (Map row : rows) {
            TaskMonitorModel id = new TaskMonitorModel();
            id.setId(fmt.ToString(row.get("rnum")));
            id.setStime(fmt.ToString(row.get("d_change_dt")));
            id.setUserid(fmt.ToString(row.get("v_maker_cd")));
            id.setUserName(fmt.ToString(row.get("v_username")));
            id.setTask(fmt.ToString(row.get("v_change_type")));
            id.setRemarks(fmt.ToString(row.get("v_change_description")));
            id.setStatus(fmt.ToString(row.get("v_change_status")));
            ids.add(id);
        }
        return new GridPage<TaskMonitorModel>(ids, page, max, rowCount);
    }

    @Override
    public List<TaskMonitorModel> docOpsLog(String fromDate, String toDate, String searchField, String searchOper,
            String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("a.v_change_type", "a.v_maker_cd", "c.v_username",
                "a.v_change_description", "a.v_change_status"));
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT a.d_change_dt, a.v_maker_cd, c.v_username, a.v_change_type, a.v_change_description, "
                + "a.v_change_status "
                + "FROM AUDIT_DETAILS a, CNFG_USERS c "
                + "WHERE to_char(a.d_change_dt,'dd-Mon-yyyy') BETWEEN to_date(nvl(?, '01-Jan-1900'),'dd-Mon-yyyy') "
                + "AND to_date(nvl(?, '01-Jan-9900'),'dd-Mon-yyyy') "
                + "AND a.v_maker_cd = c.v_user_id "
                + "AND   a.v_audit_cd <> 'SYS' "
                + qObj.getCondition() + " "
                + "ORDER BY a.d_change_dt desc";
        List<TaskMonitorModel> ids = new ArrayList<TaskMonitorModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{fromDate, toDate,
            qObj.getRegex()});
        for (Map row : rows) {
            TaskMonitorModel id = new TaskMonitorModel();
            id.setId(fmt.ToString(row.get("rnum")));
            id.setStime(fmt.ToString(row.get("d_change_dt")));
            id.setUserid(fmt.ToString(row.get("v_maker_cd")));
            id.setUserName(fmt.ToString(row.get("v_username")));
            id.setTask(fmt.ToString(row.get("v_change_type")));
            id.setRemarks(fmt.ToString(row.get("v_change_description")));
            id.setStatus(fmt.ToString(row.get("v_change_status")));
            ids.add(id);
        }
        return ids;
    }

    @Override
    public GridPage<TaskMonitorModel> listPrsLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("V_BATCH_ID", "v_task_name",
                "v_status", "t_start", "t_end", "v_remarks", "v_error_details"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "t_start";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM LOG_PROCESS_FLOW "
                + "WHERE v_task_name NOT IN ('PROC_JOB_EXECUTOR','PROC_DEFRAG') "
                + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()}, Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT V_BATCH_ID v_execution_id, v_task_name, v_status, t_start, t_end, v_remarks, v_error_details "
                + "FROM LOG_PROCESS_FLOW "
                + "WHERE v_task_name NOT IN ('PROC_JOB_EXECUTOR','PROC_DEFRAG') "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + " nulls last) a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<TaskMonitorModel> ids = new ArrayList<TaskMonitorModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(),
            endIdx, startIdx});
        for (Map row : rows) {
            TaskMonitorModel id = new TaskMonitorModel();
            id.setId(fmt.ToString(row.get("rnum")));
            id.setBatchId(fmt.ToString(row.get("v_execution_id")));
            id.setTask(fmt.ToString(row.get("v_task_name")));
            id.setStime(fmt.ToString(row.get("t_start")));
            id.setEtime(fmt.ToString(row.get("t_end")));
            id.setStatus(fmt.ToString(row.get("v_status")));
            id.setRemarks(fmt.ToString(row.get("v_remarks")));
            id.setError(fmt.ToString(row.get("v_error_details")));
            ids.add(id);
        }
        return new GridPage<TaskMonitorModel>(ids, page, max, rowCount);
    }

    @Override
    public GridPage<DefragModel> listDefragTables(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("n_serial_no", "v_schema_name",
                "v_table_name"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "n_serial_no";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchWhere(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM CNFG_DEFRAG_TABLES " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{qObj.getRegex()}, Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT n_serial_no, v_schema_name, v_table_name "
                + "FROM CNFG_DEFRAG_TABLES "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<DefragModel> tables = new ArrayList<DefragModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{qObj.getRegex(), endIdx, startIdx});
        for (Map row : rows) {
            DefragModel tab = new DefragModel();
            tab.setId(fmt.ToString(row.get("n_serial_no")));
            tab.setSchemaName(fmt.ToString(row.get("v_schema_name")));
            tab.setTableName(fmt.ToString(row.get("v_table_name")));
            tables.add(tab);
        }
        return new GridPage<DefragModel>(tables, page, max, rowCount);
    }

    @Override
    public List<OptionsModel> listSchemaNames() {
        String query = "SELECT v_schema_name FROM CNFG_DEFRAG_USERS ORDER BY 1";
        List<OptionsModel> ids = new ArrayList<OptionsModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query);
        for (Map row : rows) {
            OptionsModel id = new OptionsModel();
            id.setKey(row.get("v_schema_name").toString());
            id.setValue((String) row.get("v_schema_name"));
            ids.add(id);
        }
        return ids;
    }

    @Override
    public List<OptionsModel> listTableNames(String schema) {
        String query = "SELECT table_name FROM ALL_TABLES "
                + "WHERE owner = ? "
                + "ORDER BY 1";
        List<OptionsModel> ids = new ArrayList<OptionsModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{schema});
        for (Map row : rows) {
            OptionsModel id = new OptionsModel();
            id.setKey(row.get("table_name").toString());
            id.setValue((String) row.get("table_name"));
            ids.add(id);
        }
        return ids;
    }

    @Override
    public void postDefrag(DefragModel model) {
        String query = "SELECT max(n_serial_no)+1 FROM CNFG_DEFRAG_TABLES";
        Integer no = getJdbcTemplate().queryForObject(query, Integer.class);
        query = "INSERT INTO CNFG_DEFRAG_TABLES (n_serial_no, v_schema_name, v_table_name)"
                + " VALUES(?, ?, ?)";
        getJdbcTemplate().update(query, new Object[]{no, model.getSchemaName(), model.getTableName()});
    }

    @Override
    public void deleteDefrag(String id) {
        String query = "DELETE FROM CNFG_DEFRAG_TABLES WHERE n_serial_no = ?";
        getJdbcTemplate().update(query, new Object[]{id});
    }

    @Override
    public void callDefragProc() {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource()).withSchemaName(schemaName).withProcedureName("PROC_DEFRAG").withoutProcedureColumnMetaDataAccess();
        jdbcCall.execute();
    }

    @Override
    public void updateAppVersion() {
        String query = "UPDATE APP_DETAILS SET APP_VERSION = SYSTIMESTAMP";
        getJdbcTemplate().update(query);
    }
    
    @Override
    public String rolerights(List<String> roleslist) {
    	String rolerights=null;
    	 if (roleslist.contains("ROLE_ADMIN") && roleslist.contains("ROLE_RAROC_AUTH")) {
    		 rolerights="AdminAuth";
         }if (roleslist.contains("ROLE_RAROC_AUTH") && !roleslist.contains("ROLE_ADMIN")) {
        	 rolerights="Auth";
         }if (roleslist.contains("ROLE_ADMIN") && !roleslist.contains("ROLE_RAROC_AUTH")) {
        	 rolerights="Administrator";
         }if (roleslist.contains("ROLE_ADMIN") && roleslist.contains("ROLE_RAROC_CORP")) {
        	 rolerights="Corp";
         }if (roleslist.contains("ROLE_RAROC_AUTH") && roleslist.contains("ROLE_RAROC_CORP")) {
        	 rolerights="CorpAuth";
         }if (roleslist.contains("ROLE_ADMIN") && roleslist.contains("ROLE_RAROC_AUTH")&&roleslist.contains("ROLE_RAROC_CORP")) {
        	 rolerights="AdminCorp";
         }
    	return rolerights;
    }
}
