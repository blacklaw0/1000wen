/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blacklaw.netcaller;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author blacklaw
 */

@XStreamAlias("properties")
public class NetCallerProperties {
    
    public String name = "";
    public String base = "";
    public List requests = new ArrayList<CallerRequest>();
 
}

