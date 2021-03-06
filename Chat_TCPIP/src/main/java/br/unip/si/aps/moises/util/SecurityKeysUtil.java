package br.unip.si.aps.moises.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SecurityKeysUtil {
	private static KeyPairGenerator generator;
	
	private SecurityKeysUtil() {
	}
	
	private static synchronized KeyPairGenerator getGenerator() {
		try {
			return generator == null ? (KeyPairGenerator.getInstance("RSA")) : generator;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static synchronized KeyPair newKeyPair(int numBits) {
		KeyPairGenerator generator = getGenerator();
		generator.initialize(numBits);
		return generator.generateKeyPair();
	}
	
	public static String encodePrivateKey(PrivateKey key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	public static String encodePublicKey(PublicKey key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	public static PrivateKey decodePrivateKey(String keyEncoded) {
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyEncoded));
			return factory.generatePrivate(keySpec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static PublicKey decodePublicKey(String keyEncoded) {
		try {
			KeyFactory factory = KeyFactory.getInstance("RSA");
			EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyEncoded));
			return factory.generatePublic(keySpec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
}
