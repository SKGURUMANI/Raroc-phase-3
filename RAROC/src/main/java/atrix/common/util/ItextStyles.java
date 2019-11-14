/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.common.util;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import java.awt.Color;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author vaio
 */
public class ItextStyles {
    
    public Color HeadingColor() {
        return Color.lightGray;
    }
    
    public Image TrafficLightGreen(HttpServletRequest request) throws IOException, BadElementException {
        String path = request.getServletContext().getRealPath("/")+"resources/css/images/";
        Image signal = Image.getInstance(path+"pdf_green.png");
        signal.scalePercent(30f);
        return signal;
    }
    
    public Image TrafficLightOrange(HttpServletRequest request) throws IOException, BadElementException {
        String path = request.getServletContext().getRealPath("/")+"resources/css/images/";
        Image signal = Image.getInstance(path+"pdf_orange.png");
        signal.scalePercent(30f);
        return signal;
    }
    
    public Image TrafficLightRed(HttpServletRequest request) throws IOException, BadElementException {
        String path = request.getServletContext().getRealPath("/")+"resources/css/images/";
        Image signal = Image.getInstance(path+"pdf_red.png");
        signal.scalePercent(30f);
        return signal;
    }
    
    public Image TrafficLightGrey(HttpServletRequest request) throws IOException, BadElementException {
        String path = request.getServletContext().getRealPath("/")+"resources/css/images/";
        Image signal = Image.getInstance(path+"pdf_grey.png");
        signal.scalePercent(30f);
        return signal;
    }
    
}