/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author vaio
 */
@Controller
public class NavigationController {

    @RequestMapping(value = "/operations")
    public String operations() {
        return "operations/operationsMain";
    }

    @RequestMapping(value = "/quality")
    public String quaity() {
        return "dataQuality/dqMain";
    }

    @RequestMapping(value = "/recon")
    public String recon(Map<String, Object> model) {
        model.put("breadcrumbMessage", "Reports >> Data Integrity >> GL Reconciliation");
        return "reports/dq/glRecon";
    }

    @RequestMapping(value = "/qualityBusiness")
    public String qualityBusiness(Map<String, Object> model) {
        model.put("breadcrumbMessage", "Reports >> Data Integrity >> Qualitative Checks");
        return "reports/dq/qualityBusiness";
    }
    
    @RequestMapping(value = "/baseliii")
    public String baseliii(Map<String, Object> model) {
        model.put("breadcrumbMessage", "Reports >> Basel iii");
        return "reports/others/cecar";
    }
    
    @RequestMapping(value = "reports/baseliiiList")
    public String reconList() {
        return "reports/others/baseliii";
    }
    
}