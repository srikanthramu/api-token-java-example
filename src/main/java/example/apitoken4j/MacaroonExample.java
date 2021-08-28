package example.apitoken4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.github.nitram509.jmacaroons.Macaroon;
import com.github.nitram509.jmacaroons.MacaroonsBuilder;
import com.github.nitram509.jmacaroons.MacaroonsVerifier;
import com.github.nitram509.jmacaroons.verifier.TimestampCaveatVerifier;

// Disclaimer or use at your own risk: These examples are for learning purpose only. 
// The points discussed comes with no warranty and no claims on the accuracy is made. 
// Some of the items may be insecure so users must do their own analysis. 
// The examples in this repository uses third party libraries. While I acknowledge, appreciate 
// and thank all the developers and maintainers of those libraries, the use of third party libraries 
// and third party links are not an endorsement but to illustrate certain topic. 
// Refer to their respective terms of use.  

/**
 * Example to generate Macaroon using https://github.com/nitram509/jmacaroons,
 * There are similar libraries, refer to http://macaroons.io/ 
 * For Macaroon specification, refer https://research.google/pubs/pub41892/
 * https://github.com/rescrv/libmacaroons/blob/master/README
 *
 */
public class MacaroonExample implements Token {

	// Key
	private String secretKey = null;
	private String identifier = null;

	// Just for reference
	private MacaroonExample() {
		generateKey();
	}

	// Secret Key step
	private void generateKey() {
		// Hard coded just for illustration purse
		secretKey = "eyJzdWIiOiJJdCdzIE1lISIsImF1ZCI6IlJlY2lwaWVudCBvZiB0aGlzIHRva2VuIiwibmJmIjoiMjAyMS0wOC0yOFQwMzozMDozNS45MjIrMDA6";
		identifier = TokenConstants.KID;
	}

	// Let's use the create and verify
	public static void main(String[] args) {
		MacaroonExample paseto = new MacaroonExample();
		String token = paseto.create();
		System.out.println("Serialized Macaroon : " + token);

		boolean verify = paseto.verify(token);
		System.out.println("Token Verification: " + verify);
	}

	// Example for generating Macaroon
	public String create() {
		String location = TokenConstants.AUDIENCE;
		Macaroon macaroon = new MacaroonsBuilder(location, secretKey, identifier)
				.add_first_party_caveat(TokenConstants.SUBJECT_CAVEAT)
				.add_first_party_caveat(TokenConstants.PRIV_CAVEAT)
				.add_first_party_caveat("time < " + Instant.now().plus(1, ChronoUnit.HOURS)).getMacaroon();
		System.out.println(macaroon.inspect());
		String macaroonStr = macaroon.serialize();
		return macaroonStr;
	}

	// Example to verify Macaroon
	public boolean verify(String token) {
		Macaroon macaroon = MacaroonsBuilder.deserialize(token);
		MacaroonsVerifier verifier = new MacaroonsVerifier(macaroon);
		verifier.satisfyExact(TokenConstants.SUBJECT_CAVEAT);
		verifier.satisfyExact(TokenConstants.PRIV_CAVEAT);
		verifier.satisfyGeneral(new TimestampCaveatVerifier());
		boolean valid = verifier.isValid(secretKey);

		return valid;
	}

}
