package br.tjrn.testes.signer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author onvaid@hotmail.com
 */
public class OnCert {
	// Procedimento que retorna o Keystore
	public static KeyStore funcKeyStore(String strAliasTokenCert) throws NoSuchProviderException, IOException,
			NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException {
		String strResult = "";
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
			ks.load(null, null);
			Enumeration<String> aliasEnum = ks.aliases();
			while (aliasEnum.hasMoreElements()) {
				String aliasKey = (String) aliasEnum.nextElement();
				if (ks.isKeyEntry(aliasKey)) {
					strResult = aliasKey;
				}
				if (ks.getCertificateAlias(ks.getCertificate(strResult)) == strAliasTokenCert) {
					break;
				}
			}
		} catch (KeyStoreException ex) {
			System.out.println("ERROR " + ex.getMessage());
		}
		return ks;
	}

	// Procedimento de listagem dos certificados digitais
	public static String[] funcListaCertificados(boolean booCertValido)
			throws NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateException {
		// Estou setando a variavel para 20 dispositivos no maximo
		String strResult[] = new String[20];
		Integer intCnt = 0;
		try {
			KeyStore ks = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
			ks.load(null, null);
			Enumeration<String> aliasEnum = ks.aliases();
			while (aliasEnum.hasMoreElements()) {
				String aliasKey = (String) aliasEnum.nextElement();
				if (booCertValido == false) {
					strResult[intCnt] = aliasKey;
				} else if (ks.isKeyEntry(aliasKey)) {
					strResult[intCnt] = aliasKey;
				}
				if (strResult[intCnt] != null) {
					intCnt = intCnt + 1;
				}
			}
		} catch (KeyStoreException ex) {
			System.out.println("ERROR " + ex.getMessage());
		}
		return strResult;
	}

	// Procedimento que retorna a chave privada de um certificado Digital
	public static PrivateKey funcChavePrivada(String strAliasTokenCert, String strAliasCertificado,
			String strArquivoCertificado, String strSenhaCertificado) throws Exception {
		KeyStore ks = null;
		PrivateKey privateKey = null;
		if (strAliasTokenCert == null || strAliasTokenCert == "") {
			ks = KeyStore.getInstance("PKCS12");
			FileInputStream fis = new FileInputStream(strArquivoCertificado);
			// Efetua o load do keystore
			ks.load(fis, strSenhaCertificado.toCharArray());
			// captura a chave privada para a assinatura
			privateKey = (PrivateKey) ks.getKey(strAliasCertificado, strSenhaCertificado.toCharArray());
		} else {
			if (strSenhaCertificado == null || strSenhaCertificado == "") {
				strSenhaCertificado = "Senha";
			}
			// Procedimento para a captura da chave privada do token/cert
			privateKey = (PrivateKey) funcKeyStore(strAliasTokenCert).getKey(strAliasTokenCert,
					strSenhaCertificado.toCharArray());
		}
		return privateKey;
	}

	// Procedimento que retorna a chave publica de um certificado Digital
	public static PublicKey funcChavePublica(String strAliasTokenCert, String strAliasCertificado,
			String strArquivoCertificado, String strSenhaCertificado) throws Exception {
		KeyStore ks = null;
		PublicKey chavePublica = null;
		if (strAliasTokenCert == null || strAliasTokenCert == "") {
			ks = KeyStore.getInstance("PKCS12");
			FileInputStream fis = new FileInputStream(strArquivoCertificado);
			// InputStream entrada para o arquivo
			ks.load(fis, strSenhaCertificado.toCharArray());
			fis.close();
			Key chave = (Key) ks.getKey(strAliasCertificado, strSenhaCertificado.toCharArray());
			// O tipo de dado é declarado desse modo por haver ambigüidade (Classes
			// assinadas com o mesmo nome "Certificate")
			java.security.Certificate cert = (java.security.Certificate) ks.getCertificate(strAliasCertificado);
			chavePublica = cert.getPublicKey();
		} else {
			if (strSenhaCertificado == null || strSenhaCertificado == "") {
				strSenhaCertificado = "Senha";
			}
			// Procedimento se for utilizar token para a captura de chave publica
			ks = funcKeyStore(strAliasTokenCert);
			Key key = ks.getKey(strAliasTokenCert, strSenhaCertificado.toCharArray());
			java.security.cert.Certificate crtCert = ks.getCertificate(strAliasTokenCert);
			chavePublica = crtCert.getPublicKey();
		}
		return chavePublica;
	}

	// Procedimento que verifica a assinatura
	public static boolean funcAssinaturaValida(PublicKey pbKey, byte[] bteBuffer, byte[] bteAssinado,
			String strAlgorithmAssinatura) throws Exception {
		if (strAlgorithmAssinatura == null) {
			strAlgorithmAssinatura = "MD5withRSA";
		}
		Signature isdAssinatura = Signature.getInstance(strAlgorithmAssinatura);
		isdAssinatura.initVerify(pbKey);
		isdAssinatura.update(bteBuffer, 0, bteBuffer.length);
		return isdAssinatura.verify(bteAssinado);
	}

	// Procedimento que gera a assinatura
	public static byte[] funcGeraAssinatura(PrivateKey pbKey, byte[] bteBuffer, String strAlgorithmAssinatura)
			throws Exception {
		if (strAlgorithmAssinatura == null) {
			strAlgorithmAssinatura = "MD5withRSA";
		}
		Signature isdAssinatura = Signature.getInstance(strAlgorithmAssinatura);
		isdAssinatura.initSign(pbKey);
		isdAssinatura.update(bteBuffer, 0, bteBuffer.length);
		return isdAssinatura.sign();
	}

	// Procedimento que retorna o status do certificado
	public static String funcStatusCertificado(X509Certificate crtCertificado) {
		try {
			crtCertificado.checkValidity();
			return "Certificado válido!";
		} catch (CertificateExpiredException E) {
			return "Certificado expirado!";
		} catch (CertificateNotYetValidException E) {
			return "Certificado inválido!";
		}
	}

	// Procedimento que retorna o certificado selecionado
	public static X509Certificate funcCertificadoSelecionado(String strAliasTokenCert, String strAliasCertificado,
			String strArquivoCertificado, String strSenhaCertificado) throws NoSuchProviderException, IOException,
			NoSuchAlgorithmException, CertificateException, UnrecoverableEntryException, KeyStoreException {
		X509Certificate crtCertificado = null;
		KeyStore crtRepositorio = null;
		if (strAliasTokenCert == null || strAliasTokenCert == "") {
			// Procedimento de captura do certificao arquivo passado como parametro
			InputStream dado = new FileInputStream(strArquivoCertificado);
			crtRepositorio = KeyStore.getInstance("PKCS12");
			crtRepositorio.load(dado, strSenhaCertificado.toCharArray());
			crtCertificado = (X509Certificate) crtRepositorio.getCertificate(strAliasCertificado);
		} else {
			if (strSenhaCertificado == null || strSenhaCertificado == "") {
				strSenhaCertificado = "Senha";
			}
			// Procedimento de captura do certificao token passado como parametro
			KeyStore.PrivateKeyEntry keyEntry;
			try {
				keyEntry = (KeyStore.PrivateKeyEntry) funcKeyStore(strAliasTokenCert).getEntry(strAliasTokenCert,
						new KeyStore.PasswordProtection(strSenhaCertificado.toCharArray()));
				crtCertificado = (X509Certificate) keyEntry.getCertificate();
			} catch (KeyStoreException ex) {
				//Logger.getLogger(CertEL.class.getName()).log(Level.SEVERE, null, ex);
				ex.printStackTrace();
			}
		}
		return crtCertificado;
	}

	// Procedimento de Parametros de assinatura
	public static class TAssinaXML {
		// MD2withRSA - MD5withRSA - SHA1withRSA - SHA224withRSA - SHA256withRSA -
		// SHA1withDSA - DSA - RawDSA
		// public String strAlgorithmAssinatura = "MD5withRSA";
		public String strAliasTokenCert = null;
		public String strAliasCertificado = null;
		public String strArquivoCertificado = null;
		public String strSenhaCertificado = null;
		public String strArquivoXML = null;
		public String strArquivoSaveXML = null;
		public String C14N_TRANSFORM_METHOD = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
		public boolean booNFS = true;
	}

	// Procedimento de assinar XML
	public static boolean funcAssinaXML(TAssinaXML tpAssinaXML) throws Exception {
		Signature sgi = null;
		XMLSignatureFactory sig = null;
		SignedInfo si = null;
		KeyInfo ki = null;
		String strTipoSign = "infNFe";
		String strID = "Id";
		if (tpAssinaXML.booNFS) {
			strTipoSign = "LoteRps";// "InfNfse";
			strID = "Id";
		}
		// Capturo o certificado
		X509Certificate cert = funcCertificadoSelecionado(tpAssinaXML.strAliasTokenCert,
				tpAssinaXML.strAliasCertificado, tpAssinaXML.strArquivoCertificado, tpAssinaXML.strSenhaCertificado);
		// Inicializo o arquivo/carrego
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(tpAssinaXML.strArquivoXML));
		sig = XMLSignatureFactory.getInstance("DOM");
		ArrayList<Transform> transformList = new ArrayList<Transform>();
		Transform enveloped = sig.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
		Transform c14n = sig.newTransform(tpAssinaXML.C14N_TRANSFORM_METHOD, (TransformParameterSpec) null);
		transformList.add(enveloped);
		transformList.add(c14n);
		NodeList elements = doc.getElementsByTagName(strTipoSign);
		org.w3c.dom.Element el = (org.w3c.dom.Element) elements.item(0);
		String id = el.getAttribute(strID);
		Reference r = sig.newReference("#".concat(id), sig.newDigestMethod(DigestMethod.SHA1, null), transformList,
				null, null);
		si = sig.newSignedInfo(
				sig.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
				sig.newSignatureMethod(SignatureMethod.RSA_SHA1, null), Collections.singletonList(r));
		KeyInfoFactory kif = sig.getKeyInfoFactory();
		List x509Content = new ArrayList();
		x509Content.add(cert);
		X509Data xd = kif.newX509Data(x509Content);
		ki = kif.newKeyInfo(Collections.singletonList(xd));
		DOMSignContext dsc = new DOMSignContext(funcChavePrivada(tpAssinaXML.strAliasTokenCert,
				tpAssinaXML.strAliasCertificado, tpAssinaXML.strArquivoCertificado, tpAssinaXML.strSenhaCertificado),
				doc.getDocumentElement());
		XMLSignature signature = sig.newXMLSignature(si, ki);
		signature.sign(dsc);
		// Salvo o arquivo assinado
		OutputStream os = new FileOutputStream(tpAssinaXML.strArquivoSaveXML);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer trans = tf.newTransformer();
		trans.transform(new DOMSource(doc), new StreamResult(os));
		return true;
	}
}