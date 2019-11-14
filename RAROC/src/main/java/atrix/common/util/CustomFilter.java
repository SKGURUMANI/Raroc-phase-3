/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author Amolraj
 */
public class CustomFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Pattern pattern;
        Matcher matcher;
        String search_pattern = "<(\"[^\"]*\"|'[^']*'|[^'\">])*>";
        pattern = Pattern.compile(search_pattern);

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
            matcher = pattern.matcher(txtfield);
            if (matcher.find()) {
                System.out.println("Is HTML Text : " + txtfield);
                httpresponse.addHeader("CharMsg", "P");
                throw new ServletException("Internal Server Error Please Check Application logs");
                //return;
            }
        }

        httpresponse.setHeader("Server", "");
        chain.doFilter(new PasswordWrappedRequest((HttpServletRequest)request), response);
    }

   
}
