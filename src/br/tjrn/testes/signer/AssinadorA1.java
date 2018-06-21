package br.tjrn.testes.signer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;

public class AssinadorA1 {  
    
    
    private String caminhoCertificado;  
      
      
    public AssinadorA1(String caminhoCertificado){  
          
        this.caminhoCertificado =  caminhoCertificado;  
          
    }  
      
      
      
     public void signA1(String src, String dest)  {    
              
         try {  
               
            Security.addProvider(new BouncyCastleProvider());    
             String path = this.caminhoCertificado+"/bckpcertificado.pfx";    
                String keystore_password = "awedfri!@123";   
                String key_password = "awedfri!@123!@123";  
                KeyStore ks = KeyStore.getInstance("pkcs12", "BC");    
                  
                ks.load(new FileInputStream(path), keystore_password.toCharArray());    
                String alias = ks.aliases().nextElement();    
                PrivateKey key = (PrivateKey) ks.getKey(alias, key_password.toCharArray());   
                Certificate[] chain = ks.getCertificateChain(alias);   
                PdfReader reader = new PdfReader(src);    
                FileOutputStream os = new FileOutputStream(dest);    
                PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0',null,false);    
                  
                PdfSignatureAppearance appearance = stamper.getSignatureAppearance();    
                appearance.setCrypto(key, chain, null, PdfSignatureAppearance.SELF_SIGNED);     
                appearance.setReason("Assinatura Digital.");    
                appearance.setLocation("Sistema Doc Digital");    
                appearance.setSignDate(Calendar.getInstance());    
                appearance.setVisibleSignature(new Rectangle(10, 0, 300, 30), 1, "Inicio");    
                stamper.close();  
        } catch (UnrecoverableKeyException e) {  
              
            e.printStackTrace();  
        } catch (KeyStoreException e) {  
              
            e.printStackTrace();  
        } catch (NoSuchProviderException e) {  
              
            e.printStackTrace();  
        } catch (NoSuchAlgorithmException e) {  
              
            e.printStackTrace();  
        } catch (CertificateException e) {  
              
            e.printStackTrace();  
        } catch (FileNotFoundException e) {  
              
            e.printStackTrace();  
        } catch (IOException e) {  
              
            e.printStackTrace();  
        } catch (DocumentException e) {  
              
            e.printStackTrace();  
        }    
        }    
          
      
}  