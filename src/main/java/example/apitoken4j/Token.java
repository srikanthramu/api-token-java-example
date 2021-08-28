package example.apitoken4j;

public interface Token {

	public String create();
	public boolean verify(String token);
}

