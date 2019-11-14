/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.service;

import atrix.common.model.FileModel;
import atrix.common.model.OptionsModel;
import atrix.common.util.GridPage;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author vaio
 */
public interface JobExecutorService {

    public String callKettleTransformation(String tname, String ttype, HttpServletRequest request);

    public List<OptionsModel> listJobs(String type);

    public void saveFile(FileModel fm, HttpServletRequest request);

    public GridPage<FileModel> listFiles(int page, int max, String sidx, String sord, String searchString,
            String directory);

    public void DeleteFile(String filename, HttpServletRequest request);
    
    public void DownloadFile(String filename, HttpServletResponse response);   
}