/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package atrix.st.model;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vaio
 */
@XmlRootElement
public class TreeModel<T> {
 
    private List<T> nodes;
    private String state;

    public TreeModel() {        
    }
    
    public TreeModel(List<T> nodes, String state) {
        this.nodes = nodes;
        this.state = state;
    }

    public List<T> getNodes() {
        return nodes;
    }

    public void setNodes(List<T> nodes) {
        this.nodes = nodes;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }        
    
}