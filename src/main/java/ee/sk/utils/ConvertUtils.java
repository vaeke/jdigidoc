/*
 * ConvertUtils.java
 * PROJECT: JDigiDoc
 * DESCRIPTION: Digi Doc functions for creating
 *	and reading signed documents. 
 * AUTHOR:  Veiko Sinivee, S|E|B IT Partner Estonia
 *==================================================
 * Copyright (C) AS Sertifitseerimiskeskus
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * GNU Lesser General Public Licence is available at
 * http://www.gnu.org/copyleft/lesser.html
 *==================================================
 */

package ee.sk.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.SignedDoc;


/**
 * Miscellaneous data conversion utility methods
 * @author  Veiko Sinivee
 * @version 1.0
 */
public class ConvertUtils {
    
    private static final String m_dateFormat = "yyyy.MM.dd'T'HH:mm:ss'Z'";
    private static final String m_dateFormatXAdES = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    /**
     * Helper method to convert a Date
     * object to xsd:date format
     * @param d input data
     * @param ddoc signed doc
     * @return stringified date (xsd:date)
     * @throws DigiDocException for errors
     */
    public static String date2string(Date d, SignedDoc ddoc) {
        String str = null;
        
        SimpleDateFormat f = new SimpleDateFormat(((ddoc.getVersion().equals(SignedDoc.VERSION_1_3) 
                  || ddoc.getVersion().equals(SignedDoc.VERSION_1_4) 
                  || ddoc.getFormat().equals(SignedDoc.FORMAT_BDOC)) ? m_dateFormatXAdES : m_dateFormat));
        
        f.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        str = f.format(d);
        return str;
    }
    
    
    /**
     * Helper method to convert a string
     * to a Date object from xsd:date format
     * @param str stringified date (xsd:date
     * @param ddoc signed doc
     * @return Date object
     * @throws DigiDocException for errors
     */
    public static Date string2date(String str, SignedDoc ddoc) throws DigiDocException {
        Date d = null;
        try {
            SimpleDateFormat f = new SimpleDateFormat(
            	((ddoc.getVersion().equals(SignedDoc.VERSION_1_3) ||
                  ddoc.getVersion().equals(SignedDoc.VERSION_1_4)) ||
                //IS FIX date format
                	ddoc.getFormat().equals(SignedDoc.FORMAT_BDOC)) ? m_dateFormatXAdES : m_dateFormat);
            f.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            if(str != null && str.length() > 0)
            	d = f.parse(str.trim());
        } catch(Exception ex) {
            DigiDocException.handleException(ex, DigiDocException.ERR_DATE_FORMAT);
        }
        return d;
    }

    /**
     * Helper method to convert a string
     * to a BigInteger object
     * @param str stringified date (xsd:date
     * @return BigInteger object
     * @throws DigiDocException for errors
     */
    public static BigInteger string2bigint(String str) throws DigiDocException {
        BigInteger b = null;
        try {
        	if(str != null && str.length() > 0) {
        	    b = new BigInteger(str.trim());
        	}
        } catch(Exception ex) {
            DigiDocException.handleException(ex, DigiDocException.ERR_NUMBER_FORMAT);
        }
        return b;
    }
   
    /**
     * Helper method to convert a String
     * to UTF-8
     * @param data input data
     * @param codepage codepage of input bytes
     * @return UTF-8 string
     * @throws DigiDocException for errors
     */
    public static byte[] data2utf8(byte[] data, String codepage) throws DigiDocException {
        byte[] bdata = null;
        try {
            String str = new String(data, codepage);
            bdata = str.getBytes("UTF-8");
        } catch(Exception ex) {
            DigiDocException.handleException(ex, DigiDocException.ERR_UTF8_CONVERT);
        }
        return bdata;
    }
    
    /**
     * Converts to UTF-8 byte array
     * @param str input data
     * @return byte array of string in desired codepage
     * @throws DigiDocException for errors
     */
    public static byte[] str2data(String str) {
        try {
            return str2data(str, "UTF-8");
        } catch (DigiDocException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to convert a String
     * to byte array of any codepage
     * @param data input data
     * @param codepage codepage of output bytes
     * @return byte array of string in desired codepage
     * @throws DigiDocException for errors
     */
    public static byte[] str2data(String str, String codepage) throws DigiDocException {
        byte[] bdata = null;
        try {
            bdata = str.getBytes(codepage);
        } catch(Exception ex) {
            DigiDocException.handleException(ex, DigiDocException.ERR_UTF8_CONVERT);
        }
        return bdata;
    }
    
    /**
     * Helper method to convert a String
     * to UTF-8
     * @param data input data
     * @param codepage codepage of input bytes
     * @return UTF-8 string
     * @throws DigiDocException for errors
     */
    public static String data2str(byte[] data, String codepage)
        throws DigiDocException
    {
        String str = null;
        try {
            str = new String(data, codepage);
        } catch(Exception ex) {
            DigiDocException.handleException(ex, DigiDocException.ERR_UTF8_CONVERT);
        }
        return str;
    }
    
    /**
     * Helper method to convert an UTF-8
     * String to non-utf8 string
     * @param UTF-8 input data
     * @return normal string
     * @throws DigiDocException for errors
     */
    public static String utf82str(String data)
        throws DigiDocException
    {
        String str = null;
        try {
            byte[] bdata = data.getBytes();
            str = new String(bdata, "UTF-8");
        } catch(Exception ex) {
            DigiDocException.handleException(ex, DigiDocException.ERR_UTF8_CONVERT);
        }
        return str;
    }
    

    /**
     * Checks if the certificate identified by this CN is
     * a known TSA cert
     * @param cn certificates common name
     * @return true if this is a known TSA cert
     */
    public static boolean isKnownTSACert(String cn)
    {
//    	int nTsas = ConfigManager.instance().getIntProperty("DIGIDOC_TSA_COUNT", 0);
//    	for(int i = 0; i < nTsas; i++) {
//    		String s = ConfigManager.instance().getProperty("DIGIDOC_TSA" + (i+1) + "_CN");
//    		if(s != null && s.equals(cn))
//    			return true;
//    	} // TODO: is TSA needed?
    	return false;
    }
    
    public static byte[] getBytesFromFile(File file ) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

}
