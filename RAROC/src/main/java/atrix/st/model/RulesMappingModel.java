package atrix.st.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RulesMappingModel {

    private String id;
    private String source1;
    private String source2;
    private String source3;
    private String source4;
    private String source5;
    private String destination;
    private String fxId;
    private String oper;
    private String cCode;
    private String cDesc;
    private String approach;
    private String stressRw;
    private String pd;
    private String lgd;
    private String em;

    public RulesMappingModel() {
    }

    public RulesMappingModel(String id, String source1, String source2, String source3, String source4, String source5,
            String destination, String fxId, String approach, String stressRw, String pd, String lgd, String em) {
        this.id = id;
        this.source1 = source1;
        this.source2 = source2;
        this.source3 = source3;
        this.source4 = source4;
        this.source5 = source5;
        this.destination = destination;
        this.fxId = fxId;
        this.approach = approach;
        this.stressRw = stressRw;
        this.pd = pd;
        this.lgd = lgd;
        this.em = em;
    }

    public String getcCode() {
        return cCode;
    }

    public void setcCode(String cCode) {
        this.cCode = cCode;
    }

    public String getcDesc() {
        return cDesc;
    }

    public void setcDesc(String cDesc) {
        this.cDesc = cDesc;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFxId() {
        return fxId;
    }

    public void setFxId(String fxId) {
        this.fxId = fxId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getSource1() {
        return source1;
    }

    public void setSource1(String source1) {
        this.source1 = source1;
    }

    public String getSource2() {
        return source2;
    }

    public void setSource2(String source2) {
        this.source2 = source2;
    }

    public String getSource3() {
        return source3;
    }

    public void setSource3(String source3) {
        this.source3 = source3;
    }

    public String getSource4() {
        return source4;
    }

    public void setSource4(String source4) {
        this.source4 = source4;
    }

    public String getSource5() {
        return source5;
    }

    public void setSource5(String source5) {
        this.source5 = source5;
    }

    public String getApproach() {
        return approach;
    }

    public void setApproach(String approach) {
        this.approach = approach;
    }

    public String getStressRw() {
        return stressRw;
    }

    public void setStressRw(String stressRw) {
        this.stressRw = stressRw;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public String getLgd() {
        return lgd;
    }

    public void setLgd(String lgd) {
        this.lgd = lgd;
    }

    public String getPd() {
        return pd;
    }

    public void setPd(String pd) {
        this.pd = pd;
    }    
}