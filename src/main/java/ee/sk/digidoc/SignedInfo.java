/*
 * SignedInfo.java
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

package ee.sk.digidoc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ee.sk.digidoc.services.CanonicalizationService;
import ee.sk.utils.DDUtils;

/**
 * Represents an XML-DSIG SignedInfo block
 * 
 * @author Veiko Sinivee
 * @version 1.0
 */
public class SignedInfo implements Serializable {
    /** reference to parent Signature object */
    private Signature m_signature;

    private String m_signatureMethod;

    private String m_canonicalizationMethod;

    private List<Reference> m_references;
    /** digest over the original bytes read from XML file */
    private byte[] m_origDigest;

    /**
     * Creates new SignedInfo. Initializes everything to null.
     * 
     * @param sig
     *            parent Signature reference
     */
    public SignedInfo(Signature sig) {
        m_signature = sig;
        m_signatureMethod = null;
        m_canonicalizationMethod = null;
        m_references = null;
        m_origDigest = null;
    }

    /**
     * Creates new SignedInfo
     * 
     * @param sig
     *            parent Signature reference
     * @param signatureMethod
     *            signature method uri
     * @param canonicalizationMethod
     *            xml canonicalization method uri throws DigiDocException
     */
    public SignedInfo(Signature sig, String signatureMethod, String canonicalizationMethod) throws DigiDocException {
        m_signature = sig;
        setSignatureMethod(signatureMethod);
        setCanonicalizationMethod(canonicalizationMethod);
        m_references = null;
        m_origDigest = null;
    }

    /**
     * Accessor for signature attribute
     * 
     * @return value of signature attribute
     */
    public Signature getSignature() {
        return m_signature;
    }

    /**
     * Mutator for signature attribute
     * 
     * @param sig
     *            new value for signature attribute
     */
    public void setSignature(Signature sig) {
        m_signature = sig;
    }

    /**
     * Accessor for origDigest attribute
     * 
     * @return value of origDigest attribute
     */
    public byte[] getOrigDigest() {
        return m_origDigest;
    }

    /**
     * Mutator for origDigest attribute
     * 
     * @param str
     *            new value for origDigest attribute
     */
    public void setOrigDigest(byte[] data) {
        m_origDigest = data;
    }

    /**
     * Accessor for signatureMethod attribute
     * 
     * @return value of signatureMethod attribute
     */
    public String getSignatureMethod() {
        return m_signatureMethod;
    }

    /**
     * Mutator for signatureMethod attribute
     * 
     * @param str
     *            new value for signatureMethod attribute
     * @throws DigiDocException
     *             for validation errors
     */
    public void setSignatureMethod(String str) throws DigiDocException {
        DigiDocException ex = validateSignatureMethod(str);
        if (ex != null)
            throw ex;
        m_signatureMethod = str;
    }

    /**
     * Helper method to validate a signature method
     * 
     * @param str
     *            input data
     * @return exception or null for ok
     */
    private DigiDocException validateSignatureMethod(String str) {
        DigiDocException ex = null;
        if (str == null || !str.equals(SignedDoc.RSA_SHA1_SIGNATURE_METHOD))
            ex = new DigiDocException(DigiDocException.ERR_SIGNATURE_METHOD,
                    "Currently supports only RSA-SHA1 signatures", null);
        return ex;
    }

    /**
     * Accessor for canonicalizationMethod attribute
     * 
     * @return value of canonicalizationMethod attribute
     */
    public String getCanonicalizationMethod() {
        return m_canonicalizationMethod;
    }

    /**
     * Mutator for canonicalizationMethod attribute
     * 
     * @param str
     *            new value for canonicalizationMethod attribute
     * @throws DigiDocException
     *             for validation errors
     */
    public void setCanonicalizationMethod(String str) throws DigiDocException {
        DigiDocException ex = validateCanonicalizationMethod(str);
        if (ex != null)
            throw ex;
        m_canonicalizationMethod = str;
    }

    /**
     * Helper method to validate a signature method
     * 
     * @param str
     *            input data
     * @return exception or null for ok
     */
    private DigiDocException validateCanonicalizationMethod(String str) {
        DigiDocException ex = null;
        if (str == null || !str.equals(SignedDoc.CANONICALIZATION_METHOD_20010315))
            ex = new DigiDocException(DigiDocException.ERR_CANONICALIZATION_METHOD,
                    "Currently supports only Canonical XML 1.0", null);
        return ex;
    }

    /**
     * Returns the count of Reference objects
     * 
     * @return count of Reference objects
     */
    public int countReferences() {
        return ((m_references == null) ? 0 : m_references.size());
    }

    /**
     * Adds a new reference object
     * 
     * @param ref
     *            Reference object to add
     */
    public void addReference(Reference ref) {
        if (m_references == null)
            m_references = new ArrayList<Reference>();
        m_references.add(ref);
    }

    /**
     * Returns the desired Reference object
     * 
     * @param idx
     *            index of the Reference object
     * @return desired Reference object
     */
    public Reference getReference(int idx) {
        return (Reference) m_references.get(idx);
    }

    /**
     * Returns the desired Reference object
     * 
     * @param df
     *            DataFile whose digest we are searching
     * @return desired Reference object
     */
    public Reference getReferenceForDataFile(DataFile df) {
        Reference ref = null;
        for (int i = 0; (m_references != null) && (i < m_references.size()); i++) {
            Reference r1 = (Reference) m_references.get(i);
            // A Inga <2008 aprill> BDOCiga seotud muudatused xml-is 1
            // IS Fix reference algus
            if (r1.getUri().equals(df.getFullName())) {
                ref = r1;
                break;
            }
            // IS Fix reference lopp
            // L Inga <2008 aprill> BDOCiga seotud muudatused xml-is 1
            if (r1.getUri().equals("#" + df.getId())) {
                ref = r1;
                break;
            }
        }
        return ref;
    }

    /**
     * Returns the desired Reference object
     * 
     * @param sp
     *            SignedProperties whose digest we are searching
     * @return desired Reference object
     */
    public Reference getReferenceForSignedProperties(SignedProperties sp) {
        Reference ref = null;
        for (int i = 0; (m_references != null) && (i < m_references.size()); i++) {
            Reference r1 = (Reference) m_references.get(i);
            if (r1.getUri().equals("#" + sp.getId())) {
                ref = r1;
                break;
            }
        }
        return ref;
    }

    /**
     * Returns the last Reference object
     * 
     * @return desired Reference object
     */
    public Reference getLastReference() {
        return (Reference) m_references.get(m_references.size() - 1);
    }

    /**
     * Helper method to validate references
     * 
     * @return exception or null for ok
     */
    private List<DigiDocException> validateReferences() {
        ArrayList<DigiDocException> errs = new ArrayList<DigiDocException>();
        if (countReferences() < 2) {
            errs.add(new DigiDocException(DigiDocException.ERR_NO_REFERENCES, "At least 2 References are required!",
                    null));
        } else {
            for (int i = 0; i < countReferences(); i++) {
                Reference ref = getReference(i);
                List<DigiDocException> e = ref.validate();
                if (!e.isEmpty())
                    errs.addAll(e);
            }
        }
        return errs;
    }

    /**
     * Helper method to validate the whole SignedInfo object
     * 
     * @return a possibly empty list of DigiDocException objects
     */
    public List<DigiDocException> validate() {
        ArrayList<DigiDocException> errs = new ArrayList<DigiDocException>();
        DigiDocException ex = validateSignatureMethod(m_signatureMethod);
        if (ex != null)
            errs.add(ex);
        ex = validateCanonicalizationMethod(m_canonicalizationMethod);
        if (ex != null)
            errs.add(ex);
        List<DigiDocException> e = validateReferences();
        if (!e.isEmpty())
            errs.addAll(e);
        return errs;
    }

    /**
     * Calculates the digest of SignedInfo block If the user has set origDigest
     * attribute which is allways done when reading the XML file, then this
     * digest is returned otherwise a new digest is calculated.
     * 
     * @return SignedInfo block digest
     */
    public byte[] calculateDigest(CanonicalizationService canonicalizationService) throws DigiDocException {
        if (m_origDigest == null) {
            byte[] tmp = canonicalizationService.canonicalize(toXML(), SignedDoc.CANONICALIZATION_METHOD_20010315);
            return DDUtils.digest(tmp);
        } else
            return m_origDigest;
    }

    /**
     * Converts the SignedInfo to XML form
     * 
     * @return XML representation of SignedInfo
     */
    public byte[] toXML() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write("<SignedInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\">\n".getBytes());
            bos.write("<CanonicalizationMethod Algorithm=\"".getBytes());
            bos.write(m_canonicalizationMethod.getBytes());
            bos.write("\">\n</CanonicalizationMethod>\n".getBytes());
            bos.write("<SignatureMethod Algorithm=\"".getBytes());
            bos.write(m_signatureMethod.getBytes());
            bos.write("\">\n</SignatureMethod>\n".getBytes());
            
            for (int i = 0; (m_references != null) && (i < m_references.size()); i++) {
                Reference ref = (Reference) m_references.get(i);
                bos.write(ref.toXML());
                bos.write("\n".getBytes());
            }
            
            bos.write("</SignedInfo>".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    /**
     * Returns the stringified form of SignedInfo
     * 
     * @return SignedInfo string representation
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
