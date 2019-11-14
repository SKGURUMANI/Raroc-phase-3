package atrix.common.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.HtmlUtils;

public class CustomHtmlFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httprequest = (HttpServletRequest) request;
        HttpServletResponse httpresponse = (HttpServletResponse) response;
        if (httprequest.getQueryString() != null) {
            System.out.println("Query STring : " + ((HttpServletRequest) request).getQueryString());
        }
        System.out.println("HTTP Method: " + httprequest.getMethod());
        Enumeration<String> paramnames = httprequest.getParameterNames();
        int i = 0;
        while (paramnames.hasMoreElements()) {
            String paramname = paramnames.nextElement();
            System.out.println("Param[" + ++i + "] : " + paramname + " = " + Arrays.toString(httprequest.getParameterValues(paramname)));
            String txtfield = httprequest.getParameterValues(paramname)[0];
            if (txtfield != null) {
                if (!txtfield.equals(HtmlUtils.htmlEscape(txtfield))) {
                    System.out.println("Is HTML Text : " + HtmlUtils.htmlEscape(txtfield));
                    return;
                }
            }
        }
        httpresponse.setHeader("Server", "");
        chain.doFilter(request, response);
    }
}
