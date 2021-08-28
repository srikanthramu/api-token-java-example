package example.apitoken4j;

public class TokenConstants {

	// Issuer is typically an identity or auth service
	public static final String ISSUER = "I issued this Token";
	// Audience is a services (for example resource service) that receives the token about an user/service 
	public static final String AUDIENCE = "http://RecipientOfThisToken";
	// An user or service presenting the token to a service
	public static final String SUBJECT = "It's Me!";
	// Claims or details about the user/service
	public static final String SUBJECT_CAVEAT = "Subject = " + TokenConstants.SUBJECT;
	public static final String CUSTOM_CLAIM = "Action";
	public static final String CUSTOM_CLAIM_VALUE = "Read";
	public static final String PRIV_CAVEAT = TokenConstants.CUSTOM_CLAIM + " = " + TokenConstants.CUSTOM_CLAIM_VALUE;
	// Token metadata
	public static final String KID = "KeyID 123456";
	public static final String TOKEN_ID = "Token ID 12345";
}
