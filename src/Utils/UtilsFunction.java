/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author hung.tran
 */
public class UtilsFunction {
    public static String getIP() {
        try {
            InetAddress ipAddr = InetAddress.getLocalHost();
            return ipAddr.getHostAddress() + "";

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
