package example.apitoken4j;

import dev.paseto.jpaseto.PasetoParser;
import dev.paseto.jpaseto.Pasetos;
import dev.paseto.jpaseto.Paseto;
import dev.paseto.jpaseto.Version;
import dev.paseto.jpaseto.lang.Keys;

import java.security.KeyPair;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

// Disclaimer or use at your own risk: These examples are for learning purpose only. 
// The points discussed comes with no warranty and no claims on the accuracy is made. 
// Some of the items may be insecure so users must do their own analysis. 
// The examples in this repository uses third party libraries. While I acknowledge, appreciate 
// and thank all the developers and maintainers of those libraries, the use of third party libraries 
// and third party links are not an endorsement but to illustrate certain topic. 
// Refer to their respective terms of use.  

/**
 * Sample implementation to generate Paseto using
 * https://github.com/paseto-toolkit/jpaseto. There are similar libraries, refer
 * https://paseto.io/ For Paseto standard, refer https://paseto.io/rfc/
 *
 */
public class PasetoExample implements Token {

	// Key
	private KeyPair keyPair = null;

	// Just for reference
	private PasetoExample() {
		generateKey();
	}

	// Sample to generate the asymmetric RSA key
	private void generateKey() {
		// Using V1 version, refer https://github.com/paseto-toolkit/jpaseto for better
		// options
		keyPair = Keys.keyPairFor(Version.V1);
	}

	// Let's use the create and verify
	public static void main(String[] args) {
		PasetoExample paseto = new PasetoExample();
		String token = paseto.create();
		System.out.println("Paseto : " + token);

		boolean verify = paseto.verify(token);
		System.out.println("Token Verification: " + verify);
	}

	// Example for generating Paseto
	// Normally, these tokens are created by the issuer that could an OAuth server or identity server
	// An user or a service is the bearer of the token
	public String create() {

		String paseto = Pasetos.V1.PUBLIC.builder()
				// Key
				.setPrivateKey(keyPair.getPrivate())
				// Setting a few standard Claims, for more details, refer https://paseto.io/rfc/
				.setIssuer(TokenConstants.ISSUER).setSubject(TokenConstants.SUBJECT)
				.setAudience(TokenConstants.AUDIENCE).setExpiration(Instant.now().plus(1, ChronoUnit.HOURS))
				.setNotBefore(Instant.now()).setTokenId(TokenConstants.TOKEN_ID)
				// Custom claims
				.claim(TokenConstants.CUSTOM_CLAIM, TokenConstants.CUSTOM_CLAIM_VALUE).compact();

		return paseto;
	}

	// Example to verify Paseto
	// Normally, when an user or a service present the token to a service/application 
	// the verification is done by a consuming application or a resource server
	public boolean verify(String token) {
		try {
			// When consuming the JWS, strict enforcements can be made on the expected claims
			PasetoParser pasetoParser = Pasetos.parserBuilder().requireSubject(TokenConstants.SUBJECT)
					.requireIssuer(TokenConstants.ISSUER).requireAudience(TokenConstants.AUDIENCE)
					.setPublicKey(keyPair.getPublic()).build();

			Paseto result = pasetoParser.parse(token);
			System.out.println("Paseto Claims: " + result.getClaims());
			// Additional check may be required
			return true;
		} catch (Exception e) {
			// Instead implement a better error handling
			e.printStackTrace();
		}

		return false;
	}

}
