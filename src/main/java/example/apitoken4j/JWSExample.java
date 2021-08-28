package example.apitoken4j;

import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

// Disclaimer or use at your own risk: These examples are for learning purpose only. 
// The points discussed comes with no warranty and no claims on the accuracy is made. 
// Some of the items may be insecure so users must do their own analysis. 
// The examples in this repository uses third party libraries. While I acknowledge, appreciate 
// and thank all the developers and maintainers of those libraries, the use of third party libraries 
// and third party links are not an endorsement but to illustrate certain topic. 
// Refer to their respective terms of use.  

/**
 * Example implementation to generate JWS using
 * https://bitbucket.org/b_c/jose4j/. There are similar libraries, refer
 * https://jwt.io/ For JWS standard, refer
 * https://datatracker.ietf.org/doc/html/rfc7515
 * 
 */
public class JWSExample implements Token {

	// Key
	private RsaJsonWebKey rsaJsonWebKey = null;

	// Just for reference
	private JWSExample() {
		generateKey();
	}

	// Sample to generate the asymmetric RSA key
	private void generateKey() {
		try {
			rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
		} catch (JoseException e) {
			e.printStackTrace();
		}
		rsaJsonWebKey.setKeyId(TokenConstants.KID);
	}

	// Let's use the create and verify
	public static void main(String[] args) {
		JWSExample jws = new JWSExample();
		String token = jws.create();
		System.out.println("JWT - JWS: " + token);

		boolean verify = jws.verify(token);
		System.out.println("Token Verification: " + verify);
	}

	// Example for generating JWS tokens. 
	// Normally, these tokens are created by the issuer that could an OAuth server or identity server
	// An user or a service is the bearer of the token
	public String create() {
		// Reference from https://bitbucket.org/b_c/jose4j/wiki/JWT%20Examples
		// JWS reference using RS256
		JsonWebSignature jws = new JsonWebSignature();

		try {

			// Key
			jws.setKey(rsaJsonWebKey.getPrivateKey());

			// JWT Header
			jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
			// Whether good or bad, JWS provides various algorithms to choose from.
			// Below is one option however refer https://datatracker.ietf.org/doc/html/rfc7518#section-4.1 for complete list
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

			// JWT Claims
			// For detailed reserved claims, refer
			// https://datatracker.ietf.org/doc/html/rfc7519#section-4
			JwtClaims claims = new JwtClaims();
			claims.setIssuer(TokenConstants.ISSUER);
			claims.setAudience(TokenConstants.AUDIENCE);
			claims.setSubject(TokenConstants.SUBJECT);
			claims.setExpirationTimeMinutesInTheFuture(10);
			claims.setGeneratedJwtId();
			claims.setNotBeforeMinutesInThePast(2);

			// CustomClaim
			claims.setClaim(TokenConstants.CUSTOM_CLAIM, TokenConstants.CUSTOM_CLAIM_VALUE);
			jws.setPayload(claims.toJson());

			String jwt = jws.getCompactSerialization();
			return jwt;

		} catch (JoseException e) {
			// Instead implement a better error handling
			e.printStackTrace();
		}

		return null;
	}

	// Example to verify JWS tokens
	// Normally, when an user or a service present the token to a service/application 
	// the verification is done by a consuming application or a resource server
	public boolean verify(String jwt) {

		// When consuming the JWS, strict enforcements can be made on the expected claims
		JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime().setAllowedClockSkewInSeconds(30)
				.setRequireSubject().setExpectedIssuer(TokenConstants.ISSUER)
				.setExpectedAudience(TokenConstants.AUDIENCE).setExpectedSubject(TokenConstants.SUBJECT)
				.setVerificationKey(rsaJsonWebKey.getPublicKey())
				.setJwsAlgorithmConstraints(ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256).build();

		try {
			JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
			System.out.println("JWT Claims: " + jwtClaims);
			// Additional check may be required
			return true;
		} catch (InvalidJwtException e) {
			e.printStackTrace();
		}
		return false;
	}

}
