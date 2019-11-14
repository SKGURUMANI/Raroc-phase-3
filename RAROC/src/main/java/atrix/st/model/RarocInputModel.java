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
package atrix.st.model;

/**
 *
 * @author vaio
 */
public class RarocInputModel {

    private String refrec;
    private Integer unit;
    private String bbcdab;
    private String tdfee;
    private String bbfee;
    private String trsry;
    private String other;
//    private List <RarocFacilityModel> facilities;

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
    private String id;
    private String cashMis;
    private String currMis;
    private String maturity;
    private String curfy;
    private String nextfy;
    private String avgfy;
    private String avgnextfy;
    private String oridate;
    private String refreq;
    private String synFee;
    private String extRated;
    private String giftCity;
    private String avgMat;
    private String commis;
    private String bookType;
    private String rTenure;
    private String yeild;
    private String coupon;
    private String cetExt;
    private String lcr;
    private String upfrontFee;
    private String expIncome;
    private String forex;
    private String cms;
    private String cafee;
    private String bondTenure;
    private String tentative;
    private String couponBond;

    public String getBondTenure() {
        return bondTenure;
    }

    public void setBondTenure(String bondTenure) {
        this.bondTenure = bondTenure;
    }

    public String getTentative() {
        return tentative;
    }

    public void setTentative(String tentative) {
        this.tentative = tentative;
    }

    public String getCouponBond() {
        return couponBond;
    }

    public void setCouponBond(String couponBond) {
        this.couponBond = couponBond;
    }
    
    

    public String getForex() {
        return forex;
    }

    public void setForex(String forex) {
        this.forex = forex;
    }

    public String getCms() {
        return cms;
    }

    public void setCms(String cms) {
        this.cms = cms;
    }

    public String getCafee() {
        return cafee;
    }

    public void setCafee(String cafee) {
        this.cafee = cafee;
    }
    
    

    public String getCetExt() {
        return cetExt;
    }

    public void setCetExt(String cetExt) {
        this.cetExt = cetExt;
    }

    public String getLcr() {
        return lcr;
    }

    public void setLcr(String lcr) {
        this.lcr = lcr;
    }

    public String getUpfrontFee() {
        return upfrontFee;
    }

    public void setUpfrontFee(String upfrontFee) {
        this.upfrontFee = upfrontFee;
    }

    public String getExpIncome() {
        return expIncome;
    }

    public void setExpIncome(String expIncome) {
        this.expIncome = expIncome;
    }
    
    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getrTenure() {
        return rTenure;
    }

    public void setrTenure(String rTenure) {
        this.rTenure = rTenure;
    }

    public String getYeild() {
        return yeild;
    }

    public void setYeild(String yeild) {
        this.yeild = yeild;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getCommis() {
        return commis;
    }

    public void setCommis(String commis) {
        this.commis = commis;
    }

    public String getAvgMat() {
        return avgMat;
    }

    public void setAvgMat(String avgMat) {
        this.avgMat = avgMat;
    }

    public String getExtRated() {
        return extRated;
    }

    public void setExtRated(String extRated) {
        this.extRated = extRated;
    }

    public String getGiftCity() {
        return giftCity;
    }

    public void setGiftCity(String giftCity) {
        this.giftCity = giftCity;
    }

    public String getSynFee() {
        return synFee;
    }

    public void setSynFee(String synFee) {
        this.synFee = synFee;
    }

    public String getRefreq() {
        return refreq;
    }

    public void setRefreq(String refreq) {
        this.refreq = refreq;
    }

    public String getOridate() {
        return oridate;
    }

    public void setOridate(String oridate) {
        this.oridate = oridate;
    }

    public String getCurfy() {
        return curfy;
    }

    public void setCurfy(String curfy) {
        this.curfy = curfy;
    }

    public String getNextfy() {
        return nextfy;
    }

    public void setNextfy(String nextfy) {
        this.nextfy = nextfy;
    }

    public String getAvgfy() {
        return avgfy;
    }

    public void setAvgfy(String avgfy) {
        this.avgfy = avgfy;
    }

    public String getAvgnextfy() {
        return avgnextfy;
    }

    public void setAvgnextfy(String avgnextfy) {
        this.avgnextfy = avgnextfy;
    }

    public String getMaturity() {
        return maturity;
    }

    public void setMaturity(String maturity) {
        this.maturity = maturity;
    }

    public String getCashMis() {
        return cashMis;
    }

    public void setCashMis(String cashMis) {
        this.cashMis = cashMis;
    }

    public String getCurrMis() {
        return currMis;
    }

    public void setCurrMis(String currMis) {
        this.currMis = currMis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getRefrec() {
        return refrec;
    }

    public void setRefrec(String refrec) {
        this.refrec = refrec;
    }

    public Integer getUnit() {
        return unit;
    }

    public void setUnit(Integer unit) {
        this.unit = unit;
    }

    public String getBbcdab() {
        return bbcdab;
    }

    public void setBbcdab(String bbcdab) {
        this.bbcdab = bbcdab;
    }

    public String getTdfee() {
        return tdfee;
    }

    public void setTdfee(String tdfee) {
        this.tdfee = tdfee;
    }

    public String getBbfee() {
        return bbfee;
    }

    public void setBbfee(String bbfee) {
        this.bbfee = bbfee;
    }

    public String getTrsry() {
        return trsry;
    }

    public void setTrsry(String trsry) {
        this.trsry = trsry;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

}
