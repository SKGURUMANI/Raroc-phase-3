/*
 * © 2013 Asymmetrix Solutions Private Limited. All rights reserved.
 * This work is part of the Risk Solutions and is copyrighted by Asymmetrix Solutions Private Limited.
 * All rights reserved.  No part of this work may be reproduced, stored in a retrieval system, adopted or 
 * transmitted in any form or by any means, electronic, mechanical, photographic, graphic, optic recording or
 * otherwise translated in any language or computer language, without the prior written permission of 
 * Asymmetrix Solutions Private Limited.
 * 
 * Asymmetrix Solutions Private Limited
 * 115, Bldg 2, Sector 3, Millennium Business Park,
 * Navi Mumbai, India, 410701
 */
package atrix.st.dao;

import atrix.common.model.OptionsModel;
import atrix.common.model.QueryBuilderModel;
import atrix.common.service.FormatterService;
import atrix.common.service.QueryBuilderService;
import atrix.common.util.GridPage;
import atrix.common.util.GridWithFooterPage;
import atrix.st.model.RarocAuthorize;
import atrix.st.model.RarocFacilityModel;
import atrix.st.model.RarocGridModel;
import atrix.st.model.RarocInputModel;
import atrix.st.model.RarocMasterModel;
import atrix.st.model.RarocViewModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import atrix.st.exception.CustomException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author vaio
 */
@Repository("rarocDao")
public class RarocDaoImpl extends JdbcDaoSupport implements RarocDao {

    @Autowired
    RarocDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }
    @Autowired
    private LobHandler lobHandler;
    @Autowired
    private FormatterService fmt;
    @Autowired
    private QueryBuilderService queryBuilder;
    private @Value("${schema.raroc}")
    String schemaName;

    private @Value("${mail.host}")
    String host;

    private @Value("${mail.port}")
    String port;

    private @Value("${mail.starttls}")
    String starttls;

    private @Value("${mail.auth}")
    String auth;

    @Override
    public GridPage<RarocMasterModel> listRarocMaster(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString, String userid) throws CustomException {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_rec_ref_no", "v_cust_id",
                "v_cust_name", "d_created", "v_modified", "d_modified", "f_status", "v_approved",
                "d_approved"));

        List<String> orders = Collections.unmodifiableList(Arrays.asList("asc", "desc"));

        if (sidx == null || sidx.isEmpty()) {
            sidx = "d_created";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }

        //check if sidx is in columns 
        if (!columns.contains(sidx)) {
            throw new CustomException();
        }

        if (!orders.contains(sord)) {
            throw new CustomException();
        }

        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM INPT_RAROC_MASTER WHERE v_created = ? " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{userid, qObj.getRegex()}, Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_rec_ref_no, v_rating_tool_id, v_cust_name, d_created, v_modified, d_modified, "
                + "case when f_status = 'H' then 'History' when f_status = 'A' then 'Approved' "
                + "when f_status = 'I' then 'Incomplete' when f_status = 'R' then 'Rejected' "
                + "when f_status = 'S' then 'Submitted For Approval' end f_status, "
                + "v_approved, to_char(d_approved,'dd-Mon-yy hh:mm:ss') d_approved "
                + "FROM INPT_RAROC_MASTER "
                + "WHERE v_created = ? "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<RarocMasterModel> lists = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{userid, qObj.getRegex(),
            endIdx, startIdx});
        rows.stream().map((row) -> {
            RarocMasterModel obj = new RarocMasterModel();
            obj.setId(fmt.ToString(row.get("rnum")));
            obj.setRarocref(fmt.ToString(row.get("v_rec_ref_no")));
            obj.setCid(fmt.ToString(row.get("v_rating_tool_id")));
            obj.setCname(fmt.ToString(row.get("v_cust_name")));
            obj.setCdate(fmt.ToString(row.get("d_created")));
            obj.setMuser(fmt.ToString(row.get("v_modified")));
            obj.setMdate(fmt.ToString(row.get("d_modified")));
            obj.setStatus(fmt.ToString(row.get("f_status")));
            obj.setAuser(fmt.ToString(row.get("v_approved")));
            obj.setAdate(fmt.ToString(row.get("d_approved")));
            return obj;
        }).forEach((obj) -> {
            lists.add(obj);
        });
        return new GridPage<>(lists, page, max, rowCount);
    }

    @Override
    public RarocMasterModel gridRarocHeader(String ref) {
        String query = "SELECT v_rec_ref_no, v_cust_name, (Select count(n_facility_no) from INPT_RAROC WHERE v_rec_ref_no = ? ) n_facility, v_cust_id, v_rating_tool_code, "
                + " v_int_rating, v_rating_tool_id, v_industry, v_business_unit, v_cps_id,  "
                + " n_ebid/100000 n_ebid, nvl(n_uhfce/100000,0) n_uhfce, V_EXP_BANKS,  "
                + " nvl(n_non_financial_coll/100000,0) n_non_financial_coll, "
                + " nvl(N_BA_CDAB/100000,0) N_BA_CDAB,nvl(N_TERM_DEPO/100000,0) N_TERM_DEPO,nvl(N_CA_FEE/100000,0) N_CA_FEE,nvl(N_CMS_FEE/100000,0) N_CMS_FEE,nvl(N_FOREX_COMMIS/100000,0) N_FOREX_COMMIS,nvl(N_OTHER/100000,0) N_OTHER, "
                + " V_PAN_NO, V_CIF_ID, NVL(D_MODIFIED,D_CREATED) D_CREATED, NVL(V_MODIFIED, V_CREATED) V_CREATED, V_DB_VERSION, "
                + " V_CUST_TYPE,NVL(N_PRO_AXIS_FUND*100,0) N_PRO_AXIS_FUND,NVL(N_MOV_PROPERTY/100000,0) N_MOV_PROPERTY,NVL(N_NPLL/100000,0) N_NPLL,F_WORK_CAP "
                + " FROM INPT_RAROC_MASTER "
                + " WHERE v_rec_ref_no = ? ";
        RarocMasterModel obj = (RarocMasterModel) getJdbcTemplate().queryForObject(
                query, new Object[]{ref, ref}, new RowMapper<RarocMasterModel>() {
            @Override
            public RarocMasterModel mapRow(ResultSet rs, int i) throws SQLException {
                RarocMasterModel obj = new RarocMasterModel();
                obj.setRarocref(rs.getString("v_rec_ref_no"));
                obj.setCname(rs.getString("v_cust_name"));
                obj.setFacility(rs.getInt("n_facility"));
                obj.setCid(rs.getString("v_cust_id"));
                obj.setRtool(rs.getString("v_rating_tool_code"));
                obj.setIntRat(rs.getString("v_int_rating"));
                obj.setRid(rs.getString("v_rating_tool_id"));
                obj.setInd(rs.getString("v_industry"));
                obj.setBussunit(rs.getString("v_business_unit"));
                obj.setCpsid(rs.getString("v_cps_id"));
                obj.setEbid(rs.getString("n_ebid"));
                obj.setUfce(rs.getString("n_uhfce"));
                obj.setExpBanks(rs.getString("v_exp_banks"));
                obj.setNonFinColl(rs.getString("n_non_financial_coll"));

                obj.setBbcdab(rs.getString("N_BA_CDAB"));
                obj.setTdfee(rs.getString("N_TERM_DEPO"));
                obj.setCafee(rs.getString("N_CA_FEE"));
                obj.setCms(rs.getString("N_CMS_FEE"));
                obj.setForex(rs.getString("N_FOREX_COMMIS"));
                obj.setOther(rs.getString("N_OTHER"));

                obj.setPan(rs.getString("V_PAN_NO"));
                obj.setCif(rs.getString("V_CIF_ID"));
                obj.setCreated(rs.getString("D_CREATED"));
                obj.setUsername(rs.getString("V_CREATED"));
                obj.setVersion(rs.getString("V_DB_VERSION"));
                obj.setCustType(rs.getString("V_CUST_TYPE"));
                obj.setPortion(rs.getString("N_PRO_AXIS_FUND"));
                obj.setImprop(rs.getString("N_MOV_PROPERTY"));
                obj.setNpll(rs.getString("N_NPLL"));
                obj.setWorkcap(rs.getString("F_WORK_CAP"));
                return obj;
            }
        });
        return obj;
    }

    @Override
    public String getRecordStatus(String ref) {
        String query = "SELECT f_status FROM INPT_RAROC_MASTER WHERE v_rec_ref_no = ?";
        return getJdbcTemplate().queryForObject(query, new Object[]{ref}, String.class);
    }

    @Override
    public String getCname(String ref) {
        String query = "SELECT V_CUST_NAME FROM INPT_RAROC_MASTER WHERE V_REC_REF_NO = ? ";
        return getJdbcTemplate().queryForObject(query, new Object[]{ref}, String.class);
    }

    @Override
    public String checkAvailability1(String rid, String userid) {
        String query = "SELECT max(v_created) FROM INPT_RAROC_MASTER "
                + "WHERE v_rating_tool_id = ? "
                + "AND v_created <> ?";
        return getJdbcTemplate().queryForObject(query, new Object[]{rid, userid}, String.class);
    }

    @Override
    public int checkAvailability2(String rid, String userid) {
        String query = "SELECT count(*) FROM INPT_RAROC_MASTER "
                + "WHERE v_rating_tool_id = ? "
                + "AND v_created = ? "
                + "AND f_status IN ('S')";
        return getJdbcTemplate().queryForObject(query, new Object[]{rid, userid}, Integer.class);
    }

    @Override
    public String checkRarocExistence(String rid, String userid) {
        String query = "SELECT v_rec_ref_no FROM INPT_RAROC_MASTER "
                + "WHERE v_rating_tool_id = ? "
                + "AND f_status = 'A' "
                + "AND v_created = ?";
        try {
            return getJdbcTemplate().queryForObject(query, new Object[]{rid, userid}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return "empty";
        }
    }

    @Override
    public GridPage<RarocGridModel> listRarocInput(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String recref) throws CustomException {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("n_facility_no",
                "v_fac_type", "v_fac_desc", "n_amount", "v_curr", "n_tenure"));

        List<String> orders = Collections.unmodifiableList(Arrays.asList("asc", "desc"));

        if (sidx == null || sidx.isEmpty()) {
            sidx = "n_facility_no";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }
        //check if sidx is in columns 
        if (!columns.contains(sidx)) {
            throw new CustomException();
        }

        if (!orders.contains(sord)) {
            throw new CustomException();
        }

        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) rowcount FROM INPT_RAROC WHERE v_rec_ref_no = ? " + qObj.getCondition();
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{recref, qObj.getRegex()}, Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT n_facility_no, v_fac_type, v_fac_desc, n_amount, v_curr, n_tenure "
                + "FROM INPT_RAROC "
                + "WHERE v_rec_ref_no = ? "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<RarocGridModel> lists = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{recref,
            qObj.getRegex(), endIdx, startIdx});
        for (Map row : rows) {
            RarocGridModel obj = new RarocGridModel();
            obj.setId(fmt.ToString(row.get("n_facility_no")));
            obj.setFacType(fmt.ToString(row.get("v_fac_type")));
            obj.setFacDesc(fmt.ToString(row.get("v_fac_desc")));
            obj.setAmount(fmt.ToString(row.get("n_amount")));
            obj.setCur(fmt.ToString(row.get("v_curr")));
            obj.setTenure(fmt.ToString(row.get("n_tenure")));
            lists.add(obj);
        }
        return new GridPage<>(lists, page, max, rowCount);
    }

    @Override
    public GridWithFooterPage<RarocViewModel> gridRarocView(String ref, Integer unit) {
        List<RarocViewModel> facs = new ArrayList<>();
        RarocViewModel footer = new RarocViewModel();
        String query = "SELECT inpt.v_rec_ref_no, inpt.v_fac_type, inpt.v_fac_desc, inpt.v_asset_type, inpt.v_curr, "
                + "(inpt.n_amount * inpt.n_excge_rate) n_amount, inpt.n_tenure, inpt.n_excge_rate, mast.v_rating_tool_code, mast.v_int_rating, "
                + "inpt.n_avg_utili * 100 n_avg_utili, inpt.n_int_rate_comm *100 n_int_rate_comm, "
                + "inpt.n_cost_funds * 100 n_cost_funds, inpt.n_upfront_fee, "
                + "(inpt.n_annual_fee * inpt.n_amount * inpt.n_excge_rate) n_annual_fee, "
                + "inpt.n_cash_margin * 100 n_cash_margin, inpt.v_cash_mismatch, inpt.v_long_ext, inpt.v_short_ext, "
                + "inpt.n_exp_guar * 100 n_exp_guar, inpt.v_guarantor_int, inpt.v_guarantor_ext, inpt.v_guar_type, "
                + "inpt.v_int_type, rslt.n_rwa_rs , rslt.n_ead_rs, rslt.n_el, rslt.n_nii, rslt.n_amortized_fee, "
                + "rslt.n_opex, rslt.n_pbt, rslt.n_pat, rslt.n_alloc_capital, rslt.n_income_alloc_capital, "
                + "rslt.n_adjusted_pat, rslt.n_raroc * 100 n_raroc, rslt.n_drawn_ccf * 100 n_drawn_ccf, "
                + "rslt.n_undrawn_ccf * 100 n_undrawn_ccf, rslt.n_g_sec * 100 n_g_sec, rslt.n_car * 100 n_car, "
                + "rslt.n_tax_rate * 100 n_tax_rate, case when rslt.n_facility_no <>  99   then (rslt.n_rwa_rs /rslt.n_ead_rs) end  * 100 n_rw, rslt.n_nim * 100 n_nim, rslt.n_oth_income, "
                + "rslt.n_facility_no, mast.v_business_unit, mast.v_cust_name, inpt.v_ucicf, "
                + "inpt.v_restructured_status, inpt.v_templated_rating "
                + "FROM RSLT_RAROC rslt LEFT OUTER JOIN INPT_RAROC inpt ON inpt.v_rec_ref_no = rslt.v_rec_ref_no "
                + "AND inpt.n_facility_no = rslt.n_facility_no "
                + "LEFT OUTER JOIN INPT_RAROC_MASTER mast ON mast.v_rec_ref_no = rslt.v_rec_ref_no "
                + "WHERE rslt.v_rec_ref_no = ? "
                + "AND   rslt.v_result_type = 'raroc' "
                + "ORDER BY rslt.n_facility_no nulls last";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{ref});
        int i = 0, cols = 40;
        String[][] resultArray = new String[26][cols];
        String[] outputName = new String[cols];
        String[] creditTotal = new String[cols];
        String[] total = new String[cols];
        outputName[0] = "Facility Description";
        outputName[1] = "Facility Type";
        outputName[2] = "Asset Type";
        outputName[3] = "Templated Rating";
        outputName[4] = "Currency";
        outputName[5] = "Amount";
        outputName[6] = "Tenure (in Months)";
        outputName[7] = "Exchange Rate (Rs/Currency)";
        outputName[8] = "Restructuring Status";
        outputName[9] = "Unconditionally Cancelable";
        outputName[10] = "Average Utilization (%)";
        outputName[11] = "Interest Type";
        outputName[12] = "Interest Rate (in %)";
        outputName[13] = "Cost of Funds (%)";
        outputName[14] = "Upfront Fee(Rs.)";
        outputName[15] = "Annual Fee / Commission(Rs.)";
        outputName[16] = "Cash Margin over utilized amt (%)";
        outputName[17] = "Cash Margin Currency Mismatch";
        outputName[18] = "Long-term External Rating (Borrower)";
        outputName[19] = "Short-term External Rating (Borrower)";
        outputName[20] = "Exposure Guaranteed (%)";
        outputName[21] = "Guarantor Type";
        outputName[22] = "Guarantor Internal Rating";
        outputName[23] = "Guarantor External Rating";
        outputName[24] = "Risk free rate ( in %)";
        outputName[25] = "CAR (%)";
        outputName[26] = "Tax Rate (%)";
        //outputName[27] = "Credit Conversion Factor (CCF) - drawn";
        //outputName[28] = "Credit Conversion Factor (CCF) - undrawn";
        outputName[27] = "Effective Risk Weight(%)";
        outputName[28] = "Net STD EAD(Rs.)";
        outputName[29] = "RWA-Credit Risk(Rs.)";
        outputName[30] = "NIM / Commission (%)";
        outputName[31] = "NII(Rs.)";
        outputName[32] = "Amortized Fee Income(Rs.)";
        outputName[33] = "Other Income(Rs.)";
        outputName[34] = "Operating Expense(Rs.)";
        outputName[35] = "Expected Loss(Rs.)";
        outputName[36] = "PBT(Rs.)";
        outputName[37] = "Capital Allocated(Rs.)";
        outputName[38] = "Adjusted PAT(Rs.)";
        outputName[39] = "RAROC (%)";
        for (Map row : rows) {
            if (row.get("n_facility_no") == null) {
                total[0] = fmt.ToString(row.get("v_fac_desc"));
                total[1] = fmt.ToString(row.get("v_fac_type"));
                total[2] = fmt.ToString(row.get("v_asset_type"));
                total[3] = fmt.ToString(row.get("v_templated_rating"));
                total[4] = fmt.ToString(row.get("v_curr"));
                total[5] = fmt.ToString(row.get("n_amount"));
                total[6] = fmt.ToString(row.get("n_tenure"));
                total[7] = fmt.ToString(row.get("n_excge_rate"));
                total[8] = fmt.ToString(row.get("v_restructured_status"));
                total[9] = fmt.ToString(row.get("v_ucicf"));
                total[10] = fmt.ToString(row.get("n_avg_utili"));
                total[11] = fmt.ToString(row.get("v_int_type"));
                total[12] = fmt.ToString(row.get("n_int_rate_comm"));
                total[13] = fmt.ToString(row.get("n_cost_funds"));
                total[14] = fmt.ToString(row.get("n_upfront_fee"));
                total[15] = fmt.ToString(row.get("n_annual_fee"));
                total[16] = fmt.ToString(row.get("n_cash_margin"));
                total[17] = fmt.ToString(row.get("v_cash_mismatch"));
                total[18] = fmt.ToString(row.get("v_long_ext"));
                total[19] = fmt.ToString(row.get("v_short_ext"));
                total[20] = fmt.ToString(row.get("n_exp_guar"));
                total[21] = fmt.ToString(row.get("v_guar_type"));
                total[22] = fmt.ToString(row.get("v_guarantor_int"));
                total[23] = fmt.ToString(row.get("v_guarantor_ext"));
                total[24] = fmt.ToString(row.get("n_g_sec"));
                total[25] = fmt.ToString(row.get("n_car"));
                total[26] = fmt.ToString(row.get("n_tax_rate"));
                //total[27] = fmt.ToString(row.get("n_drawn_ccf"));
                //total[28] = fmt.ToString(row.get("n_undrawn_ccf"));
                total[27] = fmt.ToString(row.get("n_rw"));//rwa_rs/ead_rs*100
                total[28] = fmt.ToString(row.get("n_ead_rs"));
                total[29] = fmt.ToString(row.get("n_rwa_rs"));
                total[30] = fmt.ToString(row.get("n_nim"));
                total[31] = fmt.ToString(row.get("n_nii"));
                total[32] = fmt.ToString(row.get("n_amortized_fee"));
                total[33] = fmt.ToString(row.get("n_oth_income"));
                total[34] = fmt.ToString(row.get("n_opex"));
                total[35] = fmt.ToString(row.get("n_el"));
                total[36] = fmt.ToString(row.get("n_pbt"));
                total[37] = fmt.ToString(row.get("n_alloc_capital"));
                total[38] = fmt.ToString(row.get("n_adjusted_pat"));
                total[39] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 99) {
                creditTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                creditTotal[1] = fmt.ToString(row.get("v_fac_type"));
                creditTotal[2] = fmt.ToString(row.get("v_asset_type"));
                creditTotal[3] = fmt.ToString(row.get("v_templated_rating"));
                creditTotal[4] = fmt.ToString(row.get("v_curr"));
                creditTotal[5] = fmt.ToString(row.get("n_amount"));
                creditTotal[6] = fmt.ToString(row.get("n_tenure"));
                creditTotal[7] = fmt.ToString(row.get("n_excge_rate"));
                creditTotal[8] = fmt.ToString(row.get("v_restructured_status"));
                creditTotal[9] = fmt.ToString(row.get("v_ucicf"));
                creditTotal[10] = fmt.ToString(row.get("n_avg_utili"));
                creditTotal[11] = fmt.ToString(row.get("v_int_type"));
                creditTotal[12] = fmt.ToString(row.get("n_int_rate_comm"));
                creditTotal[13] = fmt.ToString(row.get("n_cost_funds"));
                creditTotal[14] = fmt.ToString(row.get("n_upfront_fee"));
                creditTotal[15] = fmt.ToString(row.get("n_annual_fee"));
                creditTotal[16] = fmt.ToString(row.get("n_cash_margin"));
                creditTotal[17] = fmt.ToString(row.get("v_cash_mismatch"));
                creditTotal[18] = fmt.ToString(row.get("v_long_ext"));
                creditTotal[19] = fmt.ToString(row.get("v_short_ext"));
                creditTotal[20] = fmt.ToString(row.get("n_exp_guar"));
                creditTotal[21] = fmt.ToString(row.get("v_guar_type"));
                creditTotal[22] = fmt.ToString(row.get("v_guarantor_int"));
                creditTotal[23] = fmt.ToString(row.get("v_guarantor_ext"));
                creditTotal[24] = fmt.ToString(row.get("n_g_sec"));
                creditTotal[25] = fmt.ToString(row.get("n_car"));
                creditTotal[26] = fmt.ToString(row.get("n_tax_rate"));
                //creditTotal[27] = fmt.ToString(row.get("n_drawn_ccf"));
                //creditTotal[28] = fmt.ToString(row.get("n_undrawn_ccf"));
                creditTotal[27] = fmt.ToString(row.get("n_rw"));
                creditTotal[28] = fmt.ToString(row.get("n_ead_rs"));
                creditTotal[29] = fmt.ToString(row.get("n_rwa_rs"));
                creditTotal[30] = fmt.ToString(row.get("n_nim"));
                creditTotal[31] = fmt.ToString(row.get("n_nii"));
                creditTotal[32] = fmt.ToString(row.get("n_amortized_fee"));
                creditTotal[33] = fmt.ToString(row.get("n_oth_income"));
                creditTotal[34] = fmt.ToString(row.get("n_opex"));
                creditTotal[35] = fmt.ToString(row.get("n_el"));
                creditTotal[36] = fmt.ToString(row.get("n_pbt"));
                creditTotal[37] = fmt.ToString(row.get("n_alloc_capital"));
                creditTotal[38] = fmt.ToString(row.get("n_adjusted_pat"));
                creditTotal[39] = fmt.ToString(row.get("n_raroc"));
            } else {
                resultArray[i][0] = fmt.ToString(row.get("v_fac_desc"));
                resultArray[i][1] = fmt.ToString(row.get("v_fac_type"));
                resultArray[i][2] = fmt.ToString(row.get("v_asset_type"));
                resultArray[i][3] = fmt.ToString(row.get("v_templated_rating"));
                resultArray[i][4] = fmt.ToString(row.get("v_curr"));
                resultArray[i][5] = fmt.ToString(row.get("n_amount"));
                resultArray[i][6] = fmt.ToString(row.get("n_tenure"));
                resultArray[i][7] = fmt.ToString(row.get("n_excge_rate"));
                resultArray[i][8] = fmt.ToString(row.get("v_restructured_status"));
                resultArray[i][9] = fmt.ToString(row.get("v_ucicf"));
                resultArray[i][10] = fmt.ToString(row.get("n_avg_utili"));
                resultArray[i][11] = fmt.ToString(row.get("v_int_type"));
                resultArray[i][12] = fmt.ToString(row.get("n_int_rate_comm"));
                resultArray[i][13] = fmt.ToString(row.get("n_cost_funds"));
                resultArray[i][14] = fmt.ToString(row.get("n_upfront_fee"));
                resultArray[i][15] = fmt.ToString(row.get("n_annual_fee"));
                resultArray[i][16] = fmt.ToString(row.get("n_cash_margin"));
                resultArray[i][17] = fmt.ToString(row.get("v_cash_mismatch"));
                resultArray[i][18] = fmt.ToString(row.get("v_long_ext"));
                resultArray[i][19] = fmt.ToString(row.get("v_short_ext"));
                resultArray[i][20] = fmt.ToString(row.get("n_exp_guar"));
                resultArray[i][21] = fmt.ToString(row.get("v_guar_type"));
                resultArray[i][22] = fmt.ToString(row.get("v_guarantor_int"));
                resultArray[i][23] = fmt.ToString(row.get("v_guarantor_ext"));
                resultArray[i][24] = fmt.ToString(row.get("n_g_sec"));
                resultArray[i][25] = fmt.ToString(row.get("n_car"));
                resultArray[i][26] = fmt.ToString(row.get("n_tax_rate"));
                //resultArray[i][27] = fmt.ToString(row.get("n_drawn_ccf"));
                //resultArray[i][28] = fmt.ToString(row.get("n_undrawn_ccf"));
                resultArray[i][27] = fmt.ToString(row.get("n_rw"));
                resultArray[i][28] = fmt.ToString(row.get("n_ead_rs"));
                resultArray[i][29] = fmt.ToString(row.get("n_rwa_rs"));
                resultArray[i][30] = fmt.ToString(row.get("n_nim"));
                resultArray[i][31] = fmt.ToString(row.get("n_nii"));
                resultArray[i][32] = fmt.ToString(row.get("n_amortized_fee"));
                resultArray[i][33] = fmt.ToString(row.get("n_oth_income"));
                resultArray[i][34] = fmt.ToString(row.get("n_opex"));
                resultArray[i][35] = fmt.ToString(row.get("n_el"));
                resultArray[i][36] = fmt.ToString(row.get("n_pbt"));
                resultArray[i][37] = fmt.ToString(row.get("n_alloc_capital"));
                resultArray[i][38] = fmt.ToString(row.get("n_adjusted_pat"));
                resultArray[i][39] = fmt.ToString(row.get("n_raroc"));
                i++;
            }
        }
        i = 0;
        while (i < cols) {
            if (i == cols - 1) {
                footer.setId(i + 1);
                footer.setOutputName(outputName[i]);
                footer.setFacility1(resultArray[0][i]);
                footer.setFacility2(resultArray[1][i]);
                footer.setFacility3(resultArray[2][i]);
                footer.setFacility4(resultArray[3][i]);
                footer.setFacility5(resultArray[4][i]);
                footer.setFacility6(resultArray[5][i]);
                footer.setFacility7(resultArray[6][i]);
                footer.setFacility8(resultArray[7][i]);
                footer.setFacility9(resultArray[8][i]);
                footer.setFacility10(resultArray[9][i]);
                footer.setFacility11(resultArray[10][i]);
                footer.setFacility12(resultArray[11][i]);
                footer.setFacility13(resultArray[12][i]);
                footer.setFacility14(resultArray[13][i]);
                footer.setFacility15(resultArray[14][i]);
                footer.setFacility16(resultArray[15][i]);
                footer.setFacility17(resultArray[16][i]);
                footer.setFacility18(resultArray[17][i]);
                footer.setFacility19(resultArray[18][i]);
                footer.setFacility20(resultArray[19][i]);
                footer.setFacility21(resultArray[20][i]);
                footer.setFacility22(resultArray[21][i]);
                footer.setFacility23(resultArray[22][i]);
                footer.setFacility24(resultArray[23][i]);
                footer.setFacility25(resultArray[24][i]);
                footer.setCreditRaroc(creditTotal[i]);
                footer.setTotal(total[i]);
            } else {
                RarocViewModel obj = new RarocViewModel();
                obj.setId(i + 1);
                obj.setOutputName(outputName[i]);
                obj.setFacility1(resultArray[0][i]);
                obj.setFacility2(resultArray[1][i]);
                obj.setFacility3(resultArray[2][i]);
                obj.setFacility4(resultArray[3][i]);
                obj.setFacility5(resultArray[4][i]);
                obj.setFacility6(resultArray[5][i]);
                obj.setFacility7(resultArray[6][i]);
                obj.setFacility8(resultArray[7][i]);
                obj.setFacility9(resultArray[8][i]);
                obj.setFacility10(resultArray[9][i]);
                obj.setFacility11(resultArray[10][i]);
                obj.setFacility12(resultArray[11][i]);
                obj.setFacility13(resultArray[12][i]);
                obj.setFacility14(resultArray[13][i]);
                obj.setFacility15(resultArray[14][i]);
                obj.setFacility16(resultArray[15][i]);
                obj.setFacility17(resultArray[16][i]);
                obj.setFacility18(resultArray[17][i]);
                obj.setFacility19(resultArray[18][i]);
                obj.setFacility20(resultArray[19][i]);
                obj.setFacility21(resultArray[20][i]);
                obj.setFacility22(resultArray[21][i]);
                obj.setFacility23(resultArray[22][i]);
                obj.setFacility24(resultArray[23][i]);
                obj.setFacility25(resultArray[24][i]);
                obj.setCreditRaroc(creditTotal[i]);
                obj.setTotal(total[i]);
                facs.add(obj);
            }
            i++;
        }
        return new GridWithFooterPage<>(facs, footer, 1, 40, 40);
    }

    @Override
    public GridWithFooterPage<RarocViewModel> gridRwaCalc(String ref) {
        List<RarocViewModel> facs = new ArrayList<>();
        RarocViewModel footer = new RarocViewModel();
        String query = "SELECT inpt.v_fac_type, inpt.n_tenure, inpt.n_amount, inpt.n_avg_utili, inpt.n_cash_margin, "
                + "mast.v_int_rating, inpt.v_long_ext, inpt.v_short_ext, inpt.n_exp_guar, inpt.v_guarantor_int, "
                + "inpt.v_guarantor_ext, inpt.n_cost_funds, inpt.n_int_rate_comm, inpt.n_upfront_fee, "
                + "rslt.n_drawn_ccf, rslt.n_undrawn_ccf, rslt.n_net_ead, rslt.n_ext_rating_rw, rslt.n_industry, "
                + "rslt.n_rw, rslt.n_rw_guarantor, rslt.n_rwa "
                + "FROM RSLT_RAROC RSLT LEFT OUTER JOIN INPT_RAROC inpt ON inpt.v_rec_ref_no = rslt.v_rec_ref_no "
                + "AND inpt.n_facility_no = rslt.n_facility_no "
                + "LEFT OUTER JOIN INPT_RAROC_MASTER mast ON mast.v_rec_ref_no = rslt.v_rec_ref_no "
                + "WHERE rslt.v_rec_ref_no = ? "
                + "AND   rslt.v_result_type = 'raroc' "
                + "ORDER BY rslt.n_facility_no nulls last";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{ref});
        int i = 0, cols = 22;
        String[][] resultArray = new String[6][cols];
        String[] outputName = new String[cols];
        String[] total = new String[cols];
        outputName[0] = "Facility Type";
        outputName[1] = "Tenure (in Months)";
        outputName[2] = "Amount";
        outputName[3] = "Average Utilization (%)";
        outputName[4] = "Cash Margin over utilized amt (%)";
        outputName[5] = "Internal Rating (Borrower / Guarantor)";
        outputName[6] = "Long-term External Rating (Borrower)";
        outputName[7] = "Short-term External Rating (Borrower)";
        outputName[8] = "Exposure Guaranteed (%)";
        outputName[9] = "Guarantor Internal Rating:";
        outputName[10] = "Guarantor External Rating";
        outputName[11] = "Cost of Funds (%)";
        outputName[12] = "Int Rate (%) / Commission (%)";
        outputName[13] = "Upfront Fee (in crores)";
        outputName[14] = "Credit Conversion Factor (CCF) - drawn";
        outputName[15] = "Credit Conversion Factor (CCF) - Undrawn";
        outputName[16] = "Exposure at Default (Net EAD)";
        outputName[17] = "Risk Weight as per external rating";
        outputName[18] = "Risk Weight as per asset class";
        outputName[19] = "Risk Weight";
        outputName[20] = "Risk Weight as per guarantor";
        outputName[21] = "Risk weighted asset (RWA = Net EAD * RW)";
        for (Map row : rows) {
            if (row.get("v_fac_type") == null) {
                total[0] = fmt.ToString(row.get("v_fac_type"));
                total[1] = fmt.ToString(row.get("n_tenure"));
                total[2] = fmt.ToString(row.get("n_amount"));
                total[3] = fmt.ToString(row.get("n_avg_utili"));
                total[4] = fmt.ToString(row.get("n_cash_margin"));
                total[5] = fmt.ToString(row.get("v_int_rating"));
                total[6] = fmt.ToString(row.get("v_long_ext"));
                total[7] = fmt.ToString(row.get("v_short_ext"));
                total[8] = fmt.ToString(row.get("n_exp_guar"));
                total[9] = fmt.ToString(row.get("v_guarantor_int"));
                total[10] = fmt.ToString(row.get("v_guarantor_ext"));
                total[11] = fmt.ToString(row.get("n_cost_funds"));
                total[12] = fmt.ToString(row.get("n_int_rate_comm"));
                total[13] = fmt.ToString(row.get("n_upfront_fee"));
                total[14] = fmt.ToString(row.get("n_drawn_ccf"));
                total[15] = fmt.ToString(row.get("n_undrawn_ccf"));
                total[16] = fmt.ToString(row.get("n_net_ead"));
                total[17] = fmt.ToString(row.get("n_ext_rating_rw"));
                total[18] = fmt.ToString(row.get("n_industry"));
                total[19] = fmt.ToString(row.get("n_rw"));
                total[20] = fmt.ToString(row.get("n_rw_guarantor"));
                total[21] = fmt.ToString(row.get("n_rwa"));
            } else {
                resultArray[i][0] = fmt.ToString(row.get("v_fac_type"));
                resultArray[i][1] = fmt.ToString(row.get("n_tenure"));
                resultArray[i][2] = fmt.ToString(row.get("n_amount"));
                resultArray[i][3] = fmt.ToString(row.get("n_avg_utili"));
                resultArray[i][4] = fmt.ToString(row.get("n_cash_margin"));
                resultArray[i][5] = fmt.ToString(row.get("v_int_rating"));
                resultArray[i][6] = fmt.ToString(row.get("v_long_ext"));
                resultArray[i][7] = fmt.ToString(row.get("v_short_ext"));
                resultArray[i][8] = fmt.ToString(row.get("n_exp_guar"));
                resultArray[i][9] = fmt.ToString(row.get("v_guarantor_int"));
                resultArray[i][10] = fmt.ToString(row.get("v_guarantor_ext"));
                resultArray[i][11] = fmt.ToString(row.get("n_cost_funds"));
                resultArray[i][12] = fmt.ToString(row.get("n_int_rate_comm"));
                resultArray[i][13] = fmt.ToString(row.get("n_upfront_fee"));
                resultArray[i][14] = fmt.ToString(row.get("n_drawn_ccf"));
                resultArray[i][15] = fmt.ToString(row.get("n_undrawn_ccf"));
                resultArray[i][16] = fmt.ToString(row.get("n_net_ead"));
                resultArray[i][17] = fmt.ToString(row.get("n_ext_rating_rw"));
                resultArray[i][18] = fmt.ToString(row.get("n_industry"));
                resultArray[i][19] = fmt.ToString(row.get("n_rw"));
                resultArray[i][20] = fmt.ToString(row.get("n_rw_guarantor"));
                resultArray[i][21] = fmt.ToString(row.get("n_rwa"));
                i++;
            }
        }
        i = 0;
        while (i < cols) {
            if (i == cols - 1) {
                footer.setId(i + 1);
                footer.setOutputName(outputName[i]);
                footer.setFacility1(resultArray[0][i]);
                footer.setFacility2(resultArray[1][i]);
                footer.setFacility3(resultArray[2][i]);
                footer.setFacility4(resultArray[3][i]);
                footer.setFacility5(resultArray[4][i]);
                footer.setFacility6(resultArray[5][i]);
                footer.setTotal(total[i]);
            } else {
                RarocViewModel obj = new RarocViewModel();
                obj.setId(i + 1);
                obj.setOutputName(outputName[i]);
                obj.setFacility1(resultArray[0][i]);
                obj.setFacility2(resultArray[1][i]);
                obj.setFacility3(resultArray[2][i]);
                obj.setFacility4(resultArray[3][i]);
                obj.setFacility5(resultArray[4][i]);
                obj.setFacility6(resultArray[5][i]);
                obj.setTotal(total[i]);
                facs.add(obj);
            }
            i++;
        }
        return new GridWithFooterPage<>(facs, footer, 1, 22, 22);
    }

    @Override
    public GridWithFooterPage<RarocViewModel> gridRarocCalc(String ref) {
        List<RarocViewModel> facs = new ArrayList<>();
        RarocViewModel footer = new RarocViewModel();
        String query = "SELECT rslt.v_rec_ref_no, inpt.v_fac_type, rslt.n_drawn_ccf, rslt.n_undrawn_ccf, rslt.n_rw, rslt.n_rwa_rs, "
                + "rslt.n_ead_rs, inpt.n_avg_utili, rslt.n_pd_guarantor, rslt.n_pd_borrower, rslt.n_el, "
                + "inpt.n_int_rate_comm, inpt.n_cost_funds, rslt.n_nim, rslt.n_nii, rslt.n_amortized_fee, "
                + "rslt.n_net_income, rslt.n_oth_income, rslt.n_opex, rslt.n_pbt, rslt.n_pat, "
                + "rslt.n_alloc_capital, rslt.n_income_alloc_capital, rslt.n_adjusted_pat, rslt.n_raroc "
                + "FROM RSLT_RAROC RSLT LEFT OUTER JOIN INPT_RAROC INPT ON inpt.v_rec_ref_no = rslt.v_rec_ref_no "
                + "AND inpt.n_facility_no = rslt.n_facility_no "
                + "LEFT OUTER JOIN INPT_RAROC_MASTER mast ON mast.v_rec_ref_no = rslt.v_rec_ref_no "
                + "WHERE rslt.v_rec_ref_no = ? "
                + "AND   rslt.v_result_type = 'raroc' "
                + "ORDER BY rslt.n_facility_no nulls last";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{ref});
        int i = 0, cols = 23;
        String[][] resultArray = new String[6][cols];
        String[] outputName = new String[cols];
        String[] total = new String[cols];
        outputName[0] = "Credit Conversion Factor (CCF) - drawn";
        outputName[1] = "Credit Conversion Factor (CCF) - undrawn";
        outputName[2] = "Risk Weight";
        outputName[3] = "RWA-Credit Risk";
        outputName[4] = "Net EAD";
        outputName[5] = "Average Utilisation (%)";
        outputName[6] = "Probability of Default (Guarantor)";
        outputName[7] = "Probability of Default (Borrower)";
        outputName[8] = "Expected Loss";
        outputName[9] = "Int Rate (%) / Commission (%)";
        outputName[10] = "Cost of Funds (%)";
        outputName[11] = "NIM / Commission (%)";
        outputName[12] = "NII";
        outputName[13] = "Amortized Fee Income";
        outputName[14] = "Net Income";
        outputName[15] = "Other Income-Op Profit";
        outputName[16] = "Operating Expense";
        outputName[17] = "PBT";
        outputName[18] = "PAT";
        outputName[19] = "Capital Allocated";
        outputName[20] = "Income on capital allocated";
        outputName[21] = "Adjusted PAT";
        outputName[22] = "RAROC (%)";
        for (Map row : rows) {
            if (row.get("v_fac_type") == null) {
                total[0] = fmt.ToString(row.get("n_drawn_ccf"));
                total[1] = fmt.ToString(row.get("n_undrawn_ccf"));
                total[2] = fmt.ToString(row.get("n_rw"));
                total[3] = fmt.ToString(row.get("n_rwa_rs"));
                total[4] = fmt.ToString(row.get("n_ead_rs"));
                total[5] = fmt.ToString(row.get("n_avg_utili"));
                total[6] = fmt.ToString(row.get("n_pd_guarantor"));
                total[7] = fmt.ToString(row.get("n_pd_borrower"));
                total[8] = fmt.ToString(row.get("n_el"));
                total[9] = fmt.ToString(row.get("n_int_rate_comm"));
                total[10] = fmt.ToString(row.get("n_cost_funds"));
                total[11] = fmt.ToString(row.get("n_nim"));
                total[12] = fmt.ToString(row.get("n_nii"));
                total[13] = fmt.ToString(row.get("n_amortized_fee"));
                total[14] = fmt.ToString(row.get("n_net_income"));
                total[15] = fmt.ToString(row.get("n_oth_income"));
                total[16] = fmt.ToString(row.get("n_opex"));
                total[17] = fmt.ToString(row.get("n_pbt"));
                total[18] = fmt.ToString(row.get("n_pat"));
                total[19] = fmt.ToString(row.get("n_alloc_capital"));
                total[20] = fmt.ToString(row.get("n_income_alloc_capital"));
                total[21] = fmt.ToString(row.get("n_adjusted_pat"));
                total[22] = fmt.ToString(row.get("n_raroc"));
            } else {
                resultArray[i][0] = fmt.ToString(row.get("n_drawn_ccf"));
                resultArray[i][1] = fmt.ToString(row.get("n_undrawn_ccf"));
                resultArray[i][2] = fmt.ToString(row.get("n_rw"));
                resultArray[i][3] = fmt.ToString(row.get("n_rwa_rs"));
                resultArray[i][4] = fmt.ToString(row.get("n_ead_rs"));
                resultArray[i][5] = fmt.ToString(row.get("n_avg_utili"));
                resultArray[i][6] = fmt.ToString(row.get("n_pd_guarantor"));
                resultArray[i][7] = fmt.ToString(row.get("n_pd_borrower"));
                resultArray[i][8] = fmt.ToString(row.get("n_el"));
                resultArray[i][9] = fmt.ToString(row.get("n_int_rate_comm"));
                resultArray[i][10] = fmt.ToString(row.get("n_cost_funds"));
                resultArray[i][11] = fmt.ToString(row.get("n_nim"));
                resultArray[i][12] = fmt.ToString(row.get("n_nii"));
                resultArray[i][13] = fmt.ToString(row.get("n_amortized_fee"));
                resultArray[i][14] = fmt.ToString(row.get("n_net_income"));
                resultArray[i][15] = fmt.ToString(row.get("n_oth_income"));
                resultArray[i][16] = fmt.ToString(row.get("n_opex"));
                resultArray[i][17] = fmt.ToString(row.get("n_pbt"));
                resultArray[i][18] = fmt.ToString(row.get("n_pat"));
                resultArray[i][19] = fmt.ToString(row.get("n_alloc_capital"));
                resultArray[i][20] = fmt.ToString(row.get("n_income_alloc_capital"));
                resultArray[i][21] = fmt.ToString(row.get("n_adjusted_pat"));
                resultArray[i][22] = fmt.ToString(row.get("n_raroc"));
                i++;
            }
        }
        i = 0;
        while (i < cols) {
            if (i == cols - 1) {
                footer.setId(i + 1);
                footer.setOutputName(outputName[i]);
                footer.setFacility1(resultArray[0][i]);
                footer.setFacility2(resultArray[1][i]);
                footer.setFacility3(resultArray[2][i]);
                footer.setFacility4(resultArray[3][i]);
                footer.setFacility5(resultArray[4][i]);
                footer.setFacility6(resultArray[5][i]);
                footer.setTotal(total[i]);
            } else {
                RarocViewModel obj = new RarocViewModel();
                obj.setId(i + 1);
                obj.setOutputName(outputName[i]);
                obj.setFacility1(resultArray[0][i]);
                obj.setFacility2(resultArray[1][i]);
                obj.setFacility3(resultArray[2][i]);
                obj.setFacility4(resultArray[3][i]);
                obj.setFacility5(resultArray[4][i]);
                obj.setFacility6(resultArray[5][i]);
                obj.setTotal(total[i]);
                facs.add(obj);
            }
            i++;
        }
        return new GridWithFooterPage<>(facs, footer, 1, 23, 23);
    }

    @Override
    public List<OptionsModel> getFacilitylist(String ref) {
        String query = "SELECT n_facility_no, 'Facility '||n_facility_no facility FROM RSLT_RAROC "
                + "WHERE v_rec_ref_no = ? "
                + "AND v_result_type = 'raroc' "
                + "AND n_facility_no IS NOT NULL "
                + "AND n_facility_no <> 99 "
                + "order by 1";
        List<OptionsModel> lists = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{ref});
        rows.stream().map((row) -> {
            OptionsModel oform = new OptionsModel();
            oform.setKey(fmt.ToString(row.get("n_facility_no")));
            oform.setValue(fmt.ToString(row.get("facility")));
            return oform;
        }).forEach((oform) -> {
            lists.add(oform);
        });
        return lists;
    }

    @Override
    public List<RarocViewModel> listSensitivityRW(String ref, String facility) {
        String query = "SELECT n_rw*100 rw, n_raroc*100 raroc FROM RSLT_RAROC "
                + "WHERE v_rec_ref_no = ? "
                + "AND v_result_type = 'rw' "
                + "AND n_facility_no = ? "
                + "ORDER BY n_rw";
        List<RarocViewModel> lists = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{ref, facility});
        rows.stream().map((row) -> {
            RarocViewModel obj = new RarocViewModel();
            obj.setOutputName(fmt.ToString(row.get("rw")));
            obj.setTotal(fmt.ToString(row.get("raroc")));
            return obj;
        }).forEach((obj) -> {
            lists.add(obj);
        });
        return lists;
    }

    @Override
    public List<RarocViewModel> listSensitivityUtil(String ref, String facility) {
        String query = "SELECT n_avg_util*100 util, n_raroc*100 raroc FROM RSLT_RAROC "
                + "WHERE v_rec_ref_no = ? "
                + "AND v_result_type = 'util_rate' "
                + "AND n_facility_no = ? "
                + "ORDER BY n_avg_util";
        List<RarocViewModel> lists = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{ref, facility});
        rows.stream().map((row) -> {
            RarocViewModel obj = new RarocViewModel();
            obj.setOutputName(fmt.ToString(row.get("util")));
            obj.setTotal(fmt.ToString(row.get("raroc")));
            return obj;
        }).forEach((obj) -> {
            lists.add(obj);
        });
        return lists;
    }

    @Override
    public List<OptionsModel> listBu() {
        String query = "SELECT v_code, v_desc FROM MST_BUSINESS_UNIT";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_code")));
            obj.setValue(fmt.ToString(row.get("v_desc")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listModel() {
        String query = "SELECT distinct  M_RDM_MODEL FROM CNFG_RATING_MODEL_MAP";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("M_RDM_MODEL")));
            obj.setValue(fmt.ToString(row.get("M_RDM_MODEL")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listInternalRating(String model) {
        String query = "SELECT distinct n_rank, v_int_rating FROM MAST_INTERNAL_RATING "
                + "WHERE v_model = (SELECT distinct V_SEGMENT FROM CNFG_RATING_MODEL_MAP Where M_RDM_MODEL = ? ) AND v_rating_type = 'B' ORDER BY n_rank";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{model});
        List<OptionsModel> lObj = new ArrayList<>();
        rows.stream().map((row) -> {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_int_rating")));
            obj.setValue(fmt.ToString(row.get("v_int_rating")));
            return obj;
        }).forEach((obj) -> {
            lObj.add(obj);
        });
        return lObj;
    }

    @Override
    public List<OptionsModel> listGuarIntRating(String model) {
        String query = "SELECT distinct n_rank, v_int_rating FROM MAST_INTERNAL_RATING "
                + " WHERE f_eligible_guarantor = 'Y' AND v_guarantor_type = (SELECT DISTINCT V_MAPPING_COL FROM MST_GUARANTOR WHERE V_CODE = ? ) ORDER BY n_rank";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{model});
        List<OptionsModel> lObj = new ArrayList<>();
        rows.stream().map((row) -> {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_int_rating")));
            obj.setValue(fmt.ToString(row.get("v_int_rating")));
            return obj;
        }).forEach((obj) -> {
            lObj.add(obj);
        });
        return lObj;
    }

    @Override
    public List<OptionsModel> listIndustry() {
        String query = "SELECT v_code FROM MAST_REPOSITORY_FORM WHERE v_desc = 'INDUSTRY'";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_code")));
            obj.setValue(fmt.ToString(row.get("v_code")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listBenchmark() {
//        String query = "SELECT n_interest_rate_term||v_interest_rate_term_unit rates, n_interest_rate_term||' '||case "
//                + "when v_interest_rate_term_unit = 'D' THEN 'Day' WHEN v_interest_rate_term_unit = 'M' "
//                + "then 'Month' else 'Year' end rate_desc FROM MST_IRC_RATE WHERE v_irc_name = 'LIBOR USD' "
//                + "ORDER BY v_interest_rate_term_unit, n_interest_rate_term";

        String query = " Select rates,rate_desc from ( "
                + " Select Distinct * from ( "
                + " Select interest_rate_term, interest_rate_term_mult, interest_rate_term||interest_rate_term_mult rates, interest_rate_term||' '||case  "
                + " when interest_rate_term_mult = 'D' THEN 'Day' WHEN interest_rate_term_mult = 'M'  "
                + " then 'Month' else 'Year' end rate_desc FROM FSI_IRC_RATE_HIST Where EFFECTIVE_DATE = (SELECT MAX(EFFECTIVE_DATE) FROM FSI_IRC_RATE_HIST) "
                + " ORDER BY interest_rate_term, interest_rate_term_mult) order by 2,1)";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("rates")));
            obj.setValue(fmt.ToString(row.get("rate_desc")));
            lObj.add(obj);
        }
        return lObj;
    }

//    @Override
//    public List<String> listCustName(String value) {
//        String query = "SELECT distinct v_cust_name FROM CUSTOMER WHERE upper(v_cust_name) LIKE upper(?)";
//        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{"%" + value + "%"});
//        List<String> lObj = new ArrayList<>();
//        rows.stream().forEach((row) -> {
//            lObj.add(fmt.ToString(row.get("v_cust_name")));
//        });
//        return lObj;
//    }
    @Override
    public List<OptionsModel> listCustName(String value) {
        String query = "SELECT distinct V_TOOL_CODE COMP_COMPCODE FROM t_rdm_ram_cust_mast WHERE upper(V_TOOL_CODE) LIKE upper(?)";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{"%" + value + "%"});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("COMP_COMPCODE")));
            obj.setValue(fmt.ToString(row.get("COMP_COMPCODE")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public String getSequence() {
        String query = "SELECT FACILITY_REF_SEQ.NEXTVAL FROM DUAL";
        return getJdbcTemplate().queryForObject(query, new Object[]{}, String.class);
    }

    @Override
    public void addRarocMaster(RarocMasterModel model, String user) {
        model.setUnit(100000);
        String query = " INSERT INTO INPT_RAROC_MASTER (v_rec_ref_no, v_cust_name, n_facility, v_cust_id,  "
                + " v_rating_tool_code, v_int_rating, v_rating_tool_id, v_cps_id, v_industry, v_business_unit,  "
                + " v_created, d_created, f_status, n_ebid, n_uhfce, v_exp_banks, n_non_financial_coll, N_BA_CDAB, N_TERM_DEPO, N_CA_FEE, N_CMS_FEE, N_OTHER,N_FOREX_COMMIS, V_PAN_NO, V_CIF_ID, "
                + " N_PRO_AXIS_FUND, N_NPLL, F_WORK_CAP, V_CUST_TYPE)  "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, ?, ?, ?, ?, ?, ?, ? , ?, ? , ?, ?, ?, ?, ?/100, ?, ?, ?) ";
        getJdbcTemplate().update(query, new Object[]{
            model.getRarocref(),
            model.getCname(),
            model.getFacility(),
            model.getCid(),
            model.getRtool(),
            model.getIntRat(),
            model.getRid(),
            model.getCpsid(), model.getInd(),
            model.getBussunit(), user, "I", Double.parseDouble(model.getEbid()) * model.getUnit(),
            Double.parseDouble(model.getUfce()) * model.getUnit(), model.getExpBanks(),
            Double.parseDouble(model.getNonFinColl()) * model.getUnit(),
            Double.parseDouble(model.getBbcdab()) * model.getUnit(),
            Double.parseDouble(model.getTdfee()) * model.getUnit(),
            Double.parseDouble(model.getCafee()) * model.getUnit(),
            Double.parseDouble(model.getCms()) * model.getUnit(),
            Double.parseDouble(model.getOther()) * model.getUnit(),
            Double.parseDouble(model.getForex()) * model.getUnit(), model.getPan(), model.getCif(),
            model.getPortion(),
            Double.parseDouble(model.getNpll()) * model.getUnit(),
            model.getWorkcap(),
            model.getCustType()
        });
    }

    @Override
    public void editRarocMaster(RarocMasterModel model, String user) {
        String query = "UPDATE INPT_RAROC_MASTER SET  n_facility = ?, "
                + "v_cust_id = ?, v_rating_tool_code = ?, v_int_rating = ?, v_cps_id = ?, "
                + "v_industry = ?, v_business_unit = ?, v_modified = ?, d_modified = sysdate, "
                + "n_ebid = ?, n_uhfce = ?, v_exp_banks = ?, N_BA_CDAB = ?, N_TERM_DEPO  = ?, N_CA_FEE = ?, N_CMS_FEE = ?, N_OTHER = ?, N_FOREX_COMMIS = ?, V_PAN_NO = ?, V_CIF_ID = ?, "
                + "N_PRO_AXIS_FUND = ?/100, N_NON_FINANCIAL_COLL = ?, N_NPLL = ?, F_WORK_CAP = ? "
                + " WHERE v_rec_ref_no = ? AND v_created = ?";
        getJdbcTemplate().update(query, new Object[]{model.getFacility(),
            model.getCid(), model.getRtool(), model.getIntRat(), model.getCpsid(), model.getInd(),
            model.getBussunit(), user, Double.parseDouble(model.getEbid()) * model.getUnit(),
            Double.parseDouble(model.getUfce()) * model.getUnit(), model.getExpBanks(),
            Double.parseDouble(model.getBbcdab()) * model.getUnit(),
            Double.parseDouble(model.getTdfee()) * model.getUnit(),
            Double.parseDouble(model.getCafee()) * model.getUnit(),
            Double.parseDouble(model.getCms()) * model.getUnit(),
            Double.parseDouble(model.getOther()) * model.getUnit(),
            Double.parseDouble(model.getForex()) * model.getUnit(),
            model.getPan(), model.getCif(),
            model.getPortion(),
            Double.parseDouble(model.getNonFinColl()) * model.getUnit(),
            Double.parseDouble(model.getNpll()) * model.getUnit(),
            model.getWorkcap(),
            model.getRarocref(), user});

        String sql = "UPDATE INPT_RAROC SET  N_BA_CDAB = ?, N_TERM_DEPO  = ?, N_CA_FEE = ?, N_CMS_FEE = ?, N_OTHER = ?, N_FOREX_COMMIS = ? "
                + " WHERE v_rec_ref_no = ?";
        getJdbcTemplate().update(sql, new Object[]{
            Double.parseDouble(model.getBbcdab()) * model.getUnit(),
            Double.parseDouble(model.getTdfee()) * model.getUnit(),
            Double.parseDouble(model.getCafee()) * model.getUnit(),
            Double.parseDouble(model.getCms()) * model.getUnit(),
            Double.parseDouble(model.getOther()) * model.getUnit(),
            Double.parseDouble(model.getForex()) * model.getUnit(),
            model.getRarocref()});

    }

    @Override
    public int delRarocMaster(String recref, String userid) {
        String query = "DELETE FROM INPT_RAROC_MASTER WHERE v_rec_ref_no = ? "
                + "AND f_status = 'I' AND v_created = ?";
        return getJdbcTemplate().update(query, new Object[]{recref, userid});
    }

    @Override
    public void delRarocInput(String recref) {
        String query = "DELETE FROM INPT_RAROC WHERE v_rec_ref_no = ?";
        getJdbcTemplate().update(query, new Object[]{recref});
    }

    @Override
    public void delRarocResults(String recref) {
        String query = "DELETE FROM RSLT_RAROC WHERE v_rec_ref_no = ?";
        getJdbcTemplate().update(query, new Object[]{recref});
    }

    @Override
    public List<OptionsModel> listFacility() {
        String query = "SELECT distinct v_code FROM MST_CCF WHERE V_FACILITY_TYPE = 'FB' ORDER BY v_code";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_code")));
            obj.setValue(fmt.ToString(row.get("v_code")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listBondExtRat() {
        String query = "SELECT DISTINCT V_EXT_RATING_CD from MST_EXT_RATING_BOND ORDER BY 1";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("V_EXT_RATING_CD")));
            obj.setValue(fmt.ToString(row.get("V_EXT_RATING_CD")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listBondCet() {
        String query = "SELECT distinct V_HOR FROM MST_BANK_BOND ORDER BY 1";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("V_HOR")));
            obj.setValue(fmt.ToString(row.get("V_HOR")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listFacility_bonds() {

        String query = "SELECT distinct v_code FROM MST_CCF WHERE V_FACILITY_TYPE = 'BONDS' ORDER BY v_code";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_code")));
            obj.setValue(fmt.ToString(row.get("v_code")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listFacility_nfb() {
        String query = "SELECT distinct v_code FROM MST_CCF WHERE V_FACILITY_TYPE = 'NFB' ORDER BY v_code";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_code")));
            obj.setValue(fmt.ToString(row.get("v_code")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listAsset() {
        String query = "SELECT distinct v_code FROM MST_ASSET_TYPE ORDER BY v_code";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_code")));
            obj.setValue(fmt.ToString(row.get("v_code")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listCurrency() {
        String query = "SELECT distinct v_code FROM MST_CURR_TYPE ORDER BY decode(v_code,'INR',1,99)";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        rows.stream().map((row) -> {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_code")));
            obj.setValue(fmt.ToString(row.get("v_code")));
            return obj;
        }).forEach((obj) -> {
            lObj.add(obj);
        });
        return lObj;
    }

    @Override
    public List<OptionsModel> listRestructured() {
        String query = "SELECT v_code FROM MST_RESTRUCTURED_RW ORDER BY decode(v_code,'Not Restructured',1,2) asc";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        rows.stream().map((row) -> {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_code")));
            obj.setValue(fmt.ToString(row.get("v_code")));
            return obj;
        }).forEach((obj) -> {
            lObj.add(obj);
        });
        return lObj;
    }

    @Override
    public List<OptionsModel> listLongExt() {
        String query = "SELECT DISTINCT v_rank, v_rating_code FROM MAST_EXTERNAL_RATING "
                + "WHERE f_term_flag = 'L' ORDER BY v_rank";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_rating_code")));
            obj.setValue(fmt.ToString(row.get("v_rating_code")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listLongExt(String astType) {
        String query = "SELECT DISTINCT v_rank, v_rating_code FROM MAST_EXTERNAL_RATING "
                + "WHERE f_term_flag = 'L' AND v_guarantor_type = ? ORDER BY v_rank";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{astType});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_rating_code")));
            obj.setValue(fmt.ToString(row.get("v_rating_code")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listShortExt() {
        String query = "SELECT DISTINCT v_rank, v_rating_code FROM MAST_EXTERNAL_RATING "
                + "WHERE f_term_flag = 'S' ORDER BY v_rank";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_rating_code")));
            obj.setValue(fmt.ToString(row.get("v_rating_code")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listGuarType() {
        String query = "SELECT CASE WHEN V_MAPPING_COL is NULL then 'No'|| '~'||v_code else 'Yes'|| '~'||v_code End flg ,v_code FROM MST_GUARANTOR "
                + "ORDER BY decode(v_code,'No Guarantor/Ineligible',1,2)";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        rows.stream().map((row) -> {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("flg")));
            obj.setValue(fmt.ToString(row.get("v_code")));
            return obj;
        }).forEach((obj) -> {
            lObj.add(obj);
        });
        return lObj;
    }

    @Override
    public List<OptionsModel> listInternalRating() {
        String query = "SELECT distinct n_rank, v_int_rating FROM MAST_INTERNAL_RATING "
                + "WHERE v_rating_type = 'B' "
                + "AND v_model <> 'Templated' "
                + "ORDER BY 1";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_int_rating")));
            obj.setValue(fmt.ToString(row.get("v_int_rating")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public List<OptionsModel> listMappedExternalRating(String model, String rating) {
        String query = "SELECT distinct n_rank, v_mapped_ext_rating FROM MAST_INTERNAL_RATING "
                + "WHERE v_rating_type = 'B' "
                + "AND v_model = ? "
                + "AND v_int_rating = ? "
                + "ORDER BY 1";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{model, rating});
        List<OptionsModel> lObj = new ArrayList<>();
        rows.stream().map((row) -> {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_mapped_ext_rating")));
            obj.setValue(fmt.ToString(row.get("v_mapped_ext_rating")));
            return obj;
        }).forEach((obj) -> {
            lObj.add(obj);
        });
        return lObj;
    }

    @Override
    public List<OptionsModel> listFinHaircut() {
        String query = "SELECT v_fin_sec FROM MST_FIN_HAIRCUT "
                + "ORDER BY decode(v_fin_sec,'Deposit in same currency',1,'Deposit in different currency',2,3) asc";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query);
        List<OptionsModel> lObj = new ArrayList<>();
        rows.stream().map((row) -> {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_fin_sec")));
            obj.setValue(fmt.ToString(row.get("v_fin_sec")));
            return obj;
        }).forEach((obj) -> {
            lObj.add(obj);
        });
        return lObj;
    }

    @Override
    public String getCurveDate() {
        String query = "SELECT to_char(max(d_effective_date),'DD Mon yyyy') FROM MST_IRC_RATE";
        return getJdbcTemplate().queryForObject(query, new Object[]{}, String.class);
    }

    @Override
    public String getBA_CDAB(String refId) {
        String query = "SELECT N_BA_CDAB FROM INPT_RAROC_MASTER WHERE V_REC_REF_NO = ? ";
        return getJdbcTemplate().queryForObject(query, new Object[]{refId}, String.class);
    }

    @Override
    public String getTermDepo(String refId) {
        String query = "SELECT N_TERM_DEPO FROM INPT_RAROC_MASTER WHERE V_REC_REF_NO = ? ";
        return getJdbcTemplate().queryForObject(query, new Object[]{refId}, String.class);
    }

    @Override
    public String getBB_FEE(String refId) {
        String query = "SELECT N_FOREX_COMMIS FROM INPT_RAROC_MASTER WHERE V_REC_REF_NO = ? ";
        return getJdbcTemplate().queryForObject(query, new Object[]{refId}, String.class);
    }

    @Override
    public String getTresFee(String refId) {
        String query = "SELECT N_CA_FEE FROM INPT_RAROC_MASTER WHERE V_REC_REF_NO = ? ";
        return getJdbcTemplate().queryForObject(query, new Object[]{refId}, String.class);
    }

    @Override
    public String getCMSFee(String refId) {
        String query = "SELECT N_CMS_FEE FROM INPT_RAROC_MASTER WHERE V_REC_REF_NO = ? ";
        return getJdbcTemplate().queryForObject(query, new Object[]{refId}, String.class);
    }

    @Override
    public String getOther(String refId) {
        String query = "SELECT N_OTHER FROM INPT_RAROC_MASTER WHERE V_REC_REF_NO = ? ";
        return getJdbcTemplate().queryForObject(query, new Object[]{refId}, String.class);
    }

    // Add RAROC into table modified the base function take input variable i from front end
    @Override
    public void addRarocDetails(RarocInputModel model) {
        model.setUnit(100000);
        String query = "INSERT INTO INPT_RAROC "
                + " (V_FAC_DESC,V_FAC_TYPE,N_AMOUNT,V_TEMPLATED_RATING,N_AVG_BAL_CUR,N_AVG_BAL_NEXT,N_AVG_UTL_CUR, "
                + " N_AVG_UTL_NEXT,V_UCICF,V_RESTRUCTURED_STATUS,D_ORIGIN_DATE,N_ORIGINAL_MATURITY,N_TENURE,V_REPRICE_FREQ, "
                + " N_ANNUAL_FEE,V_ASSET_TYPE,V_PSL,V_CURR,N_EXCGE_RATE,V_INT_TYPE,V_BENCHMARK,N_INT_RATE_COMM,N_COST_FUNDS, "
                + " N_UPFRONT_FEE,N_SYNDICATION_FEE,V_COMP_SECURED,V_REGION,N_CASH_MARGIN,V_CASH_MISMATCH,V_GUAR_TYPE,N_EXP_GUAR, "
                + " V_GUARANTOR_EXT,V_GUARANTOR_INT,V_LONG_EXT,V_SHORT_EXT,V_EXT_RATED,V_GIFT_CITY,N_BA_CDAB,N_TERM_DEPO,N_CA_FEE, "
                + " N_CMS_FEE,N_FOREX_COMMIS,N_OTHER,N_FACILITY_NO,V_REC_REF_NO,N_CCY_MISMATCH, V_FACILITY_TYPE) "
                + " VALUES "
                + " (?,?,NVL(?,0),?,?*100000,?*100000,?/100,?/100,?,?,?,NVL(?,0), "
                + " NVL(?,0),?,NVL(?,0)/100,?,?,?, NVL(?,0) ,?,?,NVL(?,0)/100,NVL(?,0)/100,NVL(?,0), "
                + " NVL(?,0),?,?,NVL(?,0)/100,?,?,NVL(?,0)/100,?,?,?,?,?,?,NVL(?,0),NVL(?,0),NVL(?,0), "
                + " NVL(?,0),NVL(?,0),NVL(?,0),(SELECT NVL(MAX(N_FACILITY_NO),0)+1 FROM INPT_RAROC WHERE V_REC_REF_NO = ?),?, "
                + " (SELECT n_haircut FROM MST_FIN_HAIRCUT WHERE v_fin_sec = ?), ?) ";
        String[] guar = model.getGuarType().split("~");
        String gType = guar[1];
        getJdbcTemplate().update(query, new Object[]{
            model.getFacDesc(),
            model.getFacType(),
            Double.parseDouble(model.getAmount()) * model.getUnit(),
            model.gettRating(),
            model.getCurfy(),
            model.getNextfy(),
            model.getAvgfy(),
            model.getAvgnextfy(),
            model.getUcicf(),
            model.getRestStatus(),
            model.getOridate(),
            model.getMaturity(),
            model.getTenure(),
            model.getRefreq(),
            model.getaFee(),
            model.getAstType(),
            model.getPsl(),
            model.getCur(),
            model.getExgRate(),
            model.getInttype(),
            model.getBenchmark(),
            model.getIntRate(),
            model.getCostFunds(),
            Double.parseDouble(model.getuFee()) * model.getUnit(),
            Double.parseDouble(model.getSynFee()) * model.getUnit(),
            model.getcSecured(),
            model.getRegion(),
            model.getcMargin(),
            model.getcMarginCurr(),
            gType,
            model.geteGuar(),
            model.getGuarExtRat(),
            model.getGuarIntRat(),
            model.getLtRating(),
            model.getStRating(),
            model.getExtRated(),
            model.getGiftCity(),
            Double.parseDouble(model.getBbcdab()) * model.getUnit(),
            Double.parseDouble(model.getTdfee()) * model.getUnit(),
            Double.parseDouble(model.getCafee()) * model.getUnit(),
            Double.parseDouble(model.getCms()) * model.getUnit(),
            Double.parseDouble(model.getForex()) * model.getUnit(),
            Double.parseDouble(model.getOther()) * model.getUnit(),
            model.getRefrec(),
            model.getRefrec(),
            model.getcMarginCurr(), "FB"
        });

        /*
        if (model.getFacType().equals("Derivative")) {
            procName[i] = "CVA_ADD";
            totProcName = "CVA_ADD_TOTAL";
        } else {
            procName[i] = "RAROC_ADD";
            totProcName = "RAROC_ADD_TOTAL";
        }        
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                .withSchemaName(schemaName)
                .withProcedureName("RAROC_LGD_CALC")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("refrec", java.sql.Types.VARCHAR));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("refrec", model.getRefrec());
        jdbcCall.execute(map);

        for (i = i - 1; i >= 1; i--) {
            jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                    .withSchemaName(schemaName)
                    .withProcedureName(procName[i])
                    .withoutProcedureColumnMetaDataAccess()
                        .declareParameters(new SqlParameter("refrec", java.sql.Types.VARCHAR),
                            new SqlParameter("facilityId", java.sql.Types.VARCHAR));

            map = new HashMap<String, Object>();
            map.put("refrec", model.getRefrec());
            map.put("facilityId", i);
            jdbcCall.execute(map);
        }

        jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                .withSchemaName(schemaName)
                .withProcedureName(totProcName)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("refrec", java.sql.Types.VARCHAR));
        map = new HashMap<String, Object>();
        map.put("refrec", model.getRefrec());
        jdbcCall.execute(map);

         */
    }

    @Override
    public void addRarocDetails_bonds(RarocInputModel model) {
        model.setUnit(100000);
        String query = "INSERT INTO INPT_RAROC (V_BOOK_TYPE, V_REC_REF_NO,N_FACILITY_NO,V_FAC_DESC,V_FAC_TYPE,N_AMOUNT,N_RESI_TENOR,N_YEILD,"
                + "     V_COUPON_FREQ, V_REGION, V_EXT_RATING, V_LCR, N_UPFRONT_FEE, N_EXP_TRADE, V_CURR,  N_EXCGE_RATE, V_FACILITY_TYPE, N_COUPON, N_BOND_TENURE, N_BOND_HOLD_PRD)"
                + "     VALUES (?, ?,(SELECT NVL(MAX(N_FACILITY_NO),0)+1 FROM INPT_RAROC WHERE V_REC_REF_NO = ?),?,?,nvl(?,0),nvl(?,0),nvl(?,0)/100    ,"
                + "     ?, ? , ?, ?, nvl(?,0), nvl(?,0), ?, nvl(?,0), ?, nvl(?,0)/100, ? , ?)    ";
        getJdbcTemplate().update(query, new Object[]{
            model.getBookType(),
            model.getRefrec(),
            model.getRefrec(),
            model.getFacDesc(),
            model.getFacType(),
            Double.parseDouble(model.getAmount()) * model.getUnit(),
            model.getrTenure(),
            model.getYeild(),
            model.getCoupon(),
            model.getRegion(),
            model.getCetExt(),
            model.getLcr(),
            Double.parseDouble(model.getUpfrontFee()) * model.getUnit(),
            Double.parseDouble(model.getExpIncome()) * model.getUnit(),
            model.getCur(),
            model.getExgRate(),
            "BONDS",
            model.getCouponBond(),
            model.getBondTenure(),
            model.getTentative()
        });
    }

    @Override
    public void updateRarocDetails_bonds(RarocInputModel model) {
        model.setUnit(100000);
        String query = " UPDATE INPT_RAROC SET V_BOOK_TYPE = ?, V_FAC_DESC = ?,V_FAC_TYPE = ?,N_AMOUNT = NVL(?,0), "
                + " N_RESI_TENOR = NVL(?,0),N_YEILD = NVL(?,0)/100,V_COUPON_FREQ = ?,V_REGION = ?,V_EXT_RATING = ?, "
                + " V_LCR = ?, N_UPFRONT_FEE = NVL(?,0),N_EXP_TRADE = NVL(?,0),V_CURR = ?,N_EXCGE_RATE = NVL(?,0),  "
                + " N_COUPON = NVL(?,0)/100, N_BOND_TENURE = ?, N_BOND_HOLD_PRD = ? WHERE V_REC_REF_NO = ? AND N_FACILITY_NO = ?";
        getJdbcTemplate().update(query, new Object[]{
            model.getBookType(),
            model.getFacDesc(),
            model.getFacType(),
            Double.parseDouble(model.getAmount()) * model.getUnit(),
            model.getrTenure(),
            model.getYeild(),
            model.getCoupon(),
            model.getRegion(),
            model.getCetExt(),
            model.getLcr(),
            Double.parseDouble(model.getUpfrontFee()) * model.getUnit(),
            Double.parseDouble(model.getExpIncome()) * model.getUnit(),
            model.getCur(),
            model.getExgRate(),
            model.getCouponBond(),
            model.getBondTenure(),
            model.getTentative(),
            model.getRefrec(),
            model.getFacNo(),});
    }

    @Override
    public void addRarocDetails_nfb(RarocInputModel model) {
        model.setUnit(100000);
        String query = " INSERT INTO INPT_RAROC (V_FAC_DESC,V_FAC_TYPE,N_AMOUNT,V_TEMPLATED_RATING,N_AVG_UTL_CUR,N_AVG_UTL_NEXT,V_UCICF,V_RESTRUCTURED_STATUS, "
                + " N_ORIGINAL_MATURITY,N_AVG_MATURITY,V_ASSET_TYPE,V_CURR,N_EXCGE_RATE,N_COMMISSION,N_UPFRONT_FEE,N_SYNDICATION_FEE,V_COMP_SECURED,V_REGION, "
                + " N_CASH_MARGIN,V_CASH_MISMATCH,V_GUAR_TYPE,N_EXP_GUAR,V_GUARANTOR_EXT,V_GUARANTOR_INT,V_LONG_EXT,V_SHORT_EXT,V_EXT_RATED,V_GIFT_CITY, "
                + " N_BA_CDAB,N_TERM_DEPO,N_CA_FEE,N_CMS_FEE,N_FOREX_COMMIS,N_OTHER,N_FACILITY_NO,V_REC_REF_NO,N_CCY_MISMATCH, V_FACILITY_TYPE) "
                + " VALUES "
                + " (?,?,NVL(?,0),?,NVL(?,0)/100,NVL(?,0)/100,?,?,NVL(?,0),NVL(?,0),?,?,NVL(?,0),NVL(?,0)/100,NVL(?,0),NVL(?,0),?, "
                + " ?,NVL(?,0)/100,?,?,NVL(?,0)/100,?,?,?,?,?,?,NVL(?,0),NVL(?,0),NVL(?,0),NVL(?,0),NVL(?,0),NVL(?,0),(SELECT NVL(MAX(N_FACILITY_NO),0)+1 FROM INPT_RAROC WHERE V_REC_REF_NO = ?),?,((SELECT n_haircut FROM MST_FIN_HAIRCUT WHERE v_fin_sec = ?)),?) ";
        String[] guar = model.getGuarType().split("~");
        String gType = guar[1];
        getJdbcTemplate().update(query, new Object[]{model.getFacDesc(),
            model.getFacType(),
            Double.parseDouble(model.getAmount()) * model.getUnit(),
            model.gettRating(),
            model.getAvgfy(),
            model.getAvgnextfy(),
            model.getUcicf(),
            model.getRestStatus(),
            model.getMaturity(),
            model.getAvgMat(),
            model.getAstType(),
            model.getCur(),
            model.getExgRate(),
            model.getIntRate(),
            Double.parseDouble(model.getuFee()) * model.getUnit(),
            Double.parseDouble(model.getSynFee()) * model.getUnit(),
            model.getcSecured(),
            model.getRegion(),
            model.getcMargin(),
            model.getcMarginCurr(),
            gType,
            model.geteGuar(),
            model.getGuarExtRat(),
            model.getGuarIntRat(),
            model.getLtRating(),
            model.getStRating(),
            model.getExtRated(),
            model.getGiftCity(),
            Double.parseDouble(model.getBbcdab()) * model.getUnit(),
            Double.parseDouble(model.getTdfee()) * model.getUnit(),
            Double.parseDouble(model.getCafee()) * model.getUnit(),
            Double.parseDouble(model.getCms()) * model.getUnit(),
            Double.parseDouble(model.getForex()) * model.getUnit(),
            Double.parseDouble(model.getOther()) * model.getUnit(),
            model.getRefrec(),
            model.getRefrec(),
            model.getcMarginCurr(), "NFB"});
    }

    @Override
    public List<String> listFacNumber(String refRec) {
        String query = "SELECT n_facility_no FROM INPT_RAROC WHERE V_REC_REF_NO = ? ORDER BY n_facility_no asc";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{refRec});
        List<String> lObj = new ArrayList<>();
        rows.stream().forEach((row) -> {
            lObj.add(fmt.ToString(row.get("n_facility_no")));
        });
        return lObj;
    }

    @Override
    public void submitRarocDetails(String refRec, String facType) {

        String procName = "";
        String totProcName = "";

        if (facType.equals("Derivative")) {
            procName = "CVA_ADD";
            totProcName = "CVA_ADD_TOTAL";
        } else {
            procName = "RAROC_ADD";
            totProcName = "RAROC_ADD_TOTAL";
        }

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                .withSchemaName(schemaName)
                .withProcedureName("RAROC_LGD_CALC")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("refrec", java.sql.Types.VARCHAR));

        Map<String, Object> map = new HashMap<>();
        map.put("refrec", refRec);
        jdbcCall.execute(map);

        // Delete the existing data from RSLT_RAROC table for ref id
        String query = "DELETE FROM RSLT_RAROC WHERE v_rec_ref_no = ?";
        getJdbcTemplate().update(query, new Object[]{refRec});

        // Delete the existing data from RSLT_RAROC table for ref id
        String query1 = "DELETE FROM RSLT_RAROC_NEXT WHERE v_rec_ref_no = ?";
        getJdbcTemplate().update(query1, new Object[]{refRec});

        List<String> lst = listFacNumber(refRec);

        for (String number : lst) {
            jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                    .withSchemaName(schemaName)
                    .withProcedureName(procName)
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(new SqlParameter("refrec", java.sql.Types.VARCHAR),
                            new SqlParameter("facilityId", java.sql.Types.VARCHAR));
            map = new HashMap<>();
            map.put("refrec", refRec);
            map.put("facilityId", number);
            jdbcCall.execute(map);
        }

        jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                .withSchemaName(schemaName)
                .withProcedureName(totProcName)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("refrec", java.sql.Types.VARCHAR));
        map = new HashMap<>();
        map.put("refrec", refRec);
        jdbcCall.execute(map);

        System.out.println("Finished");
    }

    @Override
    public void submitRarocDetailsNext(String refRec, String facType) {

        String procName = "";
        String totProcName = "";

        if (facType.equals("Derivative")) {
            procName = "CVA_ADD";
            totProcName = "CVA_ADD_TOTAL";
        } else {
            procName = "RAROC_ADD_NEXT";
            totProcName = "RAROC_ADD_TOTAL_NEXT";
        }

        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                .withSchemaName(schemaName)
                .withProcedureName("RAROC_LGD_CALC_NEXT")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("refrec", java.sql.Types.VARCHAR));

        Map<String, Object> map = new HashMap<>();
        map.put("refrec", refRec);
        jdbcCall.execute(map);

        // Delete the existing data from RSLT_RAROC table for ref id
        String query = "DELETE FROM RSLT_RAROC_NEXT WHERE v_rec_ref_no = ?";
        getJdbcTemplate().update(query, new Object[]{refRec});

        List<String> lst = listFacNumber(refRec);

        for (String number : lst) {
            jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                    .withSchemaName(schemaName)
                    .withProcedureName(procName)
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(new SqlParameter("refrec", java.sql.Types.VARCHAR),
                            new SqlParameter("facilityId", java.sql.Types.VARCHAR));
            map = new HashMap<>();
            map.put("refrec", refRec);
            map.put("facilityId", number);
            jdbcCall.execute(map);
        }

        jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                .withSchemaName(schemaName)
                .withProcedureName(totProcName)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlParameter("refrec", java.sql.Types.VARCHAR));
        map = new HashMap<>();
        map.put("refrec", refRec);
        jdbcCall.execute(map);

        System.out.println("Finished");
    }

    @Override
    public RarocInputModel getRarocInput(String recref, Integer unit) {
        String query = "SELECT inpt.v_fac_type, inpt.v_fac_desc, inpt.v_asset_type, inpt.v_curr, "
                + "inpt.n_amount/ " + unit + " n_amount, inpt.n_tenure, inpt.n_excge_rate, "
                + "nvl(inpt.n_avg_utili,0)*100 n_avg_utili, nvl(inpt.n_int_rate_comm,0)*100 n_int_rate_comm, "
                + "nvl(inpt.n_cost_funds,0)*100 n_cost_funds, inpt.n_upfront_fee/ " + unit + " n_upfront_fee, "
                + "nvl(inpt.n_annual_fee,0)*100 n_annual_fee, nvl(inpt.n_cash_margin,0)*100 n_cash_margin, "
                + "inpt.v_cash_mismatch, inpt.v_long_ext, inpt.v_short_ext, nvl(inpt.n_exp_guar,0)*100 n_exp_guar, "
                + "inpt.v_guarantor_int, inpt.v_guarantor_ext, inpt.v_guar_type, inpt.v_int_type, inpt.n_facility_no, "
                + "inpt.n_ca_cdab / " + unit + " n_ca_cdab, inpt.n_bb_fee / " + unit + " n_bb_fee, "
                + "inpt.n_trsry_fee / " + unit + " n_trsry_fee, inpt.n_others / " + unit + " n_others, "
                + "inpt.n_td_fee / " + unit + " n_td_fee, inpt.v_rec_ref_no, inpt.v_ucicf, inpt.v_restructured_status, "
                + "inpt.v_templated_rating, inpt.v_benchmark, inpt.v_psl, inpt.v_region, inpt.v_derivative_type, "
                + "inpt.v_comp_secured "
                + "FROM  INPT_RAROC inpt "
                + "WHERE inpt.v_rec_ref_no = ? "
                + "ORDER BY inpt.n_facility_no";
        RarocInputModel inpt = new RarocInputModel();
        List<RarocFacilityModel> facs = new ArrayList<>();
        String cdab = "", bbfee = "", trsry = "", others = "", tdfee = "", refrec = "", nonFinColl = "";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{recref});
        for (Map row : rows) {
            RarocFacilityModel obj = new RarocFacilityModel();
            obj.setFacType(fmt.ToString(row.get("v_fac_type")));
            obj.setFacDesc(fmt.ToString(row.get("v_fac_desc")));
            obj.setAstType(fmt.ToString(row.get("v_asset_type")));
            obj.setCur(fmt.ToString(row.get("v_curr")));
            obj.setAmount(fmt.ToString(row.get("n_amount")));
            obj.setTenure(fmt.ToString(row.get("n_tenure")));
            obj.setExgRate(fmt.ToString(row.get("n_excge_rate")));
            obj.setAvgUtil(fmt.ToString(row.get("n_avg_utili")));
            obj.setIntRate(fmt.ToString(row.get("n_int_rate_comm")));
            obj.setCostFunds(fmt.ToString(row.get("n_cost_funds")));
            obj.setuFee(fmt.ToString(row.get("n_upfront_fee")));
            obj.setaFee(fmt.ToString(row.get("n_annual_fee")));
            obj.setcMargin(fmt.ToString(row.get("n_cash_margin")));
            obj.setcMarginCurr(fmt.ToString(row.get("v_cash_mismatch")));
            obj.setLtRating(fmt.ToString(row.get("v_long_ext")));
            obj.setStRating(fmt.ToString(row.get("v_short_ext")));
            obj.seteGuar(fmt.ToString(row.get("n_exp_guar")));
            obj.setGuarType(fmt.ToString(row.get("v_guar_type")));
            obj.setGuarIntRat(fmt.ToString(row.get("v_guarantor_int")));
            obj.setGuarExtRat(fmt.ToString(row.get("v_guarantor_ext")));
            obj.setInttype(fmt.ToString(row.get("v_int_type")));
            obj.setUcicf(fmt.ToString(row.get("v_ucicf")));
            obj.setRestStatus(fmt.ToString(row.get("v_restructured_status")));
            obj.settRating(fmt.ToString(row.get("v_templated_rating")));
            obj.setBenchmark(fmt.ToString(row.get("v_benchmark")));
            obj.setPsl(fmt.ToString(row.get("v_psl")));
            obj.setRegion(fmt.ToString(row.get("v_region")));
            obj.setDerType(fmt.ToString(row.get("v_derivative_type")));
            obj.setcSecured(fmt.ToString(row.get("v_comp_secured")));
            obj.setFacNo(fmt.ToString(row.get("n_facility_no")));

            cdab = fmt.ToString(row.get("n_ca_cdab"));
            bbfee = fmt.ToString(row.get("n_bb_fee"));
            trsry = fmt.ToString(row.get("n_trsry_fee"));
            others = fmt.ToString(row.get("n_others"));
            tdfee = fmt.ToString(row.get("n_td_fee"));
            refrec = fmt.ToString(row.get("v_rec_ref_no"));
            facs.add(obj);
        }
        inpt.setBbcdab(cdab);
        inpt.setBbfee(bbfee);
        inpt.setTrsry(trsry);
        inpt.setOther(others);
        inpt.setTdfee(tdfee);
        inpt.setRefrec(refrec);
        //inpt.setFacilities(facs);
        return inpt;
    }

    @Override
    public RarocInputModel getExistingInput(String recref, String facnos, Integer unit) {
        String query = "SELECT inpt.v_fac_type, inpt.v_fac_desc, inpt.v_asset_type, inpt.v_curr, "
                + "inpt.n_amount / " + unit + " n_amount, inpt.n_tenure, inpt.n_excge_rate, "
                + "nvl(inpt.n_avg_utili,0)*100 n_avg_utili, nvl(inpt.n_int_rate_comm,0)*100 n_int_rate_comm, "
                + "nvl(inpt.n_cost_funds,0)*100 n_cost_funds, inpt.n_upfront_fee / " + unit + " n_upfront_fee, "
                + "nvl(inpt.n_annual_fee,0)*100 n_annual_fee, nvl(inpt.n_cash_margin,0)*100 n_cash_margin, "
                + "inpt.v_cash_mismatch, inpt.v_long_ext, inpt.v_short_ext, nvl(inpt.n_exp_guar,0)*100 n_exp_guar, "
                + "inpt.v_guarantor_int, inpt.v_guarantor_ext, inpt.v_guar_type, inpt.v_int_type, inpt.n_facility_no, "
                + "inpt.n_ca_cdab / " + unit + " n_ca_cdab, inpt.n_bb_fee / " + unit + " n_bb_fee, "
                + "inpt.n_trsry_fee / " + unit + " n_trsry_fee, inpt.n_others / " + unit + " n_others, "
                + "inpt.n_td_fee / " + unit + " n_td_fee, inpt.v_rec_ref_no, inpt.v_ucicf, inpt.v_restructured_status, "
                + "inpt.v_templated_rating, inpt.v_benchmark, inpt.v_psl, inpt.v_region, inpt.v_derivative_type, "
                + "inpt.v_comp_secured "
                + "FROM  INPT_RAROC inpt "
                + "WHERE inpt.v_rec_ref_no = ? "
                + "AND inpt.n_facility_no IN (" + facnos + ") "
                + "ORDER BY inpt.n_facility_no";
        RarocInputModel inpt = new RarocInputModel();
        List<RarocFacilityModel> facs = new ArrayList<>();
        String cdab = "", bbfee = "", trsry = "", others = "", tdfee = "", refrec = "", nonFinColl = "";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{recref});
        for (Map row : rows) {
            RarocFacilityModel obj = new RarocFacilityModel();
            obj.setFacType(fmt.ToString(row.get("v_fac_type")));
            obj.setFacDesc(fmt.ToString(row.get("v_fac_desc")));
            obj.setAstType(fmt.ToString(row.get("v_asset_type")));
            obj.setCur(fmt.ToString(row.get("v_curr")));
            obj.setAmount(fmt.ToString(row.get("n_amount")));
            obj.setTenure(fmt.ToString(row.get("n_tenure")));
            obj.setExgRate(fmt.ToString(row.get("n_excge_rate")));
            obj.setAvgUtil(fmt.ToString(row.get("n_avg_utili")));
            obj.setIntRate(fmt.ToString(row.get("n_int_rate_comm")));
            obj.setCostFunds(fmt.ToString(row.get("n_cost_funds")));
            obj.setuFee(fmt.ToString(row.get("n_upfront_fee")));
            obj.setaFee(fmt.ToString(row.get("n_annual_fee")));
            obj.setcMargin(fmt.ToString(row.get("n_cash_margin")));
            obj.setcMarginCurr(fmt.ToString(row.get("v_cash_mismatch")));
            obj.setLtRating(fmt.ToString(row.get("v_long_ext")));
            obj.setStRating(fmt.ToString(row.get("v_short_ext")));
            obj.seteGuar(fmt.ToString(row.get("n_exp_guar")));
            obj.setGuarType(fmt.ToString(row.get("v_guar_type")));
            obj.setGuarIntRat(fmt.ToString(row.get("v_guarantor_int")));
            obj.setGuarExtRat(fmt.ToString(row.get("v_guarantor_ext")));
            obj.setInttype(fmt.ToString(row.get("v_int_type")));
            obj.setUcicf(fmt.ToString(row.get("v_ucicf")));
            obj.setRestStatus(fmt.ToString(row.get("v_restructured_status")));
            obj.settRating(fmt.ToString(row.get("v_templated_rating")));
            obj.setBenchmark(fmt.ToString(row.get("v_benchmark")));
            obj.setPsl(fmt.ToString(row.get("v_psl")));
            obj.setRegion(fmt.ToString(row.get("v_region")));
            obj.setDerType(fmt.ToString(row.get("v_derivative_type")));
            obj.setcSecured(fmt.ToString(row.get("v_comp_secured")));
            obj.setFacNo(fmt.ToString(row.get("n_facility_no")));

            cdab = fmt.ToString(row.get("n_ca_cdab"));
            bbfee = fmt.ToString(row.get("n_bb_fee"));
            trsry = fmt.ToString(row.get("n_trsry_fee"));
            others = fmt.ToString(row.get("n_others"));
            tdfee = fmt.ToString(row.get("n_td_fee"));
            refrec = fmt.ToString(row.get("v_rec_ref_no"));
            facs.add(obj);
        }
        inpt.setBbcdab(cdab);
        inpt.setBbfee(bbfee);
        inpt.setTrsry(trsry);
        inpt.setOther(others);
        inpt.setTdfee(tdfee);
        inpt.setRefrec(refrec);
//        inpt.setFacilities(facs);
        return inpt;
    }

    @Override
    public void updateRarocDetails_fb(RarocInputModel model) {
        model.setUnit(100000);
        String query = "UPDATE INPT_RAROC SET  "
                + "V_FAC_DESC =	?,V_FAC_TYPE = ?,N_AMOUNT = NVL(?,0),V_TEMPLATED_RATING = ?,N_AVG_BAL_CUR = ?*100000, "
                + "N_AVG_BAL_NEXT = ?*100000,N_AVG_UTL_CUR = ?/100,N_AVG_UTL_NEXT = ?/100,V_UCICF = ?,"
                + "V_RESTRUCTURED_STATUS = ?,D_ORIGIN_DATE = ?,N_ORIGINAL_MATURITY = NVL(?,0),N_TENURE = NVL(?,0), "
                + "V_REPRICE_FREQ = ?,N_ANNUAL_FEE = NVL(?,0)/100,V_ASSET_TYPE = ?,V_PSL =	?,V_CURR = ?,N_EXCGE_RATE =	NVL(?,0), "
                + "V_INT_TYPE =	?,V_BENCHMARK =	?,N_INT_RATE_COMM = NVL(?,0)/100,N_COST_FUNDS =	NVL(?,0)/100,N_UPFRONT_FEE =	NVL(?,0), "
                + "N_SYNDICATION_FEE =	NVL(?,0),V_COMP_SECURED = ?,V_REGION = ?,N_CASH_MARGIN = NVL(?,0)/100,V_CASH_MISMATCH =	?, "
                + "V_GUAR_TYPE = ?,N_EXP_GUAR =	NVL(?,0)/100,V_GUARANTOR_EXT = ?,V_GUARANTOR_INT = ?,V_LONG_EXT = ?,V_SHORT_EXT = ?, "
                + "V_EXT_RATED = ?,V_GIFT_CITY = ?,N_BA_CDAB =	NVL(?,0),N_TERM_DEPO =	NVL(?,0),N_CA_FEE = NVL(?,0),N_CMS_FEE = NVL(?,0), "
                + "N_FOREX_COMMIS = NVL(?,0),N_OTHER =	NVL(?,0),N_CCY_MISMATCH = (SELECT n_haircut FROM MST_FIN_HAIRCUT WHERE v_fin_sec = ?) "
                + "Where N_FACILITY_NO = ? AND V_REC_REF_NO = ?";

        String[] guar = model.getGuarType().split("~");
        String gType = guar[1];

        getJdbcTemplate().update(query, new Object[]{
            model.getFacDesc(),
            model.getFacType(),
            Double.parseDouble(model.getAmount()) * model.getUnit(),
            model.gettRating(),
            model.getCurfy(),
            model.getNextfy(),
            model.getAvgfy(),
            model.getAvgnextfy(),
            model.getUcicf(),
            model.getRestStatus(),
            model.getOridate(),
            model.getMaturity(),
            model.getTenure(),
            model.getRefreq(),
            model.getaFee(),
            model.getAstType(),
            model.getPsl(),
            model.getCur(),
            model.getExgRate(),
            model.getInttype(),
            model.getBenchmark(),
            model.getIntRate(),
            model.getCostFunds(),
            Double.parseDouble(model.getuFee()) * model.getUnit(),
            Double.parseDouble(model.getSynFee()) * model.getUnit(),
            model.getcSecured(),
            model.getRegion(),
            model.getcMargin(),
            model.getcMarginCurr(),
            gType,
            model.geteGuar(),
            model.getGuarExtRat(),
            model.getGuarIntRat(),
            model.getLtRating(),
            model.getStRating(),
            model.getExtRated(),
            model.getGiftCity(),
            Double.parseDouble(model.getBbcdab()) * model.getUnit(),
            Double.parseDouble(model.getTdfee()) * model.getUnit(),
            Double.parseDouble(model.getCafee()) * model.getUnit(),
            Double.parseDouble(model.getCms()) * model.getUnit(),
            Double.parseDouble(model.getForex()) * model.getUnit(),
            Double.parseDouble(model.getOther()) * model.getUnit(),
            model.getcMarginCurr(),
            model.getFacNo(),
            model.getRefrec()
        });
    }

    @Override
    public void updateRarocDetails_nfb(RarocInputModel model) {
        model.setUnit(100000);
        String query = " UPDATE INPT_RAROC SET "
                + " V_FAC_DESC = ?,V_FAC_TYPE =	?,N_AMOUNT = NVL(?,0),V_TEMPLATED_RATING = ?,N_AVG_UTL_CUR = ?/100, "
                + " N_AVG_UTL_NEXT = ?/100,V_UCICF = ?,V_RESTRUCTURED_STATUS = ?,N_ORIGINAL_MATURITY = NVL(?,0), "
                + " N_AVG_MATURITY = NVL(?,0),V_ASSET_TYPE = ?,V_CURR = ?,N_EXCGE_RATE = NVL(?,0),N_COMMISSION = NVL(?,0)/100, "
                + " N_UPFRONT_FEE = NVL(?,0),N_SYNDICATION_FEE = NVL(?,0),V_COMP_SECURED = ?,V_REGION =	?,N_CASH_MARGIN = NVL(?,0)/100, "
                + " V_CASH_MISMATCH = ?,V_GUAR_TYPE = ?,N_EXP_GUAR = NVL(?,0)/100,V_GUARANTOR_EXT = ?,V_GUARANTOR_INT =	?,V_LONG_EXT =	?, "
                + " V_SHORT_EXT = ?,V_EXT_RATED = ?,V_GIFT_CITY = ?,N_BA_CDAB = NVL(?,0),N_TERM_DEPO = NVL(?,0),N_CA_FEE = NVL(?,0), "
                + " N_CMS_FEE = NVL(?,0),N_FOREX_COMMIS = NVL(?,0),N_OTHER = NVL(?,0),N_CCY_MISMATCH = (SELECT n_haircut FROM MST_FIN_HAIRCUT WHERE v_fin_sec = ?) "
                + " WHERE N_FACILITY_NO = ? AND V_REC_REF_NO = ?";
        String[] guar = model.getGuarType().split("~");
        String gType = guar[1];

        getJdbcTemplate().update(query, new Object[]{
            model.getFacDesc(),
            model.getFacType(),
            Double.parseDouble(model.getAmount()) * model.getUnit(),
            model.gettRating(),
            model.getAvgfy(),
            model.getAvgnextfy(),
            model.getUcicf(),
            model.getRestStatus(),
            model.getMaturity(),
            model.getAvgMat(),
            model.getAstType(),
            model.getCur(),
            model.getExgRate(),
            model.getIntRate(),
            Double.parseDouble(model.getuFee()) * model.getUnit(),
            Double.parseDouble(model.getSynFee()) * model.getUnit(),
            model.getcSecured(),
            model.getRegion(),
            model.getcMargin(),
            model.getcMarginCurr(),
            gType,
            model.geteGuar(),
            model.getGuarExtRat(),
            model.getGuarIntRat(),
            model.getLtRating(),
            model.getStRating(),
            model.getExtRated(),
            model.getGiftCity(),
            Double.parseDouble(model.getBbcdab()) * model.getUnit(),
            Double.parseDouble(model.getTdfee()) * model.getUnit(),
            Double.parseDouble(model.getCafee()) * model.getUnit(),
            Double.parseDouble(model.getCms()) * model.getUnit(),
            Double.parseDouble(model.getForex()) * model.getUnit(),
            Double.parseDouble(model.getOther()) * model.getUnit(),
            model.getcMarginCurr(),
            model.getFacNo(),
            model.getRefrec()
        });
    }

    @Override
    public void editRarocDetails(RarocInputModel model) {
        String query = "MERGE INTO INPT_RAROC i USING dual ON (i.v_rec_ref_no = ? AND i.n_facility_no = ?) "
                + "WHEN MATCHED THEN "
                + "UPDATE SET i.v_fac_type = ?, i.v_fac_desc = ?, i.v_asset_type = ?, i.v_curr = ?, i.n_amount = nvl(?,0), "
                + "i.n_tenure = ?, i.n_excge_rate = ?, i.n_avg_utili = nvl(?,0)/100, i.n_int_rate_comm = nvl(?,0)/100, "
                + "i.n_cost_funds = nvl(?,0)/100, i.n_upfront_fee = nvl(?,0), i.n_annual_fee = nvl(?,0)/100, "
                + "i.n_cash_margin = nvl(?,0)/100, i.v_cash_mismatch = ?, i.v_long_ext = nvl(?,'UNRATED'), "
                + "i.v_short_ext = nvl(?,'UNRATED'), i.n_exp_guar = nvl(?,0)/100, i.v_guarantor_int = ?, "
                + "i.v_guarantor_ext = ?, i.n_ca_cdab = nvl(?,0), i.n_bb_fee = nvl(?,0), i.n_trsry_fee = nvl(?,0), "
                + "i.n_others = nvl(?,0), i.n_td_fee = nvl(?,0), i.v_guar_type = ?, i.v_int_type = ?, "
                + "i.n_ccy_mismatch = (SELECT n_haircut FROM MST_FIN_HAIRCUT WHERE v_fin_sec = ?), i.v_ucicf = ?, "
                + "i.v_restructured_status = ?, i.v_templated_rating = ?, i.v_benchmark = ?, i.v_psl = ?, "
                + "i.v_region = ?, i.v_derivative_type = ?, i.v_comp_secured = ?, N_ORIGINAL_MATURITY = ? "
                + "WHEN NOT MATCHED THEN "
                + "INSERT (v_fac_type, v_fac_desc, v_asset_type, v_curr, n_amount, n_tenure, n_excge_rate, "
                + "n_avg_utili, n_int_rate_comm, n_cost_funds, n_upfront_fee, n_annual_fee, n_cash_margin, "
                + "v_cash_mismatch, v_long_ext, v_short_ext, n_exp_guar, v_guarantor_int, v_guarantor_ext, "
                + "n_ca_cdab, n_bb_fee, n_trsry_fee, n_others, n_td_fee, n_facility_no, v_rec_ref_no, v_guar_type, "
                + "v_int_type, n_ccy_mismatch, v_ucicf, v_restructured_status, v_templated_rating, v_benchmark, v_psl, "
                + "v_region, v_derivative_type, v_comp_secured,N_ORIGINAL_MATURITY) "
                + "VALUES(?, ?, ?, ?, nvl(?,0), nvl(?,0), nvl(?,0), nvl(?,0)/100, nvl(?,0)/100, "
                + "nvl(?,0)/100, nvl(?,0), nvl(?,0)/100, nvl(?,0)/100, ?, NVL(?,'UNRATED'), NVL(?,'UNRATED'), "
                + "?, ?, nvl(?,0), nvl(?,0), nvl(?,0), nvl(?,0), nvl(?,0), nvl(?,0), ?, ?, ?, ?, "
                + "(SELECT n_haircut FROM MST_FIN_HAIRCUT WHERE v_fin_sec = ?), ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String[] guar = model.getGuarType().split("~");
        String gType = guar[1];

        getJdbcTemplate().update(query, new Object[]{model.getRefrec(), model.getFacNo(),
            model.getFacType(), model.getFacDesc(), model.getAstType(), model.getCur(),
            Double.parseDouble(model.getAmount()) * model.getUnit(), model.getTenure(),
            model.getExgRate(), model.getAvgUtil(), model.getIntRate(), model.getCostFunds(),
            Double.parseDouble(model.getuFee()) * model.getUnit(), model.getaFee(), model.getcMargin(),
            model.getcMarginCurr(), model.getLtRating(), model.getStRating(),
            model.geteGuar(), model.getGuarIntRat(), model.getGuarExtRat(),
            Double.parseDouble(model.getBbcdab()) * model.getUnit(),
            Double.parseDouble(model.getBbfee()) * model.getUnit(),
            Double.parseDouble(model.getTrsry()) * model.getUnit(),
            Double.parseDouble(model.getOther()) * model.getUnit(),
            Double.parseDouble(model.getTdfee()) * model.getUnit(), gType, model.getInttype(),
            model.getcMarginCurr(), model.getUcicf(), model.getRestStatus(), model.gettRating(), model.getBenchmark(),
            model.getPsl(), model.getRegion(), model.getDerType(), model.getcSecured(), model.getMaturity(),
            model.getFacType(), model.getFacDesc(), model.getAstType(), model.getCur(),
            Double.parseDouble(model.getAmount()) * model.getUnit(), model.getTenure(), model.getExgRate(),
            model.getAvgUtil(), model.getIntRate(), model.getCostFunds(),
            Double.parseDouble(model.getuFee()) * model.getUnit(),
            model.getaFee(), model.getcMargin(), model.getcMarginCurr(), model.getLtRating(), model.getStRating(),
            model.geteGuar(), model.getGuarIntRat(), model.getGuarExtRat(),
            Double.parseDouble(model.getBbcdab()) * model.getUnit(),
            Double.parseDouble(model.getBbfee()) * model.getUnit(),
            Double.parseDouble(model.getTrsry()) * model.getUnit(),
            Double.parseDouble(model.getOther()) * model.getUnit(),
            Double.parseDouble(model.getTdfee()) * model.getUnit(),
            model.getFacNo(), model.getRefrec(), gType, model.getInttype(), model.getcMarginCurr(),
            model.getUcicf(), model.getRestStatus(), model.gettRating(), model.getBenchmark(), model.getPsl(),
            model.getRegion(), model.getDerType(), model.getcSecured(), model.getMaturity()
        });

    }

    @Override
    public void rarocStatus(String recref, String user, String status, String pass) {
        System.out.println("1");

        String emails = null;
        String sql = " Select LISTAGG(SUPER_EMPID, ',') WITHIN GROUP (ORDER BY lvl) SUPER_EMPID "
                + "FROM ( "
                + "SELECT FIRST_NAME, EMPLOYEE_NUMBER, SUPER_EMPID||'@axisbank.com' SUPER_EMPID, LEVEL lvl   "
                + "FROM T_EMPLOYEE_DETAILS "
                + "START WITH EMPLOYEE_NUMBER = ? and SUPER_EMPID <> 0 "
                + "CONNECT BY PRIOR SUPER_EMPID = EMPLOYEE_NUMBER   and level <= 2 "
                + "ORDER SIBLINGS BY FIRST_NAME)";
        System.out.println("2");
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, new Object[]{user});
        System.out.println("3");
        for (Map row : rows) {
            emails = fmt.ToString(row.get("SUPER_EMPID"));
            System.out.println("4 " + emails);
        }
        if (!rows.isEmpty()) {
            System.out.println("5 " + emails);
            //final String username = user + "@axisbank.com";
            final String username = "Raroc.approval@axisbank.com";
            System.out.println("6 " + username);
            System.out.println("7 " + pass);

            final String password = pass;
            String to = emails;//change accordingly  

            //Get the session object  
            Properties props = new Properties();
            props.put("mail.smtp.auth", auth);
            props.put("mail.smtp.starttls.enable", starttls);
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            System.out.println("username = " + username);
            System.out.println("password = " + password);
            System.out.println("auth = " + auth);
            System.out.println("starttls = " + starttls);
            System.out.println("host = " + host);
            System.out.println("port = " + port);

            Session session = Session.getInstance(props);

            try {

                String que = "SELECT V_SUBJECT FROM CNFG_MAIL_SETTING WHERE N_ID = 1";
                String subject = getJdbcTemplate().queryForObject(que, String.class);

                String query = "SELECT V_MAIL_BODY FROM CNFG_MAIL_SETTING WHERE N_ID = 1";
                String body = getJdbcTemplate().queryForObject(query, String.class);

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));
                message.setSubject(subject);
                message.setText("Dear Users, "
                        + body + " " + recref);

                Transport.send(message);

                System.out.println("Done");

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        String query = " UPDATE INPT_RAROC_MASTER SET f_status = ?, V_CHECKER = (Select LISTAGG(SUPER_EMPID, ',') WITHIN GROUP (ORDER BY lvl) SUPER_EMPID    "
                + " FROM (  "
                + " SELECT SUPER_EMPID, LEVEL lvl    "
                + " FROM T_EMPLOYEE_DETAILS  "
                + " START WITH EMPLOYEE_NUMBER = ? and SUPER_EMPID <> 0  "
                + " CONNECT BY PRIOR SUPER_EMPID = EMPLOYEE_NUMBER   and level <= 2 "
                + " ORDER SIBLINGS BY FIRST_NAME)) "
                + " WHERE v_rec_ref_no = ? "
                + " AND v_created = ?";
        getJdbcTemplate().update(query, new Object[]{status, user, recref, user});
    }

    @Override
    public GridPage<RarocMasterModel> listRaroc(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String userid) throws CustomException {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_rec_ref_no", "v_cust_id",
                "v_cust_name", "d_created", "v_modified", "d_modified", "f_status", "v_approved",
                "d_approved"));
        List<String> orders = Collections.unmodifiableList(Arrays.asList("asc", "desc"));

        if (sidx == null || sidx.isEmpty()) {
            sidx = "d_created";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "desc";
        }

        //check if sidx is in columns 
        if (!columns.contains(sidx)) {
            throw new CustomException();
        }

        if (!orders.contains(sord)) {
            throw new CustomException();
        }
        String query = "SELECT v_department FROM CNFG_USERS WHERE v_user_id = ?";
        String dept = getJdbcTemplate().queryForObject(query, new Object[]{userid}, String.class);
        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        query = "SELECT count(*) FROM INPT_RAROC_MASTER "
                //+ "WHERE v_created IN (SELECT v_user_id FROM CNFG_USERS WHERE v_department = ?) "
                + "WHERE ','||V_CHECKER||',' LIKE  '%,'||?||',%' "
                + "AND f_status IN ('S','A','H') " + qObj.getCondition();
        System.out.println("User:= " + userid);
        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{userid, qObj.getRegex()}, Integer.class);
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "(SELECT a.*, rownum rnum FROM "
                + "(SELECT v_rec_ref_no, v_rating_tool_id, v_cust_name, d_created, v_modified, d_modified, "
                + "case when f_status = 'H' then 'History' when f_status = 'A' then 'Approved' "
                + "when f_status = 'I' then 'Incomplete' when f_status = 'R' then 'Rejected' "
                + "when f_status = 'S' then 'Submitted For Approval' end f_status, "
                + "v_approved, to_char(d_approved,'dd-Mon-yy hh:mm:ss') d_approved "
                + "FROM INPT_RAROC_MASTER "
                //+ "WHERE v_created IN (SELECT v_user_id FROM CNFG_USERS WHERE v_department = ?) "
                + " WHERE ','||V_CHECKER||',' LIKE  '%,'||?||',%'  "
                + "AND f_status IN ('S','A','H') "
                + qObj.getCondition() + " "
                + "ORDER BY " + sidx + " " + sord + ") a "
                + "WHERE rownum <= ?) WHERE rnum >= ?";
        List<RarocMasterModel> lists = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{userid, qObj.getRegex(),
            endIdx, startIdx});
        rows.stream().map((row) -> {
            RarocMasterModel obj = new RarocMasterModel();
            obj.setId(fmt.ToString(row.get("rnum")));
            obj.setRarocref(fmt.ToString(row.get("v_rec_ref_no")));
            obj.setCid(fmt.ToString(row.get("v_rating_tool_id")));
            obj.setCname(fmt.ToString(row.get("v_cust_name")));
            obj.setCdate(fmt.ToString(row.get("d_created")));
            obj.setCuser(fmt.ToString(row.get("v_created")));
            obj.setMuser(fmt.ToString(row.get("v_modified")));
            obj.setMdate(fmt.ToString(row.get("d_modified")));
            obj.setStatus(fmt.ToString(row.get("f_status")));
            obj.setAuser(fmt.ToString(row.get("v_approved")));
            obj.setAdate(fmt.ToString(row.get("d_approved")));
            return obj;
        }).forEach((obj) -> {
            lists.add(obj);
        });
        return new GridPage<>(lists, page, max, rowCount);
    }

    @Override
    public void adminEditRarocMaster(RarocMasterModel model, String user) {
        String query = "UPDATE INPT_RAROC_MASTER SET v_cust_name = ?, n_facility = ?, "
                + "v_rating_tool_code = ?, v_int_rating = ?, v_rating_tool_id = ?, "
                + "v_industry = ?, v_business_unit = ?, v_modified = ?, d_modified = sysdate "
                + "WHERE v_rec_ref_no = ?";
        getJdbcTemplate().update(query, new Object[]{model.getCname(), model.getFacility(),
            model.getRtool(), model.getIntRat(), model.getRid(), model.getInd(),
            model.getBussunit(), user, model.getRarocref()});
    }

    @Override
    public void adminRecordStatus(RarocAuthorize model, String status, String user, String ref) {
        String query = "UPDATE INPT_RAROC_MASTER SET f_status = ?, v_approved = ?, "
                + "d_approved = sysdate, c_remarks = ? "
                + "WHERE v_rec_ref_no = ?";
        getJdbcTemplate().update(query, new Object[]{status, user, model.getRemarks(), ref});
    }

    @Override
    public String getApproverComments(String refno) {
        String sql = "SELECT c_remarks FROM INPT_RAROC_MASTER WHERE v_rec_ref_no = ?";
        String comment = (String) getJdbcTemplate().queryForObject(sql, new Object[]{refno}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int i) throws SQLException {
                return lobHandler.getClobAsString(rs, "c_remarks");
            }
        });
        return comment;
    }

    @Override
    public GridPage<RarocInputModel> listRarocInputs(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_rec_ref_no", "N_FACILITY_NO"));
        List<String> orders = Collections.unmodifiableList(Arrays.asList("asc", "desc"));;

        if (sidx == null || sidx.isEmpty()) {
            sidx = "N_FACILITY_NO";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }

        //check if sidx is in columns 
        if (!columns.contains(sidx)) {
            throw new CustomException();
        }

        if (!orders.contains(sord)) {
            throw new CustomException();
        }

        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM INPT_RAROC Where V_REC_REF_NO = ? AND V_FACILITY_TYPE = 'FB'  " + qObj.getCondition();

        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{refId, qObj.getRegex()}, Integer.class
        );
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "( SELECT a.*, rownum rnum FROM "
                + "( SELECT V_REC_REF_NO,N_FACILITY_NO,V_FAC_DESC,V_FAC_TYPE,V_ASSET_TYPE,V_CURR,N_AMOUNT/100000 N_AMOUNT,N_TENURE,N_EXCGE_RATE,N_AVG_UTILI*100 N_AVG_UTILI,N_INT_RATE_COMM*100 N_INT_RATE_COMM, "
                + " NVL(N_COST_FUNDS*100,0) N_COST_FUNDS,N_UPFRONT_FEE/100000 N_UPFRONT_FEE,N_ANNUAL_FEE*100 N_ANNUAL_FEE,N_CASH_MARGIN*100 N_CASH_MARGIN,V_CASH_MISMATCH,V_LONG_EXT,V_SHORT_EXT,N_EXP_GUAR*100 N_EXP_GUAR, "
                + " V_GUARANTOR_INT,V_GUARANTOR_EXT,N_CA_CDAB,N_BB_FEE,N_TRSRY_FEE,N_OTHERS,V_GUAR_TYPE,V_INT_TYPE,N_CCY_MISMATCH,V_UCICF,V_RESTRUCTURED_STATUS, "
                + " V_TEMPLATED_RATING,V_BENCHMARK,N_TD_FEE,V_PSL,V_REGION,V_DERIVATIVE_TYPE,V_COMP_SECURED, N_ORIGINAL_MATURITY ,N_AVG_BAL_CUR/100000 N_AVG_BAL_CUR,N_AVG_BAL_NEXT/100000 N_AVG_BAL_NEXT,N_AVG_UTL_CUR*100 N_AVG_UTL_CUR,N_AVG_UTL_NEXT*100 N_AVG_UTL_NEXT,N_SYNDICATION_FEE/100000 N_SYNDICATION_FEE, V_EXT_RATED, V_GIFT_CITY, to_char(D_ORIGIN_DATE,'dd-Mon-yyyy') D_ORIGIN_DATE,V_REPRICE_FREQ "
                + " FROM INPT_RAROC WHERE V_REC_REF_NO = ?   AND V_FACILITY_TYPE = 'FB' "
                + qObj.getCondition() + " "
                + " ORDER BY " + sidx + " " + sord + ") a "
                + " WHERE rownum <= ?) WHERE rnum >= ?";
        List<RarocInputModel> lists = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{refId, qObj.getRegex(),
            endIdx, startIdx});

        for (Map row : rows) {
            RarocInputModel obj = new RarocInputModel();
            obj.setId(fmt.ToString(row.get("rnum")));
            obj.setRefrec(fmt.ToString(row.get("V_REC_REF_NO")));
            obj.setFacNo(fmt.ToString(row.get("N_FACILITY_NO")));
            obj.setFacDesc(fmt.ToString(row.get("V_FAC_DESC")));
            obj.setFacType(fmt.ToString(row.get("V_FAC_TYPE")));
            obj.setAstType(fmt.ToString(row.get("V_ASSET_TYPE")));
            obj.setCur(fmt.ToString(row.get("V_CURR")));
            obj.setAmount(fmt.ToString(row.get("N_AMOUNT")));
            obj.setTenure(fmt.ToString(row.get("N_TENURE")));
            obj.setExgRate(fmt.ToString(row.get("N_EXCGE_RATE")));
            obj.setAvgUtil(fmt.ToString(row.get("N_AVG_UTILI")));
            obj.setInttype(fmt.ToString(row.get("V_INT_TYPE")));
            obj.setIntRate(fmt.ToString(row.get("N_INT_RATE_COMM")));
            obj.setCostFunds(fmt.ToString(row.get("N_COST_FUNDS")));
            obj.setuFee(fmt.ToString(row.get("N_UPFRONT_FEE")));
            obj.setaFee(fmt.ToString(row.get("N_ANNUAL_FEE")));
            obj.setcMargin(fmt.ToString(row.get("N_CASH_MARGIN")));
            obj.setcMarginCurr(fmt.ToString(row.get("V_CASH_MISMATCH")));
            obj.setLtRating(fmt.ToString(row.get("V_LONG_EXT")));
            obj.setStRating(fmt.ToString(row.get("V_SHORT_EXT")));
            obj.seteGuar(fmt.ToString(row.get("N_EXP_GUAR")));
            obj.setGuarIntRat(fmt.ToString(row.get("V_GUARANTOR_INT")));
            obj.setGuarExtRat(fmt.ToString(row.get("V_GUARANTOR_EXT")));
            obj.setBbcdab(fmt.ToString(row.get("N_CA_CDAB")));
            obj.setBbfee(fmt.ToString(row.get("N_BB_FEE")));
            obj.setTrsry(fmt.ToString(row.get("N_TRSRY_FEE")));
            obj.setOther(fmt.ToString(row.get("N_OTHERS")));
            obj.setGuarType(fmt.ToString(row.get("V_GUAR_TYPE")));
            obj.setCurrMis(fmt.ToString(row.get("N_CCY_MISMATCH")));
            obj.setUcicf(fmt.ToString(row.get("V_UCICF")));
            obj.setRestStatus(fmt.ToString(row.get("V_RESTRUCTURED_STATUS")));
            obj.settRating(fmt.ToString(row.get("V_TEMPLATED_RATING")));
            obj.setBenchmark(fmt.ToString(row.get("V_BENCHMARK")));
            obj.setTdfee(fmt.ToString(row.get("N_TD_FEE")));
            obj.setPsl(fmt.ToString(row.get("V_PSL")));
            obj.setRegion(fmt.ToString(row.get("V_REGION")));
            obj.setDerType(fmt.ToString(row.get("V_DERIVATIVE_TYPE")));
            obj.setcSecured(fmt.ToString(row.get("V_COMP_SECURED")));
            obj.setMaturity(fmt.ToString(row.get("N_ORIGINAL_MATURITY")));
            obj.setCurfy(fmt.ToString(row.get("N_AVG_BAL_CUR")));
            obj.setNextfy(fmt.ToString(row.get("N_AVG_BAL_NEXT")));
            obj.setAvgfy(fmt.ToString(row.get("N_AVG_UTL_CUR")));
            obj.setAvgnextfy(fmt.ToString(row.get("N_AVG_UTL_NEXT")));
            obj.setSynFee(fmt.ToString(row.get("N_SYNDICATION_FEE")));
            obj.setExtRated(fmt.ToString(row.get("V_EXT_RATED")));
            obj.setGiftCity(fmt.ToString(row.get("V_GIFT_CITY")));
            obj.setOridate(fmt.ToString(row.get("D_ORIGIN_DATE")));
            obj.setRefreq(fmt.ToString(row.get("V_REPRICE_FREQ")));
            lists.add(obj);
        }
        return new GridPage<>(lists, page, max, rowCount);
    }

    @Override
    public GridPage<RarocInputModel> listRarocInputs_nfb(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_rec_ref_no", "N_FACILITY_NO"));
        List<String> orders = Collections.unmodifiableList(Arrays.asList("asc", "desc"));;

        if (sidx == null || sidx.isEmpty()) {
            sidx = "N_FACILITY_NO";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }

        //check if sidx is in columns 
        if (!columns.contains(sidx)) {
            throw new CustomException();
        }

        if (!orders.contains(sord)) {
            throw new CustomException();
        }

        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM INPT_RAROC Where V_REC_REF_NO = ? AND V_FACILITY_TYPE = 'NFB'  " + qObj.getCondition();

        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{refId, qObj.getRegex()}, Integer.class
        );
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "( SELECT a.*, rownum rnum FROM "
                + "( SELECT V_REC_REF_NO,N_FACILITY_NO,V_FAC_DESC,V_FAC_TYPE,V_ASSET_TYPE,V_CURR,N_AMOUNT/100000 N_AMOUNT,N_TENURE,N_EXCGE_RATE,N_AVG_UTL_CUR*100 N_AVG_UTL_CUR,N_AVG_UTL_NEXT*100 N_AVG_UTL_NEXT, N_COMMISSION * 100 N_INT_RATE_COMM, "
                + " N_COST_FUNDS*100 N_COST_FUNDS,N_UPFRONT_FEE/100000 N_UPFRONT_FEE,N_ANNUAL_FEE*100 N_ANNUAL_FEE,N_CASH_MARGIN*100 N_CASH_MARGIN,V_CASH_MISMATCH,V_LONG_EXT,V_SHORT_EXT,N_EXP_GUAR*100 N_EXP_GUAR, "
                + " V_GUARANTOR_INT,V_GUARANTOR_EXT,N_CA_CDAB,N_BB_FEE,N_TRSRY_FEE,N_OTHERS,V_GUAR_TYPE,V_INT_TYPE,N_CCY_MISMATCH,V_UCICF,V_RESTRUCTURED_STATUS, "
                + " V_TEMPLATED_RATING,V_BENCHMARK,N_TD_FEE,V_PSL,V_REGION,V_DERIVATIVE_TYPE,V_COMP_SECURED,N_AVG_MATURITY, N_ORIGINAL_MATURITY, N_SYNDICATION_FEE/100000 N_SYNDICATION_FEE, V_EXT_RATED, V_GIFT_CITY FROM INPT_RAROC WHERE V_REC_REF_NO = ? AND V_FACILITY_TYPE = 'NFB' "
                + qObj.getCondition() + " "
                + " ORDER BY " + sidx + " " + sord + ") a "
                + " WHERE rownum <= ?) WHERE rnum >= ?";
        List<RarocInputModel> lists = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{refId, qObj.getRegex(),
            endIdx, startIdx});
        for (Map row : rows) {
            RarocInputModel obj = new RarocInputModel();
            obj.setId(fmt.ToString(row.get("rnum")));
            obj.setRefrec(fmt.ToString(row.get("V_REC_REF_NO")));
            obj.setFacNo(fmt.ToString(row.get("N_FACILITY_NO")));
            obj.setFacDesc(fmt.ToString(row.get("V_FAC_DESC")));
            obj.setFacType(fmt.ToString(row.get("V_FAC_TYPE")));
            obj.setAstType(fmt.ToString(row.get("V_ASSET_TYPE")));
            obj.setCur(fmt.ToString(row.get("V_CURR")));
            obj.setAmount(fmt.ToString(row.get("N_AMOUNT")));
            obj.setTenure(fmt.ToString(row.get("N_TENURE")));
            obj.setExgRate(fmt.ToString(row.get("N_EXCGE_RATE")));
            obj.setAvgfy(fmt.ToString(row.get("N_AVG_UTL_CUR")));
            obj.setAvgnextfy(fmt.ToString(row.get("N_AVG_UTL_NEXT")));
            obj.setInttype(fmt.ToString(row.get("V_INT_TYPE")));
            obj.setIntRate(fmt.ToString(row.get("N_INT_RATE_COMM")));
            obj.setCostFunds(fmt.ToString(row.get("N_COST_FUNDS")));
            obj.setuFee(fmt.ToString(row.get("N_UPFRONT_FEE")));
            obj.setaFee(fmt.ToString(row.get("N_ANNUAL_FEE")));
            obj.setcMargin(fmt.ToString(row.get("N_CASH_MARGIN")));
            obj.setcMarginCurr(fmt.ToString(row.get("V_CASH_MISMATCH")));
            obj.setLtRating(fmt.ToString(row.get("V_LONG_EXT")));
            obj.setStRating(fmt.ToString(row.get("V_SHORT_EXT")));
            obj.seteGuar(fmt.ToString(row.get("N_EXP_GUAR")));
            obj.setGuarIntRat(fmt.ToString(row.get("V_GUARANTOR_INT")));
            obj.setGuarExtRat(fmt.ToString(row.get("V_GUARANTOR_EXT")));
            obj.setBbcdab(fmt.ToString(row.get("N_CA_CDAB")));
            obj.setBbfee(fmt.ToString(row.get("N_BB_FEE")));
            obj.setTrsry(fmt.ToString(row.get("N_TRSRY_FEE")));
            obj.setOther(fmt.ToString(row.get("N_OTHERS")));
            obj.setGuarType(fmt.ToString(row.get("V_GUAR_TYPE")));
            obj.setCurrMis(fmt.ToString(row.get("N_CCY_MISMATCH")));
            obj.setUcicf(fmt.ToString(row.get("V_UCICF")));
            obj.setRestStatus(fmt.ToString(row.get("V_RESTRUCTURED_STATUS")));
            obj.settRating(fmt.ToString(row.get("V_TEMPLATED_RATING")));
            obj.setBenchmark(fmt.ToString(row.get("V_BENCHMARK")));
            obj.setTdfee(fmt.ToString(row.get("N_TD_FEE")));
            obj.setPsl(fmt.ToString(row.get("V_PSL")));
            obj.setRegion(fmt.ToString(row.get("V_REGION")));
            obj.setDerType(fmt.ToString(row.get("V_DERIVATIVE_TYPE")));
            obj.setcSecured(fmt.ToString(row.get("V_COMP_SECURED")));
            obj.setMaturity(fmt.ToString(row.get("N_ORIGINAL_MATURITY")));
            obj.setAvgMat(fmt.ToString(row.get("N_AVG_MATURITY")));
            obj.setSynFee(fmt.ToString(row.get("N_SYNDICATION_FEE")));
            obj.setExtRated(fmt.ToString(row.get("V_EXT_RATED")));
            obj.setGiftCity(fmt.ToString(row.get("V_GIFT_CITY")));
            lists.add(obj);
        }
        return new GridPage<>(lists, page, max, rowCount);
    }

    @Override
    public GridPage<RarocInputModel> listRarocInputs_bonds(int page, int max, String sidx, String sord,
            String searchField, String searchOper, String searchString, String refId) throws CustomException {
        List<String> columns = Collections.unmodifiableList(Arrays.asList("v_rec_ref_no", "N_FACILITY_NO"));
        List<String> orders = Collections.unmodifiableList(Arrays.asList("asc", "desc"));;

        if (sidx == null || sidx.isEmpty()) {
            sidx = "N_FACILITY_NO";
        }
        if (sord == null || sord.isEmpty()) {
            sord = "asc";
        }

        //check if sidx is in columns 
        if (!columns.contains(sidx)) {
            throw new CustomException();
        }

        if (!orders.contains(sord)) {
            throw new CustomException();
        }

        QueryBuilderModel qObj = queryBuilder.SearchAnd(searchOper, searchField, searchString, columns);
        String query = "SELECT count(*) FROM INPT_RAROC Where V_REC_REF_NO = ? AND V_FACILITY_TYPE = 'BONDS'  " + qObj.getCondition();

        int rowCount = getJdbcTemplate().queryForObject(query, new Object[]{refId, qObj.getRegex()}, Integer.class
        );
        final int startIdx = ((page - 1) * max) + 1;
        final int endIdx = Math.min(startIdx + max, rowCount);
        query = "SELECT * FROM "
                + "( SELECT a.*, rownum rnum FROM "
                + "( SELECT V_REC_REF_NO,N_FACILITY_NO,V_FAC_DESC,V_FAC_TYPE,V_CURR,N_AMOUNT/100000 N_AMOUNT,N_EXCGE_RATE,N_UPFRONT_FEE/100000 N_UPFRONT_FEE,V_REGION, "
                + "  V_BOOK_TYPE, N_RESI_TENOR, N_YEILD*100 N_YEILD, V_COUPON_FREQ, V_EXT_RATING, V_LCR, N_EXP_TRADE/100000 N_EXP_TRADE, "
                + "  N_COUPON*100 N_COUPON, N_BOND_TENURE, N_BOND_HOLD_PRD FROM INPT_RAROC WHERE V_REC_REF_NO = ?   AND V_FACILITY_TYPE = 'BONDS' "
                + qObj.getCondition() + " "
                + " ORDER BY " + sidx + " " + sord + ") a "
                + " WHERE rownum <= ?) WHERE rnum >= ?";
        List<RarocInputModel> lists = new ArrayList<>();
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{refId, qObj.getRegex(),
            endIdx, startIdx});
        for (Map row : rows) {
            RarocInputModel obj = new RarocInputModel();
            obj.setId(fmt.ToString(row.get("rnum")));
            obj.setRefrec(fmt.ToString(row.get("V_REC_REF_NO")));
            obj.setFacNo(fmt.ToString(row.get("N_FACILITY_NO")));
            obj.setFacDesc(fmt.ToString(row.get("V_FAC_DESC")));
            obj.setFacType(fmt.ToString(row.get("V_FAC_TYPE")));
            obj.setCur(fmt.ToString(row.get("V_CURR")));
            obj.setAmount(fmt.ToString(row.get("N_AMOUNT")));
            obj.setrTenure(fmt.ToString(row.get("N_RESI_TENOR")));
            obj.setExgRate(fmt.ToString(row.get("N_EXCGE_RATE")));
            obj.setuFee(fmt.ToString(row.get("N_UPFRONT_FEE")));
            obj.setRegion(fmt.ToString(row.get("V_REGION")));
            obj.setBookType(fmt.ToString(row.get("V_BOOK_TYPE")));
            obj.setYeild(fmt.ToString(row.get("N_YEILD")));
            obj.setCoupon(fmt.ToString(row.get("V_COUPON_FREQ")));
            obj.setCetExt(fmt.ToString(row.get("V_EXT_RATING")));
            obj.setLcr(fmt.ToString(row.get("V_LCR")));
            obj.setExpIncome(fmt.ToString(row.get("N_EXP_TRADE")));
            obj.setCouponBond(fmt.ToString(row.get("N_COUPON")));
            obj.setBondTenure(fmt.ToString(row.get("N_BOND_TENURE")));
            obj.setTentative(fmt.ToString(row.get("N_BOND_HOLD_PRD")));
            lists.add(obj);
        }
        return new GridPage<>(lists, page, max, rowCount);
    }

    @Override
    public String getCostFundFlag() {
        String query = "SELECT N_VALUE FROM MST_RAROC WHERE N_CODE = 25";

        return getJdbcTemplate().queryForObject(query, String.class
        );
    }

    @Override
    public String getMapCol(String id) {
        String query = "SELECT CASE WHEN V_MAPPING_COL is NULL then 'No' else 'Yes' End flg FROM MST_GUARANTOR Where V_CODE = ?";

        return getJdbcTemplate().queryForObject(query, new Object[]{id}, String.class
        );
    }

    @Override
    public List<OptionsModel> listPSL() {
        String query = "SELECT v_code, n_premium_percentage FROM MST_PSL_CATEGORY";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        rows.stream().map((row) -> {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_code")));
            obj.setValue(fmt.ToString(row.get("v_code")));
            return obj;
        }).forEach((obj) -> {
            lObj.add(obj);
        });
        return lObj;
    }

    @Override
    public int delRarocFacility(String recref, String id) {
        String query = "DELETE FROM INPT_RAROC WHERE v_rec_ref_no = ? "
                + "AND N_FACILITY_NO = ? ";
        int i = getJdbcTemplate().update(query, new Object[]{recref, id});

        String sql = "UPDATE INPT_RAROC SET N_FACILITY_NO = N_FACILITY_NO - 1 WHERE v_rec_ref_no = ? AND  N_FACILITY_NO >  ? ";
        getJdbcTemplate().update(sql, new Object[]{recref, id});

        return i;
    }

    @Override
    public RarocMasterModel getRatingData(String ratingId) {
        String sql = "SELECT COUNT(*) FROM t_rdm_ram_cust_mast Where V_TOOL_CODE = ? ";
        int rowCount = getJdbcTemplate().queryForObject(sql, new Object[]{ratingId}, Integer.class);
        RarocMasterModel obj = null;
        if (rowCount == 0) {
            try {
                throw new CustomException();
            } catch (CustomException ex) {
                Logger.getLogger(RarocDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            String query = "SELECT V_TOOL_CODE V_RATING_ID,V_CUST_ID CUSTID,V_CUST_NAME,V_PAN,V_UCIF_CODE,M_NAME V_MODEL_CD,COMP3_OVERALLGRADE V_INT_RATING, "
                    + " V_RAS_SEG_CODE V_BUSS_UNIT,V_CUST_INDUSTRY FROM t_rdm_ram_cust_mast Where V_TOOL_CODE = ? ";
                obj = (RarocMasterModel) getJdbcTemplate().queryForObject(
                    query, new Object[]{ratingId}, new RowMapper<RarocMasterModel>() {
                @Override
                public RarocMasterModel mapRow(ResultSet rs, int i) throws SQLException {
                    RarocMasterModel obj = new RarocMasterModel();
                    obj.setRid(rs.getString("V_RATING_ID"));
                    obj.setCid(rs.getString("CUSTID"));
                    obj.setCname(rs.getString("V_CUST_NAME"));
                    obj.setPan(rs.getString("V_PAN"));
                    obj.setUfce(rs.getString("V_UCIF_CODE"));
                    obj.setRtool(rs.getString("V_MODEL_CD"));
                    obj.setIntRat(rs.getString("V_INT_RATING"));
                    obj.setBussunit(rs.getString("V_BUSS_UNIT"));
                    obj.setInd(rs.getString("V_CUST_INDUSTRY"));
                    return obj;
                }
            });
            
        }
        return obj;
    }

    @Override
    public List<OptionsModel> listRatingIds() {
        String query = "SELECT V_TOOL_CODE COMP_COMPCODE, V_TOOL_CODE V_RATING_ID FROM t_rdm_ram_cust_mast";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        rows.stream().map((row) -> {
            OptionsModel obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("COMP_COMPCODE")));
            obj.setValue(fmt.ToString(row.get("V_RATING_ID")));
            return obj;
        }).forEach((obj) -> {
            lObj.add(obj);
        });
        return lObj;
    }

    @Override
    public List<OptionsModel> listReFreq() {
        String query = "SELECT  V_FREQUENCY_CD, V_FREQUENCY_DETAIL FROM MST_REPRICE_FREQUENCY "
                + " ORDER BY N_ORDER asc";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        obj.setKey("");
        obj.setValue("Select...");
        lObj.add(obj);
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("V_FREQUENCY_CD")));
            obj.setValue(fmt.ToString(row.get("V_FREQUENCY_DETAIL")));
            lObj.add(obj);
        }
        return lObj;
    }

    @Override
    public String callCostOfFundFunc(String facType, String country, String intType, String curr, String repFreq, String mult,
            String oriDate, String OriMat) {
        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(getJdbcTemplate().getDataSource())
                .withSchemaName(schemaName)
                .withFunctionName("RAROC_COF_CALC")
                .withReturnValue()
                .useInParameterNames("in_facility_type")
                .useInParameterNames("in_country")
                .useInParameterNames("in_interest_type")
                .useInParameterNames("in_currency")
                .useInParameterNames("in_ORIGIN_date")
                .useInParameterNames("in_ORIGINAL_MATURITY")
                .useInParameterNames("in_repricing_frequency")
                .useInParameterNames("IN_REPRICING_FREQUENCY_MULT")
                .declareParameters(
                        new SqlParameter("in_facility_type", java.sql.Types.VARCHAR),
                        new SqlParameter("in_country", java.sql.Types.VARCHAR),
                        new SqlParameter("in_interest_type", java.sql.Types.VARCHAR),
                        new SqlParameter("in_currency", java.sql.Types.VARCHAR),
                        new SqlParameter("in_ORIGIN_date", java.sql.Types.VARCHAR),
                        new SqlParameter("in_ORIGINAL_MATURITY", java.sql.Types.VARCHAR),
                        new SqlParameter("in_repricing_frequency", java.sql.Types.VARCHAR),
                        new SqlParameter("IN_REPRICING_FREQUENCY_MULT", java.sql.Types.VARCHAR)
                );
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("in_facility_type", facType);
        map.put("in_country", country);
        map.put("in_interest_type", intType);
        map.put("in_currency", curr);
        map.put("in_ORIGIN_date", oriDate);
        map.put("in_ORIGINAL_MATURITY", OriMat);
        map.put("in_repricing_frequency", repFreq);
        map.put("IN_REPRICING_FREQUENCY_MULT", mult);
        BigDecimal result = jdbcCall.executeFunction(BigDecimal.class, map);

        System.out.println("facType- " + facType);
        System.out.println("country = " + country);
        System.out.println("intType = " + intType);
        System.out.println("curr = " + curr);
        System.out.println("oriDate = " + oriDate);
        System.out.println("OriMat = " + OriMat);
        System.out.println("repFreq = " + repFreq);
        System.out.println("mult = " + mult);
        System.out.println("result = " + result);
        return result.toString();
    }

    @Override
    public GridWithFooterPage<RarocViewModel> gridRarocNewView(String ref, Integer unit) {
        List<RarocViewModel> facs = new ArrayList<>();
        RarocViewModel footer = new RarocViewModel();
        String sql = "Select ((nvl(N_PRO_AXIS_FUND,0)* nvl(N_NPLL,0)) * 0.75)/100000 val FROM INPT_RAROC_MASTER Where v_rec_ref_no = ?";
        Double ln_protion = getJdbcTemplate().queryForObject(sql, new Object[]{ref}, Double.class);
        /*String query = "SELECT to_char(INPT.D_ORIGIN_DATE,'dd-Mon-yyyy') D_ORIGIN_DATE,inpt.v_psl,inpt.n_original_maturity,inpt.N_AVG_UTL_CUR * 100 N_AVG_UTL_CUR ,inpt.N_AVG_BAL_CUR,inpt.v_benchmark,   "
                + " (inpt.N_AVG_BAL_CUR * inpt.n_int_rate_comm) * rslt.n_days_remain N_INT_INCOME,   "
                + " (inpt.N_AVG_BAL_CUR * inpt.n_cost_funds) * rslt.n_days_remain N_FTP,   "
                + " (nvl(rslt.n_oth_income,0) + nvl(rslt.n_amortized_fee,0)) N_CREDIT_FEE,   "
                + "  NVL(N_IRB_EAD,0) N_IRB_EAD , null N_LIAB_FEE, "
                + " (inpt.N_BA_CDAB+inpt.N_TERM_DEPO+inpt.N_CA_FEE+inpt.N_CMS_FEE+inpt.N_OTHER+inpt.N_FOREX_COMMIS) N_NON_CREDIT_FEE,   "
                + " inpt.v_rec_ref_no, inpt.v_fac_type, inpt.v_fac_desc, inpt.v_asset_type, inpt.v_curr,    "
                + " (inpt.n_amount * inpt.n_excge_rate) n_amount, inpt.n_tenure, inpt.n_excge_rate, mast.v_rating_tool_code, mast.v_int_rating,    "
                + " inpt.n_avg_utili * 100 n_avg_utili, inpt.n_int_rate_comm *100 n_int_rate_comm,    "
                + " inpt.n_cost_funds * 100 n_cost_funds, inpt.n_upfront_fee,    "
                + " (inpt.n_annual_fee * inpt.n_amount * inpt.n_excge_rate) n_annual_fee,    "
                + " inpt.n_cash_margin * 100 n_cash_margin, inpt.v_cash_mismatch, inpt.v_long_ext, inpt.v_short_ext,    "
                + " inpt.n_exp_guar * 100 n_exp_guar, inpt.v_guarantor_int, inpt.v_guarantor_ext, inpt.v_guar_type,    "
                + " inpt.v_int_type, rslt.n_rwa_rs , rslt.n_ead_rs, rslt.n_el, rslt.n_nii, rslt.n_amortized_fee,    "
                + " rslt.n_opex, rslt.n_pbt, rslt.n_pat, rslt.n_alloc_capital, rslt.n_income_alloc_capital,    "
                + " rslt.n_adjusted_pat, rslt.n_raroc * 100 n_raroc, rslt.n_drawn_ccf * 100 n_drawn_ccf,    "
                + " rslt.n_undrawn_ccf * 100 n_undrawn_ccf, rslt.n_g_sec * 100 n_g_sec, rslt.n_car * 100 n_car,    "
                + " rslt.n_tax_rate * 100 n_tax_rate, case when rslt.n_facility_no <>  99   then (rslt.n_rwa_rs /rslt.n_ead_rs) end  * 100 n_rw, rslt.n_nim * 100 n_nim, rslt.n_oth_income,    "
                + " rslt.n_facility_no, mast.v_business_unit, mast.v_cust_name, inpt.v_ucicf,    "
                + " inpt.v_restructured_status, inpt.v_templated_rating    "
                + " FROM RSLT_RAROC rslt LEFT OUTER JOIN INPT_RAROC inpt ON inpt.v_rec_ref_no = rslt.v_rec_ref_no    "
                + " AND inpt.n_facility_no = rslt.n_facility_no    "
                + " LEFT OUTER JOIN INPT_RAROC_MASTER mast ON mast.v_rec_ref_no = rslt.v_rec_ref_no    "
                + " WHERE rslt.v_rec_ref_no = ?    "
                + " AND   rslt.v_result_type = 'raroc'    "
                + " ORDER BY rslt.n_facility_no nulls last";*/
 /*String query = " With tab as (   "
                + "  SELECT to_char(INPT.D_ORIGIN_DATE,'dd-Mon-yyyy') D_ORIGIN_DATE,inpt.v_psl,inpt.n_original_maturity,inpt.N_AVG_UTL_CUR * 100 N_AVG_UTL_CUR ,inpt.N_AVG_BAL_CUR,inpt.v_benchmark,      "
                + "  CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_INT_INCOME_BOND,2) ELSE"
                + "  (inpt.N_AVG_BAL_CUR * inpt.n_int_rate_comm) * (rslt.n_days_remain/365) END N_INT_INCOME,      "
                + "  CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_FTP_BOND,2) ELSE"
                + "  (inpt.N_AVG_BAL_CUR * inpt.n_cost_funds) * (rslt.n_days_remain/365) END N_FTP,      "
                + "  CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_FEES,2) ELSE"
                + "  (nvl(rslt.n_oth_income,0) + nvl(rslt.n_amortized_fee,0)) END N_CREDIT_FEE,      "
                + "  NVL(N_IRB_EAD,0) N_IRB_EAD , N_LIAB_FEE,    "
                + "  N_NON_CREDIT_FEE,      "
                + "  inpt.v_rec_ref_no, inpt.v_fac_type, inpt.v_fac_desc, inpt.v_asset_type, inpt.v_curr,       "
                + "  (inpt.n_amount * inpt.n_excge_rate) n_amount, inpt.n_tenure, inpt.n_excge_rate, mast.v_rating_tool_code, mast.v_int_rating,       "
                + "  inpt.n_avg_utili * 100 n_avg_utili, nvl(inpt.n_int_rate_comm, inpt.N_YEILD) *100 n_int_rate_comm,       "
                + "  CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then rslt.n_cost_funds * 100 else"
                + "  inpt.n_cost_funds * 100 end n_cost_funds, inpt.n_upfront_fee,       "
                + "  "
                + "  (inpt.n_annual_fee * inpt.n_amount * inpt.n_excge_rate) n_annual_fee,       "
                + "  inpt.n_cash_margin * 100 n_cash_margin, inpt.v_cash_mismatch, inpt.v_long_ext, inpt.v_short_ext,       "
                + "  inpt.n_exp_guar * 100 n_exp_guar, inpt.v_guarantor_int, inpt.v_guarantor_ext, inpt.v_guar_type,       "
                + "  inpt.v_int_type, rslt.n_rwa_rs , rslt.n_ead_rs, rslt.n_el, rslt.n_nii, rslt.n_amortized_fee,       "
                + "  rslt.n_opex, rslt.n_pbt, rslt.n_pat, rslt.n_alloc_capital, rslt.n_income_alloc_capital,       "
                + "  rslt.n_adjusted_pat, rslt.n_raroc * 100 n_raroc, rslt.n_drawn_ccf * 100 n_drawn_ccf,       "
                + "  rslt.n_undrawn_ccf * 100 n_undrawn_ccf, rslt.n_g_sec * 100 n_g_sec, rslt.n_car * 100 n_car,       "
                + "  rslt.n_tax_rate * 100 n_tax_rate, case when rslt.n_facility_no <>  99   then (rslt.n_rwa_rs /rslt.n_ead_rs) end  * 100 n_rw, rslt.n_nim * 100 n_nim, rslt.n_oth_income,       "
                + "  rslt.n_facility_no, mast.v_business_unit, mast.v_cust_name, inpt.v_ucicf,       "
                + "  inpt.v_restructured_status, inpt.v_templated_rating       "
                + "  FROM RSLT_RAROC rslt LEFT OUTER JOIN INPT_RAROC inpt ON inpt.v_rec_ref_no = rslt.v_rec_ref_no       "
                + "  AND inpt.n_facility_no = rslt.n_facility_no       "
                + "  LEFT OUTER JOIN INPT_RAROC_MASTER mast ON mast.v_rec_ref_no = rslt.v_rec_ref_no       "
                + "  WHERE rslt.v_rec_ref_no = ? and rslt.n_facility_no <> 99   "
                + "  AND   rslt.v_result_type IN ('raroc','raroc-CA','raroc-SA','raroc-TD','raroc-CMS','raroc-FX','raroc-OTH')       "
                + "  ORDER BY rslt.n_facility_no nulls last)   "
                + "  Select n_facility_no, v_fac_desc,v_fac_type,D_ORIGIN_DATE,V_LONG_EXT,N_EXP_GUAR,V_PSL,n_rw,v_curr,n_original_maturity,n_tenure,N_AVG_UTL_CUR,v_int_type,v_benchmark,n_int_rate_comm,n_cost_funds,n_nim,n_ead_rs,N_IRB_EAD,N_AVG_BAL_CUR,n_alloc_capital,N_LIAB_FEE,N_INT_INCOME,N_FTP,   "
                + "  n_nii,N_CREDIT_FEE,N_NON_CREDIT_FEE,n_opex,n_el,n_pbt,n_pat,n_rwa_rs,n_raroc from tab   "
                + "  UNION ALL   "
                + "  Select 100 n_facility_no, null v_fac_desc,null v_fac_type, null D_ORIGIN_DATE,null V_LONG_EXT, null N_EXP_GUAR, null V_PSL,SUM(n_rw),MAX(v_curr),null n_original_maturity,null n_tenure,null N_AVG_UTL_CUR,null v_int_type,null v_benchmark,AVG(n_int_rate_comm),AVG(n_cost_funds),SUM(n_nim),SUM(n_ead_rs),SUM(N_IRB_EAD), null N_AVG_BAL_CUR,SUM(n_alloc_capital),SUM(N_LIAB_FEE),SUM(N_INT_INCOME),SUM(N_FTP),   "
                + "  SUM(n_nii),SUM(N_CREDIT_FEE),SUM(N_NON_CREDIT_FEE),SUM(n_opex),SUM(n_el),SUM(n_pbt),SUM(n_pat),SUM(n_rwa_rs),null from tab";*/
        String query = "Select N_FACILITY_NO ,V_FAC_DESC,V_FAC_TYPE,D_ORIGIN_DATE,V_LONG_EXT,N_EXP_GUAR,V_PSL,N_RW,V_CURR,N_ORIGINAL_MATURITY,N_TENURE,N_AVG_UTL_CUR,V_INT_TYPE,V_BENCHMARK,N_INT_RATE_COMM, "
                + " N_COST_FUNDS,N_NIM,round(N_EAD_RS,2) N_EAD_RS,round(N_IRB_EAD,2) N_IRB_EAD,round(N_AVG_BAL_CUR,2) N_AVG_BAL_CUR,round(N_ALLOC_CAPITAL,2) N_ALLOC_CAPITAL,round(N_LIAB_FEE,2) N_LIAB_FEE,round(N_INT_INCOME,2) N_INT_INCOME,round(N_FTP,2) N_FTP,round(N_NII,2) N_NII,round(N_CREDIT_FEE,2) N_CREDIT_FEE,round(N_NON_CREDIT_FEE,2) N_NON_CREDIT_FEE,round(N_OPEX,2) N_OPEX,round(N_EL,2) N_EL,round(N_PBT,2) N_PBT,round(N_PAT,2) N_PAT,round(N_RWA_RS,2) N_RWA_RS,N_RAROC from ( "
                + " With tab as (SELECT to_char(INPT.D_ORIGIN_DATE,'dd-Mon-yyyy') D_ORIGIN_DATE,inpt.v_psl,inpt.n_original_maturity,inpt.N_AVG_UTL_CUR * 100 N_AVG_UTL_CUR ,inpt.N_AVG_BAL_CUR/100000 N_AVG_BAL_CUR,inpt.v_benchmark,       "
                + " CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_INT_INCOME_BOND,2) ELSE "
                + " ((nvl(inpt.N_AVG_BAL_CUR,inpt.N_AVG_UTL_CUR*INPT.N_AMOUNT) * inpt.n_int_rate_comm) * (rslt.n_days_remain/365)) * inpt.n_excge_rate END/100000 N_INT_INCOME,       "
                + " CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_FTP_BOND,2) ELSE "
                + " ((nvl(inpt.N_AVG_BAL_CUR,inpt.N_AVG_UTL_CUR*INPT.N_AMOUNT) * inpt.n_cost_funds) * (rslt.n_days_remain/365)) * inpt.n_excge_rate   END /100000 N_FTP,       "
                + " CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_FEES,2) ELSE "
                + " (nvl(rslt.n_oth_income,0) + nvl(rslt.n_amortized_fee,0)) END/100000 N_CREDIT_FEE,       "
                + " NVL(N_IRB_EAD,0)/100000 N_IRB_EAD , N_LIAB_FEE/100000 N_LIAB_FEE,     "
                + " N_NON_CREDIT_FEE/100000 N_NON_CREDIT_FEE,       "
                + " inpt.v_rec_ref_no, inpt.v_fac_type, inpt.v_fac_desc, inpt.v_asset_type, inpt.v_curr,        "
                + " (inpt.n_amount * inpt.n_excge_rate) n_amount, inpt.n_tenure, inpt.n_excge_rate, mast.v_rating_tool_code, mast.v_int_rating,        "
                + " inpt.n_avg_utili * 100 n_avg_utili, nvl(inpt.n_int_rate_comm, inpt.N_YEILD) *100 n_int_rate_comm,        "
                + " CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then rslt.n_cost_funds * 100 else "
                + " inpt.n_cost_funds * 100 end n_cost_funds, inpt.n_upfront_fee,        "
                + " (inpt.n_annual_fee * inpt.n_amount * inpt.n_excge_rate) n_annual_fee,        "
                + " inpt.n_cash_margin * 100 n_cash_margin, inpt.v_cash_mismatch, inpt.v_long_ext, inpt.v_short_ext,        "
                + " inpt.n_exp_guar * 100 n_exp_guar, inpt.v_guarantor_int, inpt.v_guarantor_ext, inpt.v_guar_type,        "
                + " inpt.v_int_type, rslt.n_rwa_rs/100000 n_rwa_rs , rslt.n_ead_rs/100000 n_ead_rs, rslt.n_el/100000 n_el, rslt.n_nii/100000 n_nii, rslt.n_amortized_fee,        "
                + " rslt.n_opex/100000 n_opex, rslt.n_pbt/100000 n_pbt, rslt.n_pat/100000 n_pat, rslt.n_alloc_capital/100000 n_alloc_capital, rslt.n_income_alloc_capital,        "
                + " rslt.n_adjusted_pat, case when rslt.n_ead_rs = 0 then null else rslt.n_raroc end * 100 n_raroc, rslt.n_drawn_ccf * 100 n_drawn_ccf,        "
                + " rslt.n_undrawn_ccf * 100 n_undrawn_ccf, rslt.n_g_sec * 100 n_g_sec, rslt.n_car * 100 n_car,        "
                + " rslt.n_tax_rate * 100 n_tax_rate, case when rslt.n_facility_no <>  99   then (case when rslt.n_ead_rs = 0 then null else rslt.n_rwa_rs /rslt.n_ead_rs end) end  * 100 n_rw, rslt.n_nim * 100 n_nim, rslt.n_oth_income,        "
                + " rslt.n_facility_no, mast.v_business_unit, mast.v_cust_name, inpt.v_ucicf,        "
                + " inpt.v_restructured_status, inpt.v_templated_rating        "
                + " FROM RSLT_RAROC rslt LEFT OUTER JOIN INPT_RAROC inpt ON inpt.v_rec_ref_no = rslt.v_rec_ref_no        "
                + " AND inpt.n_facility_no = rslt.n_facility_no        "
                + " LEFT OUTER JOIN INPT_RAROC_MASTER mast ON mast.v_rec_ref_no = rslt.v_rec_ref_no        "
                + " WHERE rslt.v_rec_ref_no = ? and rslt.n_facility_no <> 99    "
                + " AND rslt.v_result_type IN ('raroc','raroc-CA','raroc-SA','raroc-TD','raroc-CMS','raroc-FX','raroc-OTH')        "
                + " ORDER BY rslt.n_facility_no nulls last)    "
                + " Select n_facility_no, v_fac_desc,v_fac_type,D_ORIGIN_DATE,V_LONG_EXT,N_EXP_GUAR,V_PSL,n_rw,v_curr,n_original_maturity,n_tenure,N_AVG_UTL_CUR,v_int_type,v_benchmark,n_int_rate_comm,n_cost_funds,n_nim,n_ead_rs,N_IRB_EAD,N_AVG_BAL_CUR,n_alloc_capital,N_LIAB_FEE,N_INT_INCOME,N_FTP,    "
                + " n_nii,N_CREDIT_FEE,N_NON_CREDIT_FEE,n_opex,n_el,n_pbt,n_pat,n_rwa_rs,n_raroc from tab    "
                + " UNION ALL    "
                + " Select 100 n_facility_no, null v_fac_desc,null v_fac_type, null D_ORIGIN_DATE,null V_LONG_EXT, null N_EXP_GUAR, null V_PSL,SUM(n_rw),MAX(v_curr),null n_original_maturity,null n_tenure,null N_AVG_UTL_CUR,null v_int_type,null v_benchmark,AVG(n_int_rate_comm),AVG(n_cost_funds),SUM(n_nim),SUM(n_ead_rs),SUM(N_IRB_EAD), null N_AVG_BAL_CUR,SUM(n_alloc_capital),SUM(N_LIAB_FEE),SUM(N_INT_INCOME),SUM(N_FTP),    "
                + " SUM(n_nii),SUM(N_CREDIT_FEE),SUM(N_NON_CREDIT_FEE),SUM(n_opex),SUM(n_el),SUM(n_pbt),SUM(n_pat),SUM(n_rwa_rs) + " + ln_protion + " ,CASE WHEN SUM(n_alloc_capital) = 0 THEN null ELSE  trunc(SUM(n_pat)/SUM(n_alloc_capital)*100,2) END from tab)";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{ref});
        int i = 0, cols = 32;
        String[][] resultArray = new String[26][cols];
        String[] outputName = new String[cols];
        String[] creditTotal = new String[cols];
        String[] caTotal = new String[cols];
        String[] saTotal = new String[cols];
        String[] tdTotal = new String[cols];
        String[] cmsTotal = new String[cols];
        String[] fxTotal = new String[cols];
        String[] othTotal = new String[cols];
        String[] total = new String[cols];
        outputName[0] = "Facility Description";
        outputName[1] = "Facility Type";
        outputName[2] = "Date of Disbursement";
        outputName[3] = "External Rating (Borrower/Guarantor)";
        outputName[4] = "% Exposure Guranteed";
        outputName[5] = "PSL/Non PSL";
        outputName[6] = "RWA Intensity(%)";
        outputName[7] = "Currency";
        outputName[8] = "Contractual Maturity(Months)";
        outputName[9] = "Average Maturity(Months)";
        outputName[10] = "Average Utilization (%)";
        outputName[11] = "Interest Type";
        outputName[12] = "Interest rate Benchmark";
        outputName[13] = "Interest Rate(%)";
        outputName[14] = "Net FTP (%)";
        outputName[15] = "NIM (%)";
        outputName[16] = "STD EAD (Rs Lakh)";
        outputName[17] = "IRB EAD (Rs Lakh)";
        outputName[18] = "Avg O/S/ Bal (Rs Lakh)";
        outputName[19] = "Capital ( Rs Lakh)";
        outputName[20] = "Liability CDAB (Rs Lakh)";
        outputName[21] = "Interest Income (Rs Lakh)";
        outputName[22] = "(-) FTP (Rs Lakh)";
        outputName[23] = "NII (Rs Lakh)";
        outputName[24] = "(+)Credit Fee/Commision (Rs Lakh)";
        outputName[25] = "(+)Non-credit Fees (Rs Lakh)";
        outputName[26] = "(-)Opex (Rs Lakh)";
        outputName[27] = "(-)EL (Rs Lakh)";
        outputName[28] = "PBT (Rs Lakh)";
        outputName[29] = "PAT (Rs Lakh)";
        outputName[30] = "RWA (Rs Lakh)";
        outputName[31] = "RAROC (%)";
        for (Map row : rows) {
            if (row.get("n_facility_no") == null) {
                total[0] = fmt.ToString(row.get("v_fac_desc"));
                total[1] = fmt.ToString(row.get("v_fac_type"));
                total[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                total[3] = fmt.ToString(row.get("V_LONG_EXT"));
                total[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                total[5] = fmt.ToString(row.get("V_PSL"));
                total[6] = fmt.ToString(row.get("n_rw"));
                total[7] = fmt.ToString(row.get("v_curr"));
                total[8] = fmt.ToString(row.get("n_original_maturity"));
                total[9] = fmt.ToString(row.get("n_tenure"));
                total[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                total[11] = fmt.ToString(row.get("v_int_type"));
                total[12] = fmt.ToString(row.get("v_benchmark"));
                total[13] = fmt.ToString(row.get("n_int_rate_comm"));
                total[14] = fmt.ToString(row.get("n_cost_funds"));
                total[15] = fmt.ToString(row.get("n_nim"));
                total[16] = fmt.ToString(row.get("n_ead_rs"));
                total[17] = fmt.ToString(row.get("N_IRB_EAD"));
                total[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                total[19] = fmt.ToString(row.get("n_alloc_capital"));
                total[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                total[21] = fmt.ToString(row.get("N_INT_INCOME"));
                total[22] = fmt.ToString(row.get("N_FTP"));
                total[23] = fmt.ToString(row.get("n_nii"));
                total[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                total[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                total[26] = fmt.ToString(row.get("n_opex"));
                total[27] = fmt.ToString(row.get("n_el"));
                total[28] = fmt.ToString(row.get("n_pbt"));
                total[29] = fmt.ToString(row.get("n_pat"));//rwa_rs/ead_rs*100
                total[30] = fmt.ToString(row.get("n_rwa_rs"));
                total[31] = fmt.ToString(row.get("n_raroc"));

            } else if (fmt.ToInteger(row.get("n_facility_no")) == 100) {
                creditTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                creditTotal[1] = fmt.ToString(row.get("v_fac_type"));
                creditTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                creditTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                creditTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                creditTotal[5] = fmt.ToString(row.get("V_PSL"));
                creditTotal[6] = fmt.ToString(row.get("n_rw"));
                creditTotal[7] = fmt.ToString(row.get("v_curr"));
                creditTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                creditTotal[9] = fmt.ToString(row.get("n_tenure"));
                creditTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                creditTotal[11] = fmt.ToString(row.get("v_int_type"));
                creditTotal[12] = fmt.ToString(row.get("v_benchmark"));
                creditTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                creditTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                creditTotal[15] = fmt.ToString(row.get("n_nim"));
                creditTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                creditTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                creditTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                creditTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                creditTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                creditTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                creditTotal[22] = fmt.ToString(row.get("N_FTP"));
                creditTotal[23] = fmt.ToString(row.get("n_nii"));
                creditTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                creditTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                creditTotal[26] = fmt.ToString(row.get("n_opex"));
                creditTotal[27] = fmt.ToString(row.get("n_el"));
                creditTotal[28] = fmt.ToString(row.get("n_pbt"));
                creditTotal[29] = fmt.ToString(row.get("n_pat"));
                creditTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                creditTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 93) {
                caTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                caTotal[1] = fmt.ToString(row.get("v_fac_type"));
                caTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                caTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                caTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                caTotal[5] = fmt.ToString(row.get("V_PSL"));
                caTotal[6] = fmt.ToString(row.get("n_rw"));
                caTotal[7] = fmt.ToString(row.get("v_curr"));
                caTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                caTotal[9] = fmt.ToString(row.get("n_tenure"));
                caTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                caTotal[11] = fmt.ToString(row.get("v_int_type"));
                caTotal[12] = fmt.ToString(row.get("v_benchmark"));
                caTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                caTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                caTotal[15] = fmt.ToString(row.get("n_nim"));
                caTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                caTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                caTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                caTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                caTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                caTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                caTotal[22] = fmt.ToString(row.get("N_FTP"));
                caTotal[23] = fmt.ToString(row.get("n_nii"));
                caTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                caTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                caTotal[26] = fmt.ToString(row.get("n_opex"));
                caTotal[27] = fmt.ToString(row.get("n_el"));
                caTotal[28] = fmt.ToString(row.get("n_pbt"));
                caTotal[29] = fmt.ToString(row.get("n_pat"));
                caTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                caTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 94) {
                saTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                saTotal[1] = fmt.ToString(row.get("v_fac_type"));
                saTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                saTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                saTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                saTotal[5] = fmt.ToString(row.get("V_PSL"));
                saTotal[6] = fmt.ToString(row.get("n_rw"));
                saTotal[7] = fmt.ToString(row.get("v_curr"));
                saTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                saTotal[9] = fmt.ToString(row.get("n_tenure"));
                saTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                saTotal[11] = fmt.ToString(row.get("v_int_type"));
                saTotal[12] = fmt.ToString(row.get("v_benchmark"));
                saTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                saTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                saTotal[15] = fmt.ToString(row.get("n_nim"));
                saTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                saTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                saTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                saTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                saTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                saTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                saTotal[22] = fmt.ToString(row.get("N_FTP"));
                saTotal[23] = fmt.ToString(row.get("n_nii"));
                saTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                saTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                saTotal[26] = fmt.ToString(row.get("n_opex"));
                saTotal[27] = fmt.ToString(row.get("n_el"));
                saTotal[28] = fmt.ToString(row.get("n_pbt"));
                saTotal[29] = fmt.ToString(row.get("n_pat"));
                saTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                saTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 95) {
                tdTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                tdTotal[1] = fmt.ToString(row.get("v_fac_type"));
                tdTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                tdTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                tdTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                tdTotal[5] = fmt.ToString(row.get("V_PSL"));
                tdTotal[6] = fmt.ToString(row.get("n_rw"));
                tdTotal[7] = fmt.ToString(row.get("v_curr"));
                tdTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                tdTotal[9] = fmt.ToString(row.get("n_tenure"));
                tdTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                tdTotal[11] = fmt.ToString(row.get("v_int_type"));
                tdTotal[12] = fmt.ToString(row.get("v_benchmark"));
                tdTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                tdTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                tdTotal[15] = fmt.ToString(row.get("n_nim"));
                tdTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                tdTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                tdTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                tdTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                tdTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                tdTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                tdTotal[22] = fmt.ToString(row.get("N_FTP"));
                tdTotal[23] = fmt.ToString(row.get("n_nii"));
                tdTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                tdTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                tdTotal[26] = fmt.ToString(row.get("n_opex"));
                tdTotal[27] = fmt.ToString(row.get("n_el"));
                tdTotal[28] = fmt.ToString(row.get("n_pbt"));
                tdTotal[29] = fmt.ToString(row.get("n_pat"));
                tdTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                tdTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 96) {
                cmsTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                cmsTotal[1] = fmt.ToString(row.get("v_fac_type"));
                cmsTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                cmsTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                cmsTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                cmsTotal[5] = fmt.ToString(row.get("V_PSL"));
                cmsTotal[6] = fmt.ToString(row.get("n_rw"));
                cmsTotal[7] = fmt.ToString(row.get("v_curr"));
                cmsTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                cmsTotal[9] = fmt.ToString(row.get("n_tenure"));
                cmsTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                cmsTotal[11] = fmt.ToString(row.get("v_int_type"));
                cmsTotal[12] = fmt.ToString(row.get("v_benchmark"));
                cmsTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                cmsTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                cmsTotal[15] = fmt.ToString(row.get("n_nim"));
                cmsTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                cmsTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                cmsTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                cmsTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                cmsTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                cmsTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                cmsTotal[22] = fmt.ToString(row.get("N_FTP"));
                cmsTotal[23] = fmt.ToString(row.get("n_nii"));
                cmsTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                cmsTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                cmsTotal[26] = fmt.ToString(row.get("n_opex"));
                cmsTotal[27] = fmt.ToString(row.get("n_el"));
                cmsTotal[28] = fmt.ToString(row.get("n_pbt"));
                cmsTotal[29] = fmt.ToString(row.get("n_pat"));
                cmsTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                cmsTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 97) {
                fxTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                fxTotal[1] = fmt.ToString(row.get("v_fac_type"));
                fxTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                fxTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                fxTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                fxTotal[5] = fmt.ToString(row.get("V_PSL"));
                fxTotal[6] = fmt.ToString(row.get("n_rw"));
                fxTotal[7] = fmt.ToString(row.get("v_curr"));
                fxTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                fxTotal[9] = fmt.ToString(row.get("n_tenure"));
                fxTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                fxTotal[11] = fmt.ToString(row.get("v_int_type"));
                fxTotal[12] = fmt.ToString(row.get("v_benchmark"));
                fxTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                fxTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                fxTotal[15] = fmt.ToString(row.get("n_nim"));
                fxTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                fxTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                fxTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                fxTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                fxTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                fxTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                fxTotal[22] = fmt.ToString(row.get("N_FTP"));
                fxTotal[23] = fmt.ToString(row.get("n_nii"));
                fxTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                fxTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                fxTotal[26] = fmt.ToString(row.get("n_opex"));
                fxTotal[27] = fmt.ToString(row.get("n_el"));
                fxTotal[28] = fmt.ToString(row.get("n_pbt"));
                fxTotal[29] = fmt.ToString(row.get("n_pat"));
                fxTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                fxTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 98) {
                othTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                othTotal[1] = fmt.ToString(row.get("v_fac_type"));
                othTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                othTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                othTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                othTotal[5] = fmt.ToString(row.get("V_PSL"));
                othTotal[6] = fmt.ToString(row.get("n_rw"));
                othTotal[7] = fmt.ToString(row.get("v_curr"));
                othTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                othTotal[9] = fmt.ToString(row.get("n_tenure"));
                othTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                othTotal[11] = fmt.ToString(row.get("v_int_type"));
                othTotal[12] = fmt.ToString(row.get("v_benchmark"));
                othTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                othTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                othTotal[15] = fmt.ToString(row.get("n_nim"));
                othTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                othTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                othTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                othTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                othTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                othTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                othTotal[22] = fmt.ToString(row.get("N_FTP"));
                othTotal[23] = fmt.ToString(row.get("n_nii"));
                othTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                othTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                othTotal[26] = fmt.ToString(row.get("n_opex"));
                othTotal[27] = fmt.ToString(row.get("n_el"));
                othTotal[28] = fmt.ToString(row.get("n_pbt"));
                othTotal[29] = fmt.ToString(row.get("n_pat"));
                othTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                othTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else {
                resultArray[i][0] = fmt.ToString(row.get("v_fac_desc"));
                resultArray[i][1] = fmt.ToString(row.get("v_fac_type"));
                resultArray[i][2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                resultArray[i][3] = fmt.ToString(row.get("V_LONG_EXT"));
                resultArray[i][4] = fmt.ToString(row.get("N_EXP_GUAR"));
                resultArray[i][5] = fmt.ToString(row.get("V_PSL"));
                resultArray[i][6] = fmt.ToString(row.get("n_rw"));
                resultArray[i][7] = fmt.ToString(row.get("v_curr"));
                resultArray[i][8] = fmt.ToString(row.get("n_original_maturity"));
                resultArray[i][9] = fmt.ToString(row.get("n_tenure"));
                resultArray[i][10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                resultArray[i][11] = fmt.ToString(row.get("v_int_type"));
                resultArray[i][12] = fmt.ToString(row.get("v_benchmark"));
                resultArray[i][13] = fmt.ToString(row.get("n_int_rate_comm"));
                resultArray[i][14] = fmt.ToString(row.get("n_cost_funds"));
                resultArray[i][15] = fmt.ToString(row.get("n_nim"));
                resultArray[i][16] = fmt.ToString(row.get("n_ead_rs"));
                resultArray[i][17] = fmt.ToString(row.get("N_IRB_EAD"));
                resultArray[i][18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                resultArray[i][19] = fmt.ToString(row.get("n_alloc_capital"));
                resultArray[i][20] = fmt.ToString(row.get("N_LIAB_FEE"));
                resultArray[i][21] = fmt.ToString(row.get("N_INT_INCOME"));
                resultArray[i][22] = fmt.ToString(row.get("N_FTP"));
                resultArray[i][23] = fmt.ToString(row.get("n_nii"));
                resultArray[i][24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                resultArray[i][25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                resultArray[i][26] = fmt.ToString(row.get("n_opex"));
                resultArray[i][27] = fmt.ToString(row.get("n_el"));
                resultArray[i][28] = fmt.ToString(row.get("n_pbt"));
                resultArray[i][29] = fmt.ToString(row.get("n_pat"));
                resultArray[i][30] = fmt.ToString(row.get("n_rwa_rs"));
                resultArray[i][31] = fmt.ToString(row.get("n_raroc"));
                i++;
            }
        }
        i = 0;
        while (i < cols) {
            if (i == cols - 1) {
                footer.setId(i + 1);
                footer.setOutputName(outputName[i]);
                footer.setFacility1(resultArray[0][i]);
                footer.setFacility2(resultArray[1][i]);
                footer.setFacility3(resultArray[2][i]);
                footer.setFacility4(resultArray[3][i]);
                footer.setFacility5(resultArray[4][i]);
                footer.setFacility6(resultArray[5][i]);
                footer.setFacility7(resultArray[6][i]);
                footer.setFacility8(resultArray[7][i]);
                footer.setFacility9(resultArray[8][i]);
                footer.setFacility10(resultArray[9][i]);
                footer.setFacility11(resultArray[10][i]);
                footer.setFacility12(resultArray[11][i]);
                footer.setFacility13(resultArray[12][i]);
                footer.setFacility14(resultArray[13][i]);
                footer.setFacility15(resultArray[14][i]);
                footer.setFacility16(resultArray[15][i]);
                footer.setFacility17(resultArray[16][i]);
                footer.setFacility18(resultArray[17][i]);
                footer.setFacility19(resultArray[18][i]);
                footer.setFacility20(resultArray[19][i]);
                footer.setFacility21(resultArray[20][i]);
                footer.setFacility22(resultArray[21][i]);
                footer.setFacility23(resultArray[22][i]);
                footer.setFacility24(resultArray[23][i]);
                footer.setFacility25(resultArray[24][i]);

                footer.setCa(caTotal[i]);
                footer.setSa(saTotal[i]);
                footer.setTd(tdTotal[i]);
                footer.setCms(cmsTotal[i]);
                footer.setFx(fxTotal[i]);
                footer.setOther(othTotal[i]);
                footer.setTotal(creditTotal[i]);
            } else {
                RarocViewModel obj = new RarocViewModel();
                obj.setId(i + 1);
                obj.setOutputName(outputName[i]);
                obj.setFacility1(resultArray[0][i]);
                obj.setFacility2(resultArray[1][i]);
                obj.setFacility3(resultArray[2][i]);
                obj.setFacility4(resultArray[3][i]);
                obj.setFacility5(resultArray[4][i]);
                obj.setFacility6(resultArray[5][i]);
                obj.setFacility7(resultArray[6][i]);
                obj.setFacility8(resultArray[7][i]);
                obj.setFacility9(resultArray[8][i]);
                obj.setFacility10(resultArray[9][i]);
                obj.setFacility11(resultArray[10][i]);
                obj.setFacility12(resultArray[11][i]);
                obj.setFacility13(resultArray[12][i]);
                obj.setFacility14(resultArray[13][i]);
                obj.setFacility15(resultArray[14][i]);
                obj.setFacility16(resultArray[15][i]);
                obj.setFacility17(resultArray[16][i]);
                obj.setFacility18(resultArray[17][i]);
                obj.setFacility19(resultArray[18][i]);
                obj.setFacility20(resultArray[19][i]);
                obj.setFacility21(resultArray[20][i]);
                obj.setFacility22(resultArray[21][i]);
                obj.setFacility23(resultArray[22][i]);
                obj.setFacility24(resultArray[23][i]);
                obj.setFacility25(resultArray[24][i]);
                //obj.setCreditRaroc(creditTotal[i]);
                //obj.setTotal(total[i]);
                obj.setCa(caTotal[i]);
                obj.setSa(saTotal[i]);
                obj.setTd(tdTotal[i]);
                obj.setCms(cmsTotal[i]);
                obj.setFx(fxTotal[i]);
                obj.setOther(othTotal[i]);
                obj.setTotal(creditTotal[i]);
                facs.add(obj);
            }
            i++;
        }
        return new GridWithFooterPage<>(facs, footer, 1, 30, 30);
    }

    @Override
    public GridWithFooterPage<RarocViewModel> gridRarocNewViewNext(String ref, Integer unit) {
        List<RarocViewModel> facs = new ArrayList<>();
        RarocViewModel footer = new RarocViewModel();
        String sql = "Select ((nvl(N_PRO_AXIS_FUND,0)* nvl(N_NPLL,0)) * 0.75)/100000 val FROM INPT_RAROC_MASTER Where v_rec_ref_no = ?";
        Double ln_protion = getJdbcTemplate().queryForObject(sql, new Object[]{ref}, Double.class);
        /*String query = " With tab as (   "
                + "  SELECT to_char(INPT.D_ORIGIN_DATE,'dd-Mon-yyyy') D_ORIGIN_DATE,inpt.v_psl,inpt.n_original_maturity,inpt.N_AVG_UTL_NEXT * 100 N_AVG_UTL_CUR ,inpt.N_AVG_BAL_NEXT N_AVG_BAL_CUR,inpt.v_benchmark,      "
                + "  CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_INT_INCOME_BOND,2) ELSE"
                + "  (inpt.N_AVG_BAL_NEXT * inpt.n_int_rate_comm) * (rslt.n_days_remain/365) END N_INT_INCOME,      "
                + "  CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_FTP_BOND,2) ELSE"
                + "  (inpt.N_AVG_BAL_NEXT * inpt.n_cost_funds) * (rslt.n_days_remain/365) END N_FTP,      "
                + "  CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_FEES,2) ELSE"
                + "  (nvl(rslt.n_oth_income,0) + nvl(rslt.n_amortized_fee,0)) END N_CREDIT_FEE,      "
                + "  NVL(N_IRB_EAD,0) N_IRB_EAD , N_LIAB_FEE,    "
                + "  N_NON_CREDIT_FEE,      "
                + "  inpt.v_rec_ref_no, inpt.v_fac_type, inpt.v_fac_desc, inpt.v_asset_type, inpt.v_curr,       "
                + "  (inpt.n_amount * inpt.n_excge_rate) n_amount, inpt.n_tenure, inpt.n_excge_rate, mast.v_rating_tool_code, mast.v_int_rating,       "
                + "  inpt.n_avg_utili * 100 n_avg_utili, nvl(inpt.n_int_rate_comm, inpt.N_YEILD) *100 n_int_rate_comm,       "
                + "  CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then rslt.n_cost_funds * 100 else"
                + "  inpt.n_cost_funds * 100 end n_cost_funds, inpt.n_upfront_fee,       "
                + "  "
                + "  (inpt.n_annual_fee * inpt.n_amount * inpt.n_excge_rate) n_annual_fee,       "
                + "  inpt.n_cash_margin * 100 n_cash_margin, inpt.v_cash_mismatch, inpt.v_long_ext, inpt.v_short_ext,       "
                + "  inpt.n_exp_guar * 100 n_exp_guar, inpt.v_guarantor_int, inpt.v_guarantor_ext, inpt.v_guar_type,       "
                + "  inpt.v_int_type, rslt.n_rwa_rs , rslt.n_ead_rs, rslt.n_el, rslt.n_nii, rslt.n_amortized_fee,       "
                + "  rslt.n_opex, rslt.n_pbt, rslt.n_pat, rslt.n_alloc_capital, rslt.n_income_alloc_capital,       "
                + "  rslt.n_adjusted_pat, rslt.n_raroc * 100 n_raroc, rslt.n_drawn_ccf * 100 n_drawn_ccf,       "
                + "  rslt.n_undrawn_ccf * 100 n_undrawn_ccf, rslt.n_g_sec * 100 n_g_sec, rslt.n_car * 100 n_car,       "
                + "  rslt.n_tax_rate * 100 n_tax_rate, case when rslt.n_facility_no <>  99   then (rslt.n_rwa_rs /rslt.n_ead_rs) end  * 100 n_rw, rslt.n_nim * 100 n_nim, rslt.n_oth_income,       "
                + "  rslt.n_facility_no, mast.v_business_unit, mast.v_cust_name, inpt.v_ucicf,       "
                + "  inpt.v_restructured_status, inpt.v_templated_rating       "
                + "  FROM RSLT_RAROC_NEXT rslt LEFT OUTER JOIN INPT_RAROC inpt ON inpt.v_rec_ref_no = rslt.v_rec_ref_no       "
                + "  AND inpt.n_facility_no = rslt.n_facility_no       "
                + "  LEFT OUTER JOIN INPT_RAROC_MASTER mast ON mast.v_rec_ref_no = rslt.v_rec_ref_no       "
                + "  WHERE rslt.v_rec_ref_no = ? and rslt.n_facility_no <> 99   "
                + "  AND   rslt.v_result_type IN ('raroc','raroc-CA','raroc-SA','raroc-TD','raroc-CMS','raroc-FX','raroc-OTH')       "
                + "  ORDER BY rslt.n_facility_no nulls last)   "
                + "  Select n_facility_no, v_fac_desc,v_fac_type,D_ORIGIN_DATE,V_LONG_EXT,N_EXP_GUAR,V_PSL,n_rw,v_curr,n_original_maturity,n_tenure,N_AVG_UTL_CUR,v_int_type,v_benchmark,n_int_rate_comm,n_cost_funds,n_nim,n_ead_rs,N_IRB_EAD,N_AVG_BAL_CUR,n_alloc_capital,N_LIAB_FEE,N_INT_INCOME,N_FTP,   "
                + "  n_nii,N_CREDIT_FEE,N_NON_CREDIT_FEE,n_opex,n_el,n_pbt,n_pat,n_rwa_rs,n_raroc from tab   "
                + "  UNION ALL   "
                + "  Select 100 n_facility_no, null v_fac_desc,null v_fac_type, null D_ORIGIN_DATE,null V_LONG_EXT, null N_EXP_GUAR, null V_PSL,SUM(n_rw),MAX(v_curr),null n_original_maturity,null n_tenure,null N_AVG_UTL_CUR,null v_int_type,null v_benchmark,AVG(n_int_rate_comm),AVG(n_cost_funds),SUM(n_nim),SUM(n_ead_rs),SUM(N_IRB_EAD), null N_AVG_BAL_CUR,SUM(n_alloc_capital),SUM(N_LIAB_FEE),SUM(N_INT_INCOME),SUM(N_FTP),   "
                + "  SUM(n_nii),SUM(N_CREDIT_FEE),SUM(N_NON_CREDIT_FEE),SUM(n_opex),SUM(n_el),SUM(n_pbt),SUM(n_pat),SUM(n_rwa_rs),null from tab";*/
        String query = "Select N_FACILITY_NO ,V_FAC_DESC,V_FAC_TYPE,D_ORIGIN_DATE,V_LONG_EXT,N_EXP_GUAR,V_PSL,N_RW,V_CURR,N_ORIGINAL_MATURITY,N_TENURE,N_AVG_UTL_CUR,V_INT_TYPE,V_BENCHMARK,N_INT_RATE_COMM, "
                + " N_COST_FUNDS,N_NIM,round(N_EAD_RS,2) N_EAD_RS,round(N_IRB_EAD,2) N_IRB_EAD,round(N_AVG_BAL_CUR,2) N_AVG_BAL_CUR,round(N_ALLOC_CAPITAL,2) N_ALLOC_CAPITAL,round(N_LIAB_FEE,2) N_LIAB_FEE,round(N_INT_INCOME,2) N_INT_INCOME,round(N_FTP,2) N_FTP,round(N_NII,2) N_NII,round(N_CREDIT_FEE,2) N_CREDIT_FEE,round(N_NON_CREDIT_FEE,2) N_NON_CREDIT_FEE,round(N_OPEX,2) N_OPEX,round(N_EL,2) N_EL,round(N_PBT,2) N_PBT,round(N_PAT,2) N_PAT,round(N_RWA_RS,2) N_RWA_RS,N_RAROC from ( "
                + " With tab as (SELECT to_char(INPT.D_ORIGIN_DATE,'dd-Mon-yyyy') D_ORIGIN_DATE,inpt.v_psl,inpt.n_original_maturity,inpt.N_AVG_UTL_CUR * 100 N_AVG_UTL_CUR ,inpt.N_AVG_BAL_NEXT/100000 N_AVG_BAL_CUR,inpt.v_benchmark,       "
                + " CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_INT_INCOME_BOND,2) ELSE "
                + " ((nvl(inpt.N_AVG_BAL_NEXT,inpt.N_AVG_UTL_NEXT*INPT.N_AMOUNT) * inpt.n_int_rate_comm) * (rslt.n_days_remain/365)) * inpt.n_excge_rate END/100000 N_INT_INCOME,       "
                + " CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_FTP_BOND,2) ELSE "
                + " ((nvl(inpt.N_AVG_BAL_NEXT,inpt.N_AVG_UTL_NEXT*INPT.N_AMOUNT) * inpt.n_cost_funds) * (rslt.n_days_remain/365)) * inpt.n_excge_rate END/100000 N_FTP,       "
                + " CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then round(N_FEES,2) ELSE "
                + " (nvl(rslt.n_oth_income,0) + nvl(rslt.n_amortized_fee,0)) END/100000 N_CREDIT_FEE,       "
                + " NVL(N_IRB_EAD,0)/100000 N_IRB_EAD , N_LIAB_FEE/100000 N_LIAB_FEE,     "
                + " N_NON_CREDIT_FEE/100000 N_NON_CREDIT_FEE,       "
                + " inpt.v_rec_ref_no, inpt.v_fac_type, inpt.v_fac_desc, inpt.v_asset_type, inpt.v_curr,        "
                + " (inpt.n_amount * inpt.n_excge_rate) n_amount, inpt.n_tenure, inpt.n_excge_rate, mast.v_rating_tool_code, mast.v_int_rating,        "
                + " inpt.n_avg_utili * 100 n_avg_utili, nvl(inpt.n_int_rate_comm, inpt.N_YEILD) *100 n_int_rate_comm,        "
                + " CASE WHEN inpt.V_FACILITY_TYPE = 'BONDS' then rslt.n_cost_funds * 100 else "
                + " inpt.n_cost_funds * 100 end n_cost_funds, inpt.n_upfront_fee,        "
                + " (inpt.n_annual_fee * inpt.n_amount * inpt.n_excge_rate) n_annual_fee,        "
                + " inpt.n_cash_margin * 100 n_cash_margin, inpt.v_cash_mismatch, inpt.v_long_ext, inpt.v_short_ext,        "
                + " inpt.n_exp_guar * 100 n_exp_guar, inpt.v_guarantor_int, inpt.v_guarantor_ext, inpt.v_guar_type,        "
                + " inpt.v_int_type, rslt.n_rwa_rs/100000 n_rwa_rs , rslt.n_ead_rs/100000 n_ead_rs, rslt.n_el/100000 n_el, rslt.n_nii/100000 n_nii, rslt.n_amortized_fee,        "
                + " rslt.n_opex/100000 n_opex, rslt.n_pbt/100000 n_pbt, rslt.n_pat/100000 n_pat, rslt.n_alloc_capital/100000 n_alloc_capital, rslt.n_income_alloc_capital,        "
                + " rslt.n_adjusted_pat, case when rslt.n_ead_rs = 0 then null else rslt.n_raroc end * 100 n_raroc, rslt.n_drawn_ccf * 100 n_drawn_ccf,        "
                + " rslt.n_undrawn_ccf * 100 n_undrawn_ccf, rslt.n_g_sec * 100 n_g_sec, rslt.n_car * 100 n_car,        "
                + " rslt.n_tax_rate * 100 n_tax_rate, case when rslt.n_facility_no <>  99   then (case when rslt.n_ead_rs = 0 then null else rslt.n_rwa_rs /rslt.n_ead_rs end) end  * 100 n_rw, rslt.n_nim * 100 n_nim, rslt.n_oth_income,        "
                + " rslt.n_facility_no, mast.v_business_unit, mast.v_cust_name, inpt.v_ucicf,        "
                + " inpt.v_restructured_status, inpt.v_templated_rating        "
                + " FROM RSLT_RAROC_NEXT rslt LEFT OUTER JOIN INPT_RAROC inpt ON inpt.v_rec_ref_no = rslt.v_rec_ref_no        "
                + " AND inpt.n_facility_no = rslt.n_facility_no        "
                + " LEFT OUTER JOIN INPT_RAROC_MASTER mast ON mast.v_rec_ref_no = rslt.v_rec_ref_no        "
                + " WHERE rslt.v_rec_ref_no = ? and rslt.n_facility_no <> 99    "
                + " AND rslt.v_result_type IN ('raroc','raroc-CA','raroc-SA','raroc-TD','raroc-CMS','raroc-FX','raroc-OTH')        "
                + " ORDER BY rslt.n_facility_no nulls last)    "
                + " Select n_facility_no, v_fac_desc,v_fac_type,D_ORIGIN_DATE,V_LONG_EXT,N_EXP_GUAR,V_PSL,n_rw,v_curr,n_original_maturity,n_tenure,N_AVG_UTL_CUR,v_int_type,v_benchmark,n_int_rate_comm,n_cost_funds,n_nim,n_ead_rs,N_IRB_EAD,N_AVG_BAL_CUR,n_alloc_capital,N_LIAB_FEE,N_INT_INCOME,N_FTP,    "
                + " n_nii,N_CREDIT_FEE,N_NON_CREDIT_FEE,n_opex,n_el,n_pbt,n_pat,n_rwa_rs,n_raroc from tab    "
                + " UNION ALL    "
                + " Select 100 n_facility_no, null v_fac_desc,null v_fac_type, null D_ORIGIN_DATE,null V_LONG_EXT, null N_EXP_GUAR, null V_PSL,SUM(n_rw),MAX(v_curr),null n_original_maturity,null n_tenure,null N_AVG_UTL_CUR,null v_int_type,null v_benchmark,AVG(n_int_rate_comm),AVG(n_cost_funds),SUM(n_nim),SUM(n_ead_rs),SUM(N_IRB_EAD), null N_AVG_BAL_CUR,SUM(n_alloc_capital),SUM(N_LIAB_FEE),SUM(N_INT_INCOME),SUM(N_FTP),    "
                + " SUM(n_nii),SUM(N_CREDIT_FEE),SUM(N_NON_CREDIT_FEE),SUM(n_opex),SUM(n_el),SUM(n_pbt),SUM(n_pat),SUM(n_rwa_rs)+" + ln_protion + ",CASE WHEN SUM(n_alloc_capital) = 0 THEN null ELSE  trunc(SUM(n_pat)/SUM(n_alloc_capital)*100,2) END from tab)";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{ref});
        int i = 0, cols = 32;
        String[][] resultArray = new String[26][cols];
        String[] outputName = new String[cols];
        String[] creditTotal = new String[cols];
        String[] caTotal = new String[cols];
        String[] saTotal = new String[cols];
        String[] tdTotal = new String[cols];
        String[] cmsTotal = new String[cols];
        String[] fxTotal = new String[cols];
        String[] othTotal = new String[cols];
        String[] total = new String[cols];
        outputName[0] = "Facility Description";
        outputName[1] = "Facility Type";
        outputName[2] = "Date of Disbursement";
        outputName[3] = "External Rating (Borrower/Guarantor)";
        outputName[4] = "% Exposure Guranteed";
        outputName[5] = "PSL/Non PSL";
        outputName[6] = "RWA Intensity(%)";
        outputName[7] = "Currency";
        outputName[8] = "Contractual Maturity(Months)";
        outputName[9] = "Average Maturity(Months)";
        outputName[10] = "Average Utilization (%)";
        outputName[11] = "Interest Type";
        outputName[12] = "Interest rate Benchmark";
        outputName[13] = "Interest Rate(%)";
        outputName[14] = "Net FTP (%)";
        outputName[15] = "NIM (%)";
        outputName[16] = "STD EAD (Rs Lakh)";
        outputName[17] = "IRB EAD (Rs Lakh)";
        outputName[18] = "Avg O/S/ Bal (Rs Lakh)";
        outputName[19] = "Capital ( Rs Lakh)";
        outputName[20] = "Liability CDAB (Rs Lakh)";
        outputName[21] = "Interest Income (Rs Lakh)";
        outputName[22] = "(-) FTP (Rs Lakh)";
        outputName[23] = "NII (Rs Lakh)";
        outputName[24] = "(+)Credit Fee/Commision (Rs Lakh)";
        outputName[25] = "(+)Non-credit Fees (Rs Lakh)";
        outputName[26] = "(-)Opex (Rs Lakh)";
        outputName[27] = "(-)EL (Rs Lakh)";
        outputName[28] = "PBT (Rs Lakh)";
        outputName[29] = "PAT (Rs Lakh)";
        outputName[30] = "RWA (Rs Lakh)";
        outputName[31] = "RAROC (%)";
        for (Map row : rows) {
            if (row.get("n_facility_no") == null) {
                total[0] = fmt.ToString(row.get("v_fac_desc"));
                total[1] = fmt.ToString(row.get("v_fac_type"));
                total[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                total[3] = fmt.ToString(row.get("V_LONG_EXT"));
                total[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                total[5] = fmt.ToString(row.get("V_PSL"));
                total[6] = fmt.ToString(row.get("n_rw"));
                total[7] = fmt.ToString(row.get("v_curr"));
                total[8] = fmt.ToString(row.get("n_original_maturity"));
                total[9] = fmt.ToString(row.get("n_tenure"));
                total[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                total[11] = fmt.ToString(row.get("v_int_type"));
                total[12] = fmt.ToString(row.get("v_benchmark"));
                total[13] = fmt.ToString(row.get("n_int_rate_comm"));
                total[14] = fmt.ToString(row.get("n_cost_funds"));
                total[15] = fmt.ToString(row.get("n_nim"));
                total[16] = fmt.ToString(row.get("n_ead_rs"));
                total[17] = fmt.ToString(row.get("N_IRB_EAD"));
                total[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                total[19] = fmt.ToString(row.get("n_alloc_capital"));
                total[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                total[21] = fmt.ToString(row.get("N_INT_INCOME"));
                total[22] = fmt.ToString(row.get("N_FTP"));
                total[23] = fmt.ToString(row.get("n_nii"));
                total[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                total[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                total[26] = fmt.ToString(row.get("n_opex"));
                total[27] = fmt.ToString(row.get("n_el"));
                total[28] = fmt.ToString(row.get("n_pbt"));
                total[29] = fmt.ToString(row.get("n_pat"));//rwa_rs/ead_rs*100
                total[30] = fmt.ToString(row.get("n_rwa_rs"));
                total[31] = fmt.ToString(row.get("n_raroc"));

            } else if (fmt.ToInteger(row.get("n_facility_no")) == 100) {
                creditTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                creditTotal[1] = fmt.ToString(row.get("v_fac_type"));
                creditTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                creditTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                creditTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                creditTotal[5] = fmt.ToString(row.get("V_PSL"));
                creditTotal[6] = fmt.ToString(row.get("n_rw"));
                creditTotal[7] = fmt.ToString(row.get("v_curr"));
                creditTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                creditTotal[9] = fmt.ToString(row.get("n_tenure"));
                creditTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                creditTotal[11] = fmt.ToString(row.get("v_int_type"));
                creditTotal[12] = fmt.ToString(row.get("v_benchmark"));
                creditTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                creditTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                creditTotal[15] = fmt.ToString(row.get("n_nim"));
                creditTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                creditTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                creditTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                creditTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                creditTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                creditTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                creditTotal[22] = fmt.ToString(row.get("N_FTP"));
                creditTotal[23] = fmt.ToString(row.get("n_nii"));
                creditTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                creditTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                creditTotal[26] = fmt.ToString(row.get("n_opex"));
                creditTotal[27] = fmt.ToString(row.get("n_el"));
                creditTotal[28] = fmt.ToString(row.get("n_pbt"));
                creditTotal[29] = fmt.ToString(row.get("n_pat"));
                creditTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                creditTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 93) {
                caTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                caTotal[1] = fmt.ToString(row.get("v_fac_type"));
                caTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                caTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                caTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                caTotal[5] = fmt.ToString(row.get("V_PSL"));
                caTotal[6] = fmt.ToString(row.get("n_rw"));
                caTotal[7] = fmt.ToString(row.get("v_curr"));
                caTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                caTotal[9] = fmt.ToString(row.get("n_tenure"));
                caTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                caTotal[11] = fmt.ToString(row.get("v_int_type"));
                caTotal[12] = fmt.ToString(row.get("v_benchmark"));
                caTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                caTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                caTotal[15] = fmt.ToString(row.get("n_nim"));
                caTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                caTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                caTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                caTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                caTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                caTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                caTotal[22] = fmt.ToString(row.get("N_FTP"));
                caTotal[23] = fmt.ToString(row.get("n_nii"));
                caTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                caTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                caTotal[26] = fmt.ToString(row.get("n_opex"));
                caTotal[27] = fmt.ToString(row.get("n_el"));
                caTotal[28] = fmt.ToString(row.get("n_pbt"));
                caTotal[29] = fmt.ToString(row.get("n_pat"));
                caTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                caTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 94) {
                saTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                saTotal[1] = fmt.ToString(row.get("v_fac_type"));
                saTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                saTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                saTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                saTotal[5] = fmt.ToString(row.get("V_PSL"));
                saTotal[6] = fmt.ToString(row.get("n_rw"));
                saTotal[7] = fmt.ToString(row.get("v_curr"));
                saTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                saTotal[9] = fmt.ToString(row.get("n_tenure"));
                saTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                saTotal[11] = fmt.ToString(row.get("v_int_type"));
                saTotal[12] = fmt.ToString(row.get("v_benchmark"));
                saTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                saTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                saTotal[15] = fmt.ToString(row.get("n_nim"));
                saTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                saTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                saTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                saTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                saTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                saTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                saTotal[22] = fmt.ToString(row.get("N_FTP"));
                saTotal[23] = fmt.ToString(row.get("n_nii"));
                saTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                saTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                saTotal[26] = fmt.ToString(row.get("n_opex"));
                saTotal[27] = fmt.ToString(row.get("n_el"));
                saTotal[28] = fmt.ToString(row.get("n_pbt"));
                saTotal[29] = fmt.ToString(row.get("n_pat"));
                saTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                saTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 95) {
                tdTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                tdTotal[1] = fmt.ToString(row.get("v_fac_type"));
                tdTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                tdTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                tdTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                tdTotal[5] = fmt.ToString(row.get("V_PSL"));
                tdTotal[6] = fmt.ToString(row.get("n_rw"));
                tdTotal[7] = fmt.ToString(row.get("v_curr"));
                tdTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                tdTotal[9] = fmt.ToString(row.get("n_tenure"));
                tdTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                tdTotal[11] = fmt.ToString(row.get("v_int_type"));
                tdTotal[12] = fmt.ToString(row.get("v_benchmark"));
                tdTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                tdTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                tdTotal[15] = fmt.ToString(row.get("n_nim"));
                tdTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                tdTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                tdTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                tdTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                tdTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                tdTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                tdTotal[22] = fmt.ToString(row.get("N_FTP"));
                tdTotal[23] = fmt.ToString(row.get("n_nii"));
                tdTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                tdTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                tdTotal[26] = fmt.ToString(row.get("n_opex"));
                tdTotal[27] = fmt.ToString(row.get("n_el"));
                tdTotal[28] = fmt.ToString(row.get("n_pbt"));
                tdTotal[29] = fmt.ToString(row.get("n_pat"));
                tdTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                tdTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 96) {
                cmsTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                cmsTotal[1] = fmt.ToString(row.get("v_fac_type"));
                cmsTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                cmsTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                cmsTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                cmsTotal[5] = fmt.ToString(row.get("V_PSL"));
                cmsTotal[6] = fmt.ToString(row.get("n_rw"));
                cmsTotal[7] = fmt.ToString(row.get("v_curr"));
                cmsTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                cmsTotal[9] = fmt.ToString(row.get("n_tenure"));
                cmsTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                cmsTotal[11] = fmt.ToString(row.get("v_int_type"));
                cmsTotal[12] = fmt.ToString(row.get("v_benchmark"));
                cmsTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                cmsTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                cmsTotal[15] = fmt.ToString(row.get("n_nim"));
                cmsTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                cmsTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                cmsTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                cmsTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                cmsTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                cmsTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                cmsTotal[22] = fmt.ToString(row.get("N_FTP"));
                cmsTotal[23] = fmt.ToString(row.get("n_nii"));
                cmsTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                cmsTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                cmsTotal[26] = fmt.ToString(row.get("n_opex"));
                cmsTotal[27] = fmt.ToString(row.get("n_el"));
                cmsTotal[28] = fmt.ToString(row.get("n_pbt"));
                cmsTotal[29] = fmt.ToString(row.get("n_pat"));
                cmsTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                cmsTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 97) {
                fxTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                fxTotal[1] = fmt.ToString(row.get("v_fac_type"));
                fxTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                fxTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                fxTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                fxTotal[5] = fmt.ToString(row.get("V_PSL"));
                fxTotal[6] = fmt.ToString(row.get("n_rw"));
                fxTotal[7] = fmt.ToString(row.get("v_curr"));
                fxTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                fxTotal[9] = fmt.ToString(row.get("n_tenure"));
                fxTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                fxTotal[11] = fmt.ToString(row.get("v_int_type"));
                fxTotal[12] = fmt.ToString(row.get("v_benchmark"));
                fxTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                fxTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                fxTotal[15] = fmt.ToString(row.get("n_nim"));
                fxTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                fxTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                fxTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                fxTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                fxTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                fxTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                fxTotal[22] = fmt.ToString(row.get("N_FTP"));
                fxTotal[23] = fmt.ToString(row.get("n_nii"));
                fxTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                fxTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                fxTotal[26] = fmt.ToString(row.get("n_opex"));
                fxTotal[27] = fmt.ToString(row.get("n_el"));
                fxTotal[28] = fmt.ToString(row.get("n_pbt"));
                fxTotal[29] = fmt.ToString(row.get("n_pat"));
                fxTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                fxTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else if (fmt.ToInteger(row.get("n_facility_no")) == 98) {
                othTotal[0] = fmt.ToString(row.get("v_fac_desc"));
                othTotal[1] = fmt.ToString(row.get("v_fac_type"));
                othTotal[2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                othTotal[3] = fmt.ToString(row.get("V_LONG_EXT"));
                othTotal[4] = fmt.ToString(row.get("N_EXP_GUAR"));
                othTotal[5] = fmt.ToString(row.get("V_PSL"));
                othTotal[6] = fmt.ToString(row.get("n_rw"));
                othTotal[7] = fmt.ToString(row.get("v_curr"));
                othTotal[8] = fmt.ToString(row.get("n_original_maturity"));
                othTotal[9] = fmt.ToString(row.get("n_tenure"));
                othTotal[10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                othTotal[11] = fmt.ToString(row.get("v_int_type"));
                othTotal[12] = fmt.ToString(row.get("v_benchmark"));
                othTotal[13] = fmt.ToString(row.get("n_int_rate_comm"));
                othTotal[14] = fmt.ToString(row.get("n_cost_funds"));
                othTotal[15] = fmt.ToString(row.get("n_nim"));
                othTotal[16] = fmt.ToString(row.get("n_ead_rs"));
                othTotal[17] = fmt.ToString(row.get("N_IRB_EAD"));
                othTotal[18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                othTotal[19] = fmt.ToString(row.get("n_alloc_capital"));
                othTotal[20] = fmt.ToString(row.get("N_LIAB_FEE"));
                othTotal[21] = fmt.ToString(row.get("N_INT_INCOME"));
                othTotal[22] = fmt.ToString(row.get("N_FTP"));
                othTotal[23] = fmt.ToString(row.get("n_nii"));
                othTotal[24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                othTotal[25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                othTotal[26] = fmt.ToString(row.get("n_opex"));
                othTotal[27] = fmt.ToString(row.get("n_el"));
                othTotal[28] = fmt.ToString(row.get("n_pbt"));
                othTotal[29] = fmt.ToString(row.get("n_pat"));
                othTotal[30] = fmt.ToString(row.get("n_rwa_rs"));
                othTotal[31] = fmt.ToString(row.get("n_raroc"));
            } else {
                resultArray[i][0] = fmt.ToString(row.get("v_fac_desc"));
                resultArray[i][1] = fmt.ToString(row.get("v_fac_type"));
                resultArray[i][2] = fmt.ToString(row.get("D_ORIGIN_DATE"));
                resultArray[i][3] = fmt.ToString(row.get("V_LONG_EXT"));
                resultArray[i][4] = fmt.ToString(row.get("N_EXP_GUAR"));
                resultArray[i][5] = fmt.ToString(row.get("V_PSL"));
                resultArray[i][6] = fmt.ToString(row.get("n_rw"));
                resultArray[i][7] = fmt.ToString(row.get("v_curr"));
                resultArray[i][8] = fmt.ToString(row.get("n_original_maturity"));
                resultArray[i][9] = fmt.ToString(row.get("n_tenure"));
                resultArray[i][10] = fmt.ToString(row.get("N_AVG_UTL_CUR"));
                resultArray[i][11] = fmt.ToString(row.get("v_int_type"));
                resultArray[i][12] = fmt.ToString(row.get("v_benchmark"));
                resultArray[i][13] = fmt.ToString(row.get("n_int_rate_comm"));
                resultArray[i][14] = fmt.ToString(row.get("n_cost_funds"));
                resultArray[i][15] = fmt.ToString(row.get("n_nim"));
                resultArray[i][16] = fmt.ToString(row.get("n_ead_rs"));
                resultArray[i][17] = fmt.ToString(row.get("N_IRB_EAD"));
                resultArray[i][18] = fmt.ToString(row.get("N_AVG_BAL_CUR"));
                resultArray[i][19] = fmt.ToString(row.get("n_alloc_capital"));
                resultArray[i][20] = fmt.ToString(row.get("N_LIAB_FEE"));
                resultArray[i][21] = fmt.ToString(row.get("N_INT_INCOME"));
                resultArray[i][22] = fmt.ToString(row.get("N_FTP"));
                resultArray[i][23] = fmt.ToString(row.get("n_nii"));
                resultArray[i][24] = fmt.ToString(row.get("N_CREDIT_FEE"));
                resultArray[i][25] = fmt.ToString(row.get("N_NON_CREDIT_FEE"));
                resultArray[i][26] = fmt.ToString(row.get("n_opex"));
                resultArray[i][27] = fmt.ToString(row.get("n_el"));
                resultArray[i][28] = fmt.ToString(row.get("n_pbt"));
                resultArray[i][29] = fmt.ToString(row.get("n_pat"));
                resultArray[i][30] = fmt.ToString(row.get("n_rwa_rs"));
                resultArray[i][31] = fmt.ToString(row.get("n_raroc"));
                i++;
            }
        }
        i = 0;
        while (i < cols) {
            if (i == cols - 1) {
                footer.setId(i + 1);
                footer.setOutputName(outputName[i]);
                footer.setFacility1(resultArray[0][i]);
                footer.setFacility2(resultArray[1][i]);
                footer.setFacility3(resultArray[2][i]);
                footer.setFacility4(resultArray[3][i]);
                footer.setFacility5(resultArray[4][i]);
                footer.setFacility6(resultArray[5][i]);
                footer.setFacility7(resultArray[6][i]);
                footer.setFacility8(resultArray[7][i]);
                footer.setFacility9(resultArray[8][i]);
                footer.setFacility10(resultArray[9][i]);
                footer.setFacility11(resultArray[10][i]);
                footer.setFacility12(resultArray[11][i]);
                footer.setFacility13(resultArray[12][i]);
                footer.setFacility14(resultArray[13][i]);
                footer.setFacility15(resultArray[14][i]);
                footer.setFacility16(resultArray[15][i]);
                footer.setFacility17(resultArray[16][i]);
                footer.setFacility18(resultArray[17][i]);
                footer.setFacility19(resultArray[18][i]);
                footer.setFacility20(resultArray[19][i]);
                footer.setFacility21(resultArray[20][i]);
                footer.setFacility22(resultArray[21][i]);
                footer.setFacility23(resultArray[22][i]);
                footer.setFacility24(resultArray[23][i]);
                footer.setFacility25(resultArray[24][i]);

                footer.setCa(caTotal[i]);
                footer.setSa(saTotal[i]);
                footer.setTd(tdTotal[i]);
                footer.setCms(cmsTotal[i]);
                footer.setFx(fxTotal[i]);
                footer.setOther(othTotal[i]);
                footer.setTotal(creditTotal[i]);
            } else {
                RarocViewModel obj = new RarocViewModel();
                obj.setId(i + 1);
                obj.setOutputName(outputName[i]);
                obj.setFacility1(resultArray[0][i]);
                obj.setFacility2(resultArray[1][i]);
                obj.setFacility3(resultArray[2][i]);
                obj.setFacility4(resultArray[3][i]);
                obj.setFacility5(resultArray[4][i]);
                obj.setFacility6(resultArray[5][i]);
                obj.setFacility7(resultArray[6][i]);
                obj.setFacility8(resultArray[7][i]);
                obj.setFacility9(resultArray[8][i]);
                obj.setFacility10(resultArray[9][i]);
                obj.setFacility11(resultArray[10][i]);
                obj.setFacility12(resultArray[11][i]);
                obj.setFacility13(resultArray[12][i]);
                obj.setFacility14(resultArray[13][i]);
                obj.setFacility15(resultArray[14][i]);
                obj.setFacility16(resultArray[15][i]);
                obj.setFacility17(resultArray[16][i]);
                obj.setFacility18(resultArray[17][i]);
                obj.setFacility19(resultArray[18][i]);
                obj.setFacility20(resultArray[19][i]);
                obj.setFacility21(resultArray[20][i]);
                obj.setFacility22(resultArray[21][i]);
                obj.setFacility23(resultArray[22][i]);
                obj.setFacility24(resultArray[23][i]);
                obj.setFacility25(resultArray[24][i]);
                //obj.setCreditRaroc(creditTotal[i]);
                //obj.setTotal(total[i]);
                obj.setCa(caTotal[i]);
                obj.setSa(saTotal[i]);
                obj.setTd(tdTotal[i]);
                obj.setCms(cmsTotal[i]);
                obj.setFx(fxTotal[i]);
                obj.setOther(othTotal[i]);
                obj.setTotal(creditTotal[i]);
                facs.add(obj);
            }
            i++;
        }
        return new GridWithFooterPage<>(facs, footer, 1, 30, 30);
    }

    @Override
    public List<OptionsModel> listSubHeader(String id) {
        String query = "SELECT v_fac_type, count(*) cnt from INPT_RAROC Where v_rec_ref_no = ? group by v_fac_type";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(query, new Object[]{id});
        List<OptionsModel> lObj = new ArrayList<>();
        OptionsModel obj = new OptionsModel();
        for (Map row : rows) {
            obj = new OptionsModel();
            obj.setKey(fmt.ToString(row.get("v_fac_type")));
            obj.setValue(fmt.ToString(row.get("cnt")));
            lObj.add(obj);
        }
        return lObj;
    }

}
