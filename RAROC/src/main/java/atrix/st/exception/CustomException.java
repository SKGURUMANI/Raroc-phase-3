/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.exception;

import org.apache.log4j.Logger;

/**
 *
 * @author Amolraj
 */
public class CustomException extends Exception {

    private static final Logger logger = Logger.getLogger(CustomException.class);

    public CustomException() {
        super("Error Msg: Invalid URL");
        logger.error("Error Message: Invalid Sort Column or Invalid Sort column");
    }

    public CustomException(String inpt) {
        super(inpt);
        logger.error("Error Message: Invalid Sort Column or Invalid Sort column");
    }
    
    public CustomException(String code, String msg) {
        super("Error Msg: Invalid Parameters");
        logger.error("Error Message: Invalid Parameter value");
    }
}
