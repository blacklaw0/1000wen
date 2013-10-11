package com.blacklaw.netcaller;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author blacklaw
 */
@XStreamAlias("request")
public class CallerRequest{
    public String name = "";
    public String action = "";
    public String type = "";
    public String args = "";
}

