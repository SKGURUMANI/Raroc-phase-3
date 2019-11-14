/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package atrix.st.model;

/**
 *
 * @author vinoy
 */
public class RarocFacilityModel {
        
    private String facNo;
    private String facType;
    private String facDesc;
    private String derType;
    private String astType;
    private String tRating;
    private String cur;
    private String amount;
    private String tenure;
    private String exgRate;
    private String restStatus;
    private String ucicf;
    private String avgUtil;
    private String inttype;
    private String intRate;
    private String costFunds;
    private String uFee;
    private String aFee;
    private String cMargin;
    private String cMarginCurr;
    private String cSecured;
    private String ltRating;
    private String stRating;
    private String eGuar;
    private String guarType;
    private String guarIntRat;
    private String guarExtRat;
    private String benchmark;
    private String psl;
    private String region;

    public RarocFacilityModel() {
        
    }

    public RarocFacilityModel(String facNo, String facType, String facDesc, String derType, String astType, 
            String tRating, String cur, String amount, String tenure, String exgRate, String restStatus, String ucicf, 
            String avgUtil, String inttype, String intRate, String costFunds, String uFee, String aFee, String cMargin, 
            String cMarginCurr, String cSecured, String ltRating, String stRating, String eGuar, String guarType, 
            String guarIntRat, String guarExtRat, String benchmark, String psl, String region) {
        this.facNo = facNo;
        this.facType = facType;
        this.facDesc = facDesc;
        this.derType = derType;
        this.astType = astType;
        this.tRating = tRating;
        this.cur = cur;
        this.amount = amount;
        this.tenure = tenure;
        this.exgRate = exgRate;
        this.restStatus = restStatus;
        this.ucicf = ucicf;
        this.avgUtil = avgUtil;
        this.inttype = inttype;
        this.intRate = intRate;
        this.costFunds = costFunds;
        this.uFee = uFee;
        this.aFee = aFee;
        this.cMargin = cMargin;
        this.cMarginCurr = cMarginCurr;
        this.cSecured = cSecured;
        this.ltRating = ltRating;
        this.stRating = stRating;
        this.eGuar = eGuar;
        this.guarType = guarType;
        this.guarIntRat = guarIntRat;
        this.guarExtRat = guarExtRat;
        this.benchmark = benchmark;
        this.psl = psl;
        this.region = region;
    }        

    public String getFacNo() {
        return facNo;
    }

    public void setFacNo(String facNo) {
        this.facNo = facNo;
    }        

    public String getFacType() {
        return facType;
    }

    public void setFacType(String facType) {
        this.facType = facType;
    }

    public String getFacDesc() {
        return facDesc;
    }

    public void setFacDesc(String facDesc) {
        this.facDesc = facDesc;
    }

    public String getDerType() {
        return derType;
    }

    public void setDerType(String derType) {
        this.derType = derType;
    }        

    public String getAstType() {
        return astType;
    }

    public void setAstType(String astType) {
        this.astType = astType;
    }

    public String gettRating() {
        return tRating;
    }

    public void settRating(String tRating) {
        this.tRating = tRating;
    }        

    public String getCur() {
        return cur;
    }

    public void setCur(String cur) {
        this.cur = cur;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public String getExgRate() {
        return exgRate;
    }

    public void setExgRate(String exgRate) {
        this.exgRate = exgRate;
    }

    public String getRestStatus() {
        return restStatus;
    }

    public void setRestStatus(String restStatus) {
        this.restStatus = restStatus;
    }

    public String getUcicf() {
        return ucicf;
    }

    public void setUcicf(String ucicf) {
        this.ucicf = ucicf;
    }

    public String getAvgUtil() {
        return avgUtil;
    }

    public void setAvgUtil(String avgUtil) {
        this.avgUtil = avgUtil;
    }

    public String getInttype() {
        return inttype;
    }

    public void setInttype(String inttype) {
        this.inttype = inttype;
    }

    public String getIntRate() {
        return intRate;
    }

    public void setIntRate(String intRate) {
        this.intRate = intRate;
    }

    public String getCostFunds() {
        return costFunds;
    }

    public void setCostFunds(String costFunds) {
        this.costFunds = costFunds;
    }

    public String getuFee() {
        return uFee;
    }

    public void setuFee(String uFee) {
        this.uFee = uFee;
    }

    public String getaFee() {
        return aFee;
    }

    public void setaFee(String aFee) {
        this.aFee = aFee;
    }

    public String getcMargin() {
        return cMargin;
    }

    public void setcMargin(String cMargin) {
        this.cMargin = cMargin;
    }

    public String getcMarginCurr() {
        return cMarginCurr;
    }

    public void setcMarginCurr(String cMarginCurr) {
        this.cMarginCurr = cMarginCurr;
    }

    public String getcSecured() {
        return cSecured;
    }

    public void setcSecured(String cSecured) {
        this.cSecured = cSecured;
    }        

    public String getLtRating() {
        return ltRating;
    }

    public void setLtRating(String ltRating) {
        this.ltRating = ltRating;
    }

    public String getStRating() {
        return stRating;
    }

    public void setStRating(String stRating) {
        this.stRating = stRating;
    }

    public String geteGuar() {
        return eGuar;
    }

    public void seteGuar(String eGuar) {
        this.eGuar = eGuar;
    }

    public String getGuarType() {
        return guarType;
    }

    public void setGuarType(String guarType) {
        this.guarType = guarType;
    }

    public String getGuarIntRat() {
        return guarIntRat;
    }

    public void setGuarIntRat(String guarIntRat) {
        this.guarIntRat = guarIntRat;
    }

    public String getGuarExtRat() {
        return guarExtRat;
    }

    public void setGuarExtRat(String guarExtRat) {
        this.guarExtRat = guarExtRat;
    }

    public String getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(String benchmark) {
        this.benchmark = benchmark;
    }

    public String getPsl() {
        return psl;
    }

    public void setPsl(String psl) {
        this.psl = psl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }        
        
}