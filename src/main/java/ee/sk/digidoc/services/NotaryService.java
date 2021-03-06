package ee.sk.digidoc.services;

import java.security.cert.X509Certificate;

import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.Notary;
import ee.sk.digidoc.Signature;

public interface NotaryService {

    boolean isKnownOCSPCert(String cn);

    /**
     * Get confirmation from AS Sertifitseerimiskeskus by creating an OCSP
     * request and parsing the returned OCSP response
     * 
     * @param sig
     *            Signature object
     * @param signersCert
     *            signature owners cert
     * @param caCert
     *            CA cert for this signer
     * @param notaryCert
     *            notarys own cert
     * @returns Notary object
     */
    Notary getConfirmation(Signature sig, X509Certificate signersCert, X509Certificate caCert) throws DigiDocException;

    /**
     * Check the response and parse it's data
     * 
     * @param not
     *            initial Notary object that contains only the raw bytes of an
     *            OCSP response
     * @returns Notary object with data parsed from OCSP response
     */
    Notary parseAndVerifyResponse(Signature sig, Notary not) throws DigiDocException;

    /**
     * Returns the OCSP responders certificate
     * 
     * @param responderCN
     *            responder-id's CN
     * @param specificCertNr
     *            specific cert number that we search. If this parameter is null
     *            then the newest cert is seleced (if many exist)
     * @returns OCSP responders certificate
     */
    X509Certificate getNotaryCert(String responderCN, String specificCertNr);

    /**
     * Verifies the certificate by creating an OCSP request and sending it to SK
     * server.
     * 
     * @param cert certificate to verify
     * @throws DigiDocException if the certificate is not valid
     */
    void checkCertificate(X509Certificate cert) throws DigiDocException;

}
