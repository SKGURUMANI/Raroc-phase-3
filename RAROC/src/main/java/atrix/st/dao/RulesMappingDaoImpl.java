/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.dao;

import atrix.common.model.QueryBuilderModel;
import atrix.common.service.FormatterService;
import atrix.common.service.QueryBuilderService;
import atrix.common.util.DataTablesResponse;
import atrix.common.util.GridPage;
import atrix.st.model.RulesFxGridModel;
import atrix.st.model.RulesMappingModel;
import atrix.st.model.RulesOptionModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;

/**
 *
 * @author vaio
 */
@Repository("rulesMappingDao")
public class RulesMappingDaoImpl extends JdbcDaoSupport implements RulesMappingDao {

    @Autowired
    RulesMappingDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private LobHandler lobHandler;
    @Autowired
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;

    @Override
    public GridPage<RulesMappingModel> listMappings(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String ruleName) {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("c_source1", "c_source2", "c_source3",
                "c_source4", "c_source5", "c_destination", "v_formula_id", "v_approach", "n_rw", "n_pd", "n_lgd", "n_em"));
        if (sidx == null || sidx.isEmpty()) {
            sidx = "v_formula_id";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM CNFG_RULE_MAPPING_MASTER WHERE v_rule_name = ? " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{ruleName, qObj.getRegex()},Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT n_id, c_source1, c_source2, c_source3, c_source4, c_source5, c_destination, "
                + "case when c_fx is null then '...' else ASYM_GET_FORMULA_NAME(c_fx) end v_formula_id, v_approach, n_rw, n_pd, "
                + "n_lgd, n_em "                
                + "FROM CNFG_RULE_MAPPING_MASTER WHERE v_rule_name = ? "
                + qObj.getCondition() + " "
                + "ORDER BY to_char(" + sidx + ") " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";         
        List<RulesMappingModel> rules = getJdbcTemplate().query(query, 
                new Object[]{ruleName, qObj.getRegex(), endIdx, startIdx}, new RowMapper<RulesMappingModel>() {
            @Override
            public RulesMappingModel mapRow(ResultSet rs, int i) throws SQLException {
                RulesMappingModel rule = new RulesMappingModel();
                rule.setId(rs.getString("n_id"));
                rule.setSource1(lobHandler.getClobAsString(rs, "c_source1"));
                rule.setSource2(lobHandler.getClobAsString(rs, "c_source2"));
                rule.setSource3(lobHandler.getClobAsString(rs, "c_source3"));
                rule.setSource4(lobHandler.getClobAsString(rs, "c_source4"));
                rule.setSource5(lobHandler.getClobAsString(rs, "c_source5"));
                rule.setDestination(lobHandler.getClobAsString(rs, "c_destination"));
                rule.setFxId(rs.getString("v_formula_id"));
                rule.setApproach(rs.getString("v_approach"));
                rule.setStressRw(rs.getString("n_rw"));
                rule.setPd(rs.getString("n_pd"));
                rule.setLgd(rs.getString("n_lgd"));
                rule.setEm(rs.getString("n_em"));
                return rule;
            }
        });
        return new GridPage<RulesMappingModel>(rules, page, max, rowCount);
    }

    @Override
    public void deleteRuleMapping(String rulename) {
        String query = "DELETE FROM CNFG_RULE_MAPPING_MASTER WHERE v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{rulename});
    }
    
    @Override
    public List<RulesOptionModel> getOptions(String tab, String col) {
        String query = "SELECT v_desc_column FROM CNFG_MODEL_MASTER WHERE v_code_column = ? AND v_table_name = ?";
        String v_desc = getJdbcTemplate().queryForObject(query, new Object[]{col, tab}, String.class);
        query = "SELECT distinct " + col + " col_code, " + v_desc + " col_desc FROM " + tab;
        List<RulesOptionModel> objects = new ArrayList<RulesOptionModel>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        for (Map row : rows) {
            RulesOptionModel obj = new RulesOptionModel();
            obj.setCode(fmt.ToString(row.get("col_code")));
            obj.setDesc(fmt.ToString(row.get("col_desc")));            
            objects.add(obj);
        }
        return objects;
    }

    @Override
    public void addMapping(String ruleName, RulesMappingModel rule) {
        String opDelimiter = "~#~", valDelimiter = ",";
        int i;
        String[] values;
        String source1[] = {"", ""}, source2[] = {"", ""}, source3[] = {"", ""}, source4[] = {"", ""}, 
               source5[] = {"", ""}, destination[] = {"", ""}, temp[];        
        
        if (rule.getSource1() != null && rule.getSource1().contains(opDelimiter)) {
            values = rule.getSource1().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source1[0] = source1[0] + ",";
                    source1[1] = source1[1] + ",";
                }
                source1[0] = source1[0] +  temp[0];
                source1[1] = source1[1] +  temp[1];                
            }        
        } else {
            source1[1] = rule.getSource1();
        }

        if (rule.getSource2() != null && rule.getSource2().contains(opDelimiter)) {
            values = rule.getSource2().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source2[0] = source2[0] + ",";
                    source2[1] = source2[1] + ",";
                }
                source2[0] = source2[0] +  temp[0];
                source2[1] = source2[1] +  temp[1];
            }
        } else {
            source2[1] = rule.getSource2();
        }

        if (rule.getSource3() != null && rule.getSource3().contains(opDelimiter)) {
            values = rule.getSource3().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source3[0] = source3[0] + ",";
                    source3[1] = source3[1] + ",";
                }
                source3[0] = source3[0] +  temp[0];
                source3[1] = source3[1] +  temp[1];
            }
        } else {
            source3[1] = rule.getSource3();
        }

        if (rule.getSource4() != null && rule.getSource4().contains(opDelimiter)) {
            values = rule.getSource4().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source4[0] = source4[0] + ",";
                    source4[1] = source4[1] + ",";
                }
                source4[0] = source4[0] +  temp[0];
                source4[1] = source4[1] +  temp[1];
            }
        } else {
            source4[1] = rule.getSource4();
        }

        if (rule.getSource5() != null && rule.getSource5().contains(opDelimiter)) {
            values = rule.getSource5().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source5[0] = source5[0] + ",";
                    source5[1] = source5[1] + ",";
                }
                source5[0] = source5[0] +  temp[0];
                source5[1] = source5[1] +  temp[1];
            }
        } else {
            source5[1] = rule.getSource5();
        }

        if (rule.getDestination() != null && rule.getDestination().contains(opDelimiter)) {
            values = rule.getDestination().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    destination[0] = destination[0] + ",";
                    destination[1] = destination[1] + ",";
                }
                destination[0] = destination[0] +  temp[0];
                destination[1] = destination[1] +  temp[1];
            }
        } else {
            destination[1] = rule.getDestination();
        }

        String query = "SELECT nvl(max(n_id),0)+1 FROM CNFG_RULE_MAPPING_MASTER WHERE v_rule_name = ?";
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{ruleName},Integer.class);
        query = "INSERT INTO CNFG_RULE_MAPPING_MASTER (n_id, v_rule_name, c_source1_cd, c_source1, c_source2_cd, "
                + "c_source2, c_source3_cd, c_source3, c_source4_cd, c_source4, c_source5_cd, c_source5, "
                + "c_destination_cd, c_destination, v_approach, n_rw, n_pd, n_lgd, n_em) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        getJdbcTemplate().update(query, new Object[]{
                    rowCount, ruleName, new SqlLobValue(source1[0], lobHandler), new SqlLobValue(source1[1], lobHandler), 
                    new SqlLobValue(source2[0], lobHandler), new SqlLobValue(source2[1], lobHandler), 
                    new SqlLobValue(source3[0], lobHandler), new SqlLobValue(source3[1], lobHandler),
                    new SqlLobValue(source4[0], lobHandler), new SqlLobValue(source4[1], lobHandler), 
                    new SqlLobValue(source5[0], lobHandler), new SqlLobValue(source5[1], lobHandler), 
                    new SqlLobValue(destination[0], lobHandler), new SqlLobValue(destination[1], lobHandler),
                    rule.getApproach(), rule.getStressRw(), rule.getPd(), rule.getLgd(), rule.getEm()
                }, new int[]{Types.VARCHAR, Types.VARCHAR, Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB,
                    Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.VARCHAR, 
                    Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});
        query = "UPDATE CNFG_RULE_MASTER SET d_modified=sysdate WHERE v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{ruleName});
    }
    
    @Override
    public void copyMapping(String newName, String ruleName, String audCd) {
        String query = "SELECT v_rule_name FROM CNFG_RULE_MASTER WHERE v_rule_name = ?";
        String name = getJdbcTemplate().queryForObject(query, new Object[]{newName}, String.class);
        query = "INSERT INTO CNFG_RULE_MAPPING_MASTER (n_id, v_rule_name, v_audit_cd, c_fx, c_source1_cd, c_source1, "
                + "c_source2_cd, c_source2, c_source3_cd, c_source3, c_source4_cd, c_source4, c_source5_cd, c_source5, "
                + "c_destination_cd, c_destination, v_stressed_rw, v_approach, n_rw, n_pd, n_lgd, n_em) "
                + "SELECT n_id, '"+name+"', '"+audCd+"', c_fx, c_source1_cd, c_source1, c_source2_cd, c_source2, "
                + "c_source3_cd, c_source3, c_source4_cd, c_source4, c_source5_cd, c_source5, c_destination_cd, "
                + "c_destination, v_stressed_rw, v_approach, n_rw, n_pd, n_lgd, n_em "
                + "FROM CNFG_RULE_MAPPING_MASTER WHERE v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{ruleName});
    }

    @Override
    public void editMapping(String ruleName, RulesMappingModel rule) {
        String opDelimiter = "~#~", valDelimiter = ",";
        int i;
        String[] values;
        String source1[] = {"", ""}, source2[] = {"", ""}, source3[] = {"", ""}, source4[] = {"", ""}, 
               source5[] = {"", ""}, destination[] = {"", ""}, temp[];

        if (rule.getSource1() != null && rule.getSource1().contains(opDelimiter)) {
            values = rule.getSource1().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source1[0] = source1[0] + ",";
                    source1[1] = source1[1] + ",";
                }
                source1[0] = source1[0] +  temp[0];
                source1[1] = source1[1] +  temp[1];
            }
        } else {
            source1[1] = rule.getSource1();
        }

        if (rule.getSource2() != null && rule.getSource2().contains(opDelimiter)) {
            values = rule.getSource2().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source2[0] = source2[0] + ",";
                    source2[1] = source2[1] + ",";
                }
                source2[0] = source2[0] +  temp[0];
                source2[1] = source2[1] +  temp[1];
            }
        } else {
            source2[1] = rule.getSource2();
        }

        if (rule.getSource3() != null && rule.getSource3().contains(opDelimiter)) {
            values = rule.getSource3().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source3[0] = source3[0] + ",";
                    source3[1] = source3[1] + ",";
                }
                source3[0] = source3[0] +  temp[0];
                source3[1] = source3[1] +  temp[1];
            }
        } else {
            source3[1] = rule.getSource3();
        }

        if (rule.getSource4() != null && rule.getSource4().contains(opDelimiter)) {
            values = rule.getSource4().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source4[0] = source4[0] + ",";
                    source4[1] = source4[1] + ",";
                }
                source4[0] = source4[0] +  temp[0];
                source4[1] = source4[1] +  temp[1];
            }
        } else {
            source4[1] = rule.getSource4();
        }

        if (rule.getSource5() != null && rule.getSource5().contains(opDelimiter)) {
            values = rule.getSource5().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    source5[0] = source5[0] + ",";
                    source5[1] = source5[1] + ",";
                }
                source5[0] = source5[0] +  temp[0];
                source5[1] = source5[1] +  temp[1];
            }
        } else {
            source5[1] = rule.getSource5();
        }

        if (rule.getDestination() != null && rule.getDestination().contains(opDelimiter)) {
            values = rule.getDestination().split(valDelimiter);
            for(i = 0; i < values.length; i++) {
                temp = values[i].split(opDelimiter, 2);
                if(i != 0) {
                    destination[0] = destination[0] + ",";
                    destination[1] = destination[1] + ",";
                }
                destination[0] = destination[0] +  temp[0];
                destination[1] = destination[1] +  temp[1];
            }
        } else {
            destination[1] = rule.getDestination();
        }

        String query = "UPDATE CNFG_RULE_MAPPING_MASTER SET c_source1_cd= ?, c_source1=?, c_source2_cd=?, c_source2= ?, "
                + "c_source3_cd=?, c_source3=?, c_source4_cd=?, c_source4=?, c_source5_cd=?, c_source5=?, "
                + "c_destination_cd=?, c_destination=?, v_approach=?, n_rw=?, n_pd=?, n_lgd=?, n_em=? "
                + "WHERE n_id = ? AND v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{
                    new SqlLobValue(source1[0], lobHandler), new SqlLobValue(source1[1], lobHandler), 
                    new SqlLobValue(source2[0], lobHandler), new SqlLobValue(source2[1], lobHandler), 
                    new SqlLobValue(source3[0], lobHandler), new SqlLobValue(source3[1], lobHandler),
                    new SqlLobValue(source4[0], lobHandler), new SqlLobValue(source4[1], lobHandler), 
                    new SqlLobValue(source5[0], lobHandler), new SqlLobValue(source5[1], lobHandler), 
                    new SqlLobValue(destination[0], lobHandler), new SqlLobValue(destination[1], lobHandler), 
                    rule.getApproach(), rule.getStressRw(), rule.getPd(), rule.getLgd(), rule.getEm(), 
                    rule.getId(), ruleName
                }, new int[]{Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, 
                    Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.CLOB, Types.VARCHAR, Types.VARCHAR, 
                    Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});
        query = "UPDATE CNFG_RULE_MASTER SET d_modified=sysdate WHERE v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{ruleName});
    }

    @Override
    public void deleteMapping(int id, String ruleName) {
        String query = "DELETE FROM CNFG_RULE_MAPPING_MASTER WHERE n_id = ? AND v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{id, ruleName});
    }

    @Override
    public Map<String, String> getColumns(String table, String ruleType) {
        String query;
        if(ruleType.equals("RR")) {
            query = "SELECT v_code_column, v_desc_column FROM CNFG_MODEL_MASTER WHERE v_table_name = ?";
        } else {
            query = "SELECT v_code_column, v_desc_column FROM CNFG_MODEL_MASTER "
                    + "WHERE v_table_name = ? AND f_pk_flag <> 'Y'";
        }
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{table});
        Map<String, String> options = new LinkedHashMap<String, String>();
        for (Map row : rows) {
            options.put(fmt.ToString(row.get("v_code_column")), fmt.ToString(row.get("v_desc_column")));
        }
        return options;
    }

    @Override
    public DataTablesResponse<RulesFxGridModel> listFx(Integer sEcho, Integer start, Integer length,
            Integer sidx, String sord, String search) {
        int startidx = start + 1, endidx, rowCount, displayCount;
        String query;
        List<Map<String, Object>> rows;
        List<RulesFxGridModel> ids = new ArrayList<RulesFxGridModel>();
        String sql = "SELECT count(*) FROM CNFG_FORMULA_TABLE";
        rowCount = getJdbcTemplate().queryForObject(sql,Integer.class);
        displayCount = rowCount;
        if (rowCount < start + length) {
            endidx = rowCount;
        } else {
            endidx = start + length;
        }
        if (search == null && search.equals("")) {
            query = "SELECT * FROM "
                    + "(SELECT a.*, rownum rnum FROM "
                    + "(SELECT v_formula_id, v_formula_name "
                    + "FROM CNFG_FORMULA_TABLE "
                    + "ORDER BY " + (sidx + 1) + " " + sord + ") a "
                    + "WHERE rownum <= ?) WHERE rnum >= ?";
            rows = getJdbcTemplate().queryForList(query, new Object[]{endidx, startidx});
        } else {
            query = "SELECT count(*) rowCount FROM CNFG_FORMULA_TABLE "
                    + "WHERE (upper(v_formula_id) like upper('%" + search + "%') "
                    + "OR upper(v_formula_name) like upper('%" + search + "%'))";
            displayCount = getJdbcTemplate().queryForObject(query,Integer.class);
            query = "SELECT * FROM "
                    + "(SELECT a.*, rownum rnum FROM "
                    + "(SELECT v_formula_id, v_formula_name "
                    + "FROM CNFG_FORMULA_TABLE "
                    + "WHERE (upper(v_formula_id) like upper('%" + search + "%') "
                    + "OR upper(v_formula_name) like upper('%" + search + "%'))"
                    + "ORDER BY " + (sidx + 1) + " " + sord + ") a "
                    + "WHERE rownum <= ?) WHERE rnum >= ?";
            rows = getJdbcTemplate().queryForList(query, new Object[]{endidx, startidx});
        }
        for (Map row : rows) {
            RulesFxGridModel id = new RulesFxGridModel();
            id.setFormulaId(fmt.ToString(row.get("v_formula_id")));
            id.setFormulaName(fmt.ToString(row.get("v_formula_name")));
            ids.add(id);
        }
        return new DataTablesResponse<RulesFxGridModel>(sEcho, rowCount, displayCount, ids);
    }

    @Override
    public RulesFxGridModel getFx(String fxid) {
        String sql = "SELECT v_formula_id, v_formula_name, v_aggregation_type, c_formula_string "
                + "FROM CNFG_FORMULA_TABLE WHERE v_formula_id = ?";
        RulesFxGridModel fx = getJdbcTemplate().queryForObject(
                sql, new Object[]{fxid}, new RowMapper<RulesFxGridModel>() {
            @Override
            public RulesFxGridModel mapRow(ResultSet rs, int i) throws SQLException {
                RulesFxGridModel obj = new RulesFxGridModel();
                obj.setFormulaId(rs.getString(1));
                obj.setFormulaName(rs.getString(2));
                obj.setAggregateType(rs.getString(3));
                obj.setFormulaText(lobHandler.getClobAsString(rs, 4));
                return obj;
            }
        });
        return fx;
    }

    @Override
    public void updateFxInMapping(RulesFxGridModel expModel) {
        String query = "UPDATE CNFG_RULE_MAPPING_MASTER SET c_fx = ? "
                + "WHERE n_id = ? AND v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{expModel.getMapping(), expModel.getId(),
                    expModel.getFormulaName()}, new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});
    }

    @Override
    public Map<String, String> getFxFromMapping(String id, String ruleName) {
        String fx;
        String optionarray[];
        int i;
        try {
            String sql = "SELECT nvl(a.c_fx,'') c_fx "
                    + "FROM CNFG_RULE_MAPPING_MASTER a "
                    + "WHERE a.n_id = ? AND a.v_rule_name = ?";
            fx = (String) getJdbcTemplate().queryForObject(
                    sql, new Object[]{id, ruleName}, new RowMapper<String>() {

                @Override
                public String mapRow(ResultSet rs, int i) throws SQLException {
                    String fx;
                    fx = lobHandler.getClobAsString(rs, "c_fx");
                    return fx;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            fx = "";
        }
        if (fx != null) {
            optionarray = fx.split(",");
            Map<String, String> options = new LinkedHashMap<String, String>();
            for (i = 0; i < optionarray.length; i++) {
                options.put(optionarray[i], optionarray[i]);
            }
            return options;
        } else {
            return null;
        }
    }
    
    @Override
    public void updateMappingNull(String cColumn, String sColumn, String ruleName) {
        String query = "UPDATE CNFG_RULE_MAPPING_MASTER SET " + cColumn + " = null, " + sColumn + " = null "
                + "WHERE v_rule_name = ?";
        getJdbcTemplate().update(query, new Object[]{ruleName}, new int[]{Types.VARCHAR});
    }
}