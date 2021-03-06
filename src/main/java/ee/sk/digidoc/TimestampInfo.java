/*
 * TimestampInfo.java
 * PROJECT: JDigiDoc
 * DESCRIPTION: Holds data about timestamp source. 
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
package ee.sk.digidoc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;

import ee.sk.utils.Base64Util;
import ee.sk.utils.ConvertUtils;

/**
 * Models the ETSI timestamp element(s) Holds timestamp info and TS_RESP
 * response.
 * 
 * @author Veiko Sinivee
 * @version 1.0
 */
public class TimestampInfo implements Serializable {
    /** elements Id atribute */
    private String id;
    /** parent object - Signature ref */
    private Signature signature;

    private int type = TIMESTAMP_TYPE_UNKNOWN;
    /** Include sublements */
    private ArrayList<IncludeInfo> m_includes;

    private TimeStampResponse tsResp;

    private TimeStampToken tsToken;
    /** real hash calculated over the corresponding xml block */
    private byte[] hash;

    /** possible values for type atribute */
    public static final int TIMESTAMP_TYPE_UNKNOWN = 0;
    public static final int TIMESTAMP_TYPE_ALL_DATA_OBJECTS = 1;
    public static final int TIMESTAMP_TYPE_INDIVIDUAL_DATA_OBJECTS = 2;
    public static final int TIMESTAMP_TYPE_SIGNATURE = 3;
    public static final int TIMESTAMP_TYPE_SIG_AND_REFS = 4;
    public static final int TIMESTAMP_TYPE_REFS_ONLY = 5;
    public static final int TIMESTAMP_TYPE_ARCHIVE = 6;

    public TimestampInfo() {
    }

    /**
     * Accessor for Signature attribute
     * 
     * @return value of Signature attribute
     */
    public Signature getSignature() {
        return signature;
    }

    /**
     * Mutator for Signature attribute
     * 
     * @param uprops
     *            value of Signature attribute
     */
    public void setSignature(Signature sig) {
        signature = sig;
    }

    /**
     * Creates new TimestampInfo
     * 
     * @param id
     *            Id atribute value
     * @param type
     *            timestamp type
     * @throws DigiDocException
     *             for validation errors
     */
    public TimestampInfo(String id, int type) throws DigiDocException {
        setId(id);
        setType(type);
        m_includes = null;
    }

    /**
     * Accessor for Hash attribute
     * 
     * @return value of Hash attribute
     */
    public byte[] getHash() {
        return hash;
    }

    /**
     * Mutator for Hash attribute
     * 
     * @param str
     *            new value for Hash attribute
     */
    public void setHash(byte[] b) {
        hash = b;
    }

    /**
     * Accessor for Id attribute
     * 
     * @return value of Id attribute
     */
    public String getId() {
        return id;
    }

    /**
     * Mutator for Id attribute
     * 
     * @param str
     *            new value for Id attribute
     * @throws DigiDocException
     *             for validation errors
     */
    public void setId(String str) throws DigiDocException {
        DigiDocException ex = validateId(str);

        if (ex != null) {
            throw ex;
        }

        id = str;
    }

    /**
     * Helper method to validate Id
     * 
     * @param str
     *            input data
     * @return exception or null for ok
     */
    private DigiDocException validateId(String str) {
        DigiDocException ex = null;

        if (str == null) {
            ex = new DigiDocException(DigiDocException.ERR_TIMESTAMP_ID, "Id atribute cannot be empty", null);
        }

        return ex;
    }

    /**
     * Accessor for Type attribute
     * 
     * @return value of Type attribute
     */
    public int getType() {
        return type;
    }

    /**
     * Mutator for Type attribute
     * 
     * @param n
     *            new value for Type attribute
     * @throws DigiDocException
     *             for validation errors
     */
    public void setType(int n) throws DigiDocException {
        DigiDocException ex = validateType(n);

        if (ex != null) {
            throw ex;
        }

        type = n;
    }

    /**
     * Helper method to validate Type
     * 
     * @param n
     *            input data
     * @return exception or null for ok
     */
    private DigiDocException validateType(int n) {
        DigiDocException ex = null;
        if (n < TIMESTAMP_TYPE_ALL_DATA_OBJECTS || n > TIMESTAMP_TYPE_ARCHIVE)
            ex = new DigiDocException(DigiDocException.ERR_TIMESTAMP_TYPE, "Invalid timestamp type", null);
        return ex;
    }

    /**
     * Accessor for TimeStampResponse attribute
     * 
     * @return value of TimeStampResponse attribute
     */
    public TimeStampResponse getTimeStampResponse() {
        return tsResp;
    }

    /**
     * Accessor for TimeStampToken attribute
     * 
     * @return value of TimeStampResponse attribute
     */
    // IS FIX TimeStampToken
    public TimeStampToken getTimeStampToken() {
        return tsToken;
    }

    /**
     * Mutator for TimeStampResponse attribute
     * 
     * @param tsr
     *            new value for TimeStampResponse attribute
     * @throws DigiDocException
     *             for validation errors
     */
    public void setTimeStampResponse(TimeStampResponse tsr) throws DigiDocException {
        DigiDocException ex = validateTimeStampResponse(tsr);
        if (ex != null)
            throw ex;
        tsResp = tsr;
    }

    /**
     * Mutator for TimeStampToken attribute
     * 
     * @param tsr
     *            new value for TimeStampResponse attribute
     * @throws DigiDocException
     *             for validation errors
     */
    // IS FIX TimeStampToken
    public void setTimeStampToken(TimeStampToken tst) throws DigiDocException {
        DigiDocException ex = validateTimeStampToken(tst);
        if (ex != null)
            throw ex;
        tsToken = tst;
    }

    /**
     * Helper method to validate TimeStampResponse
     * 
     * @param tsr
     *            input data
     * @return exception or null for ok
     */
    private DigiDocException validateTimeStampResponse(TimeStampResponse tsr) {
        DigiDocException ex = null;
        if (tsr == null)
            ex = new DigiDocException(DigiDocException.ERR_TIMESTAMP_RESP, "timestamp cannot be null", null);
        return ex;
    }

    /**
     * Helper method to validate TimeStampToken
     * 
     * @param tst
     *            input data
     * @return exception or null for ok
     */
    // IS FIX TimeStampToken
    private DigiDocException validateTimeStampToken(TimeStampToken tst) {
        DigiDocException ex = null;
        if (tst == null)
            ex = new DigiDocException(DigiDocException.ERR_TIMESTAMP_RESP, "timestamp cannot be null", null);
        return ex;
    }

    /**
     * return the count of IncludeInfo objects
     * 
     * @return count of IncludeInfo objects
     */
    public int countIncludeInfos() {
        return ((m_includes == null) ? 0 : m_includes.size());
    }

    /**
     * Adds a new IncludeInfo object
     * 
     * @param inc
     *            new object to be added
     */
    public void addIncludeInfo(IncludeInfo inc) {
        if (m_includes == null)
            m_includes = new ArrayList<IncludeInfo>();
        inc.setTimestampInfo(this);
        m_includes.add(inc);
    }

    /**
     * Retrieves IncludeInfo element with the desired index
     * 
     * @param idx
     *            IncludeInfo index
     * @return IncludeInfo element or null if not found
     */
    public IncludeInfo getIncludeInfo(int idx) {
        if (m_includes != null && idx < m_includes.size()) {
            return (IncludeInfo) m_includes.get(idx);
        } else
            return null; // not found
    }

    /**
     * Retrieves the last IncludeInfo element
     * 
     * @return IncludeInfo element or null if not found
     */
    public IncludeInfo getLastIncludeInfo() {
        if (m_includes != null && m_includes.size() > 0) {
            return (IncludeInfo) m_includes.get(m_includes.size() - 1);
        } else
            return null; // not found
    }

    /**
     * Retrieves timestamp responses signature algorithm OID.
     * 
     * @return responses signature algorithm OID
     */
    public String getAlgorithmOid() {
        String oid = null;
        if (tsResp != null) {
            oid = tsResp.getTimeStampToken().getTimeStampInfo().getMessageImprintAlgOID();
        }
        return oid;
    }

    /**
     * Retrieves timestamp responses policy
     * 
     * @return responses policy
     */
    public String getPolicy() {
        String oid = null;
        if (tsResp != null) {
            oid = tsResp.getTimeStampToken().getTimeStampInfo().getPolicy();
        }
        return oid;
    }

    /**
     * Retrieves timestamp issuing time
     * 
     * @return timestamp issuing time
     */
    public Date getTime() {
        Date d = null;
        if (tsResp != null) {
            d = tsResp.getTimeStampToken().getTimeStampInfo().getGenTime();
        }
        return d;
    }

    /**
     * Retrieves timestamp msg-imprint digest
     * 
     * @return timestamp msg-imprint digest
     */
    public byte[] getMessageImprint() {
        byte[] b = null;
        if (tsResp != null) {
            b = tsResp.getTimeStampToken().getTimeStampInfo().getMessageImprintDigest();
        }
        return b;
    }

    /**
     * Retrieves timestamp nonce
     * 
     * @return timestamp nonce
     */
    public BigInteger getNonce() {
        BigInteger b = null;
        if (tsResp != null) {
            b = tsResp.getTimeStampToken().getTimeStampInfo().getNonce();
        }
        return b;
    }

    /**
     * Retrieves timestamp serial number
     * 
     * @return timestamp serial number
     */
    public BigInteger getSerialNumber() {
        BigInteger b = null;
        if (tsResp != null) {
            b = tsResp.getTimeStampToken().getTimeStampInfo().getSerialNumber();
        }
        return b;
    }

    /**
     * Retrieves timestamp is-ordered atribute
     * 
     * @return timestamp is-ordered atribute
     */
    public boolean isOrdered() {
        boolean b = false;
        if (tsResp != null) {
            b = tsResp.getTimeStampToken().getTimeStampInfo().isOrdered();
        }
        return b;
    }

    /**
     * Retrieves timestamp is-ordered atribute
     * 
     * @return timestamp is-ordered atribute
     */
    public String getSignerCN() {
        String s = null;
        if (tsResp != null) {
            // SignerId = m_tsResp.getTimeStampToken().getSignedAttributes()
            // org.bouncycastle.cms.CMSSignedData cms =
            // m_tsResp.getTimeStampToken().

        }
        return s;
    }

    /**
     * Helper method to validate the whole TimestampInfo object
     * 
     * @return a possibly empty list of DigiDocException objects
     */
    public List<DigiDocException> validate() {
        ArrayList<DigiDocException> errs = new ArrayList<DigiDocException>();
        DigiDocException ex = validateId(id);
        if (ex != null)
            errs.add(ex);
        ex = validateType(type);
        if (ex != null)
            errs.add(ex);
        return errs;
    }

    /**
     * Converts the TimestampInfo to XML form
     * 
     * @return XML representation of TimestampInfo
     */
    public byte[] toXML() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            switch (type) {
            case TIMESTAMP_TYPE_ALL_DATA_OBJECTS:
                bos.write(ConvertUtils.str2data("<AllDataObjectsTimeStamp Id=\""));
                break;
            case TIMESTAMP_TYPE_INDIVIDUAL_DATA_OBJECTS:
                bos.write(ConvertUtils.str2data("<IndividualDataObjectsTimeStamp Id=\""));
                break;
            case TIMESTAMP_TYPE_SIGNATURE:
                bos.write(ConvertUtils.str2data("<SignatureTimeStamp Id=\""));
                break;
            case TIMESTAMP_TYPE_SIG_AND_REFS:
                bos.write(ConvertUtils.str2data("<SigAndRefsTimeStamp Id=\""));
                break;
            case TIMESTAMP_TYPE_REFS_ONLY:
                bos.write(ConvertUtils.str2data("<RefsOnlyTimeStamp Id=\""));
                break;
            case TIMESTAMP_TYPE_ARCHIVE:
                bos.write(ConvertUtils.str2data("<ArchiveTimeStamp Id=\""));
                break;
            }
            bos.write(ConvertUtils.str2data(id));
            bos.write(ConvertUtils.str2data("\">"));
            
            for (int i = 0; i < countIncludeInfos(); i++) {
                IncludeInfo inc = getIncludeInfo(i);
                bos.write(inc.toXML());
            }
            
            bos.write(ConvertUtils.str2data("<EncapsulatedTimeStamp>"));
            if (tsResp != null)
                bos.write(ConvertUtils.str2data(Base64Util.encode(tsResp.getTimeStampToken().getEncoded(), 64)));
            bos.write(ConvertUtils.str2data("</EncapsulatedTimeStamp>"));
            switch (type) {
            case TIMESTAMP_TYPE_ALL_DATA_OBJECTS:
                bos.write(ConvertUtils.str2data("</AllDataObjectsTimeStamp>"));
                break;
            case TIMESTAMP_TYPE_INDIVIDUAL_DATA_OBJECTS:
                bos.write(ConvertUtils.str2data("</IndividualDataObjectsTimeStamp>"));
                break;
            case TIMESTAMP_TYPE_SIGNATURE:
                bos.write(ConvertUtils.str2data("</SignatureTimeStamp>"));
                break;
            case TIMESTAMP_TYPE_SIG_AND_REFS:
                bos.write(ConvertUtils.str2data("</SigAndRefsTimeStamp>"));
                break;
            case TIMESTAMP_TYPE_REFS_ONLY:
                bos.write(ConvertUtils.str2data("</RefsOnlyTimeStamp>"));
                break;
            case TIMESTAMP_TYPE_ARCHIVE:
                bos.write(ConvertUtils.str2data("</ArchiveTimeStamp>"));
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    /**
     * Returns the stringified form of CompleteCertificateRefs
     * 
     * @return CompleteCertificateRefs string representation
     */
    public String toString() {
        String str = null;
        try {
            str = new String(toXML());
        } catch (Exception ex) {
        }
        return str;
    }
}
