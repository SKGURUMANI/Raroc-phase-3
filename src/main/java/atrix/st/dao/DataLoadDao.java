/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.dao;

import atrix.common.util.GridPage;
import atrix.st.model.DataLoadModel;
import atrix.st.model.DataQualityModel;

/**
 *
 * @author vaio
 */
public interface DataLoadDao {
    
    public GridPage<DataLoadModel> listTransformations(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public void deleteTrans(String pk);
    
    public void addTrans(DataLoadModel model);
    
    public GridPage<DataLoadModel> listTransformationLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
    
    public GridPage<DataQualityModel> listDqLog(int page, int max, String sidx, String sord, String searchField,
            String searchOper, String searchString);
}
