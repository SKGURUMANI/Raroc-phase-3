/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.service;

import atrix.common.dao.SecurityDao;
import atrix.common.model.FileModel;
import atrix.common.model.OptionsModel;
import atrix.common.util.GridPage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author vaio
 */
@Service("jobService")
public class JobExecutorServiceImpl implements JobExecutorService {

    private @Value("${kettle}")
    String kettle;
    private @Value("${xml}")
    String xml;
    private @Value("${dump}")
    String dump;
    private @Value("${log}")
    String log;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SecurityDao securityDao;
    private static final Logger logger = Logger.getLogger(JobExecutorServiceImpl.class);

    @Override
    public String callKettleTransformation(String tname, String ttype, HttpServletRequest request) {
        String process;
        HttpSession session = request.getSession(false);
        if (ttype.equals("KTR")) {
            process = "\"" + kettle + "\\Pan.bat\"" + " /file:\"" + xml + "\\" + tname + "\"";
        } else {
            process = "\"" + kettle + "\\Kitchen.bat\"" + " /file:\"" + xml + "\\" + tname + "\"";
        }
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec(process);
            String audseq = securityDao.getAuditSequence();
            securityDao.insertOperAudit(audseq, "Load job fired", (String)session.getAttribute("userid"), 
                "success", "Load job "+tname+" fired");
            return messageSource.getMessage("job.fire.success", new Object[]{tname}, "Successful", null);
        } catch (IOException ex) {
            logger.fatal(ex);
            return messageSource.getMessage("job.fire.failed",  new Object[]{tname}, "Failed", null);
        }
    }

    @Override
    public List<OptionsModel> listJobs(String type) {
        String filename;
        File folder = new File(xml);
        File[] listOfFiles = folder.listFiles();
        List<OptionsModel> ids = new ArrayList<OptionsModel>();
        for (int i = 0; i < listOfFiles.length; i++) {
            filename = listOfFiles[i].getName();
            if (filename.endsWith(type) || filename.endsWith(type.toLowerCase())) {
                OptionsModel obj = new OptionsModel();
                obj.setKey(filename);
                obj.setValue(filename);
                ids.add(obj);
            }
        }
        return ids;
    }

    @Override
    public void saveFile(FileModel fm, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            MultipartFile mf = fm.getFile();
            mf.transferTo(new File(dump + "\\" + mf.getOriginalFilename()));
            String audseq = securityDao.getAuditSequence();
            securityDao.insertOperAudit(audseq, "Save file", (String)session.getAttribute("userid"), 
                "success", "File "+mf.getOriginalFilename()+" saved to server");
        } catch (IOException ex) {
            logger.fatal(ex);
        }
    }

    @Override
    public GridPage<FileModel> listFiles(int page, int max, String sidx, String sord, String searchString,
    String directory) {
        if(directory.equals("dump")) {
            directory = dump;
        } else {
            directory = log;
        }
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        if(sidx == null || sidx.equals("") || sidx.equals("time")) {
            if(sord.equals("desc")) {
                Arrays.sort(listOfFiles, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        long l1 = f1.lastModified(), l2 = f2.lastModified();
                        if(l2 > l1) {
                            return 1;
                        } else if (l1 > l2) {
                            return -1;                        
                        } else {
                            return 0;
                        }
                    }
                });
            } else {
                Arrays.sort(listOfFiles, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        long l1 = f1.lastModified(), l2 = f2.lastModified();
                        if(l2 > l1) {
                            return -1;
                        } else if (l1 > l2) {
                            return 1;                        
                        } else {
                            return 0;
                        }
                    }
                });
            }            
        } else {
            if(sord.equals("desc")) {
                Arrays.sort(listOfFiles, Collections.reverseOrder());
            } else {
                Arrays.sort(listOfFiles);
            }
        }     
        int rowCount = listOfFiles.length;
        final int startIdx = (page - 1) * max;
        final int endIdx = Math.min(startIdx + max, rowCount);
        List<FileModel> files = new ArrayList<FileModel>();
        for (int i = 0; i < rowCount; i++) {            
            if(i >= startIdx && i < endIdx) {
                File file = new File(directory + "\\" + listOfFiles[i].getName());
                FileModel obj = new FileModel();
                obj.setId(i+1);
                obj.setFileName(file.getName());
                obj.setTime(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(
                    new Date(file.lastModified())));
                files.add(obj);
            }      
        }
        return new GridPage<FileModel>(files, page, max, rowCount);
    }

    @Override
    public void DeleteFile(String filename, HttpServletRequest request) {
        File file = new File(dump + "\\" + filename);
        if (file.exists()) {
            file.delete();
            HttpSession session = request.getSession(false);
            String audseq = securityDao.getAuditSequence();
            securityDao.insertOperAudit(audseq, "Delete file", (String)session.getAttribute("userid"), 
                "success", "File "+filename+" deleted from server");
        } else {
            System.err.println("File: " + dump + "\\" + filename + " does not exist");
            logger.fatal("File: " + dump + "\\" + filename + " does not exist");
        }
    }
    
    @Override
    public void DownloadFile(String filename, HttpServletResponse response) {
        File file = new File(log + "\\" + filename);
        FileInputStream iStream = null;
        OutputStream oStream = null;
        try {
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setHeader("Content-type", "txt");
            iStream = new FileInputStream(file);
            oStream = response.getOutputStream();
            IOUtils.copy(iStream,oStream);            
        } catch (IOException ex) {
            logger.fatal(ex);
        } finally {
            IOUtils.closeQuietly(iStream);
            IOUtils.closeQuietly(oStream);
        }
    }

}