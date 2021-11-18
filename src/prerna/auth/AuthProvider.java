package prerna.auth;

import java.util.HashSet;
import java.util.Set;

public enum AuthProvider {

	GOOGLE, 
	GOOGLE_MAP,
	GITHUB,
	GITLAB,
	MS, // this is azure graph
	SF, // this is salesforce 
	SITEMINDER,
	SURVEYMONKEY,
	AMAZON,
	NATIVE,
	FORGEROCK,
	SAML,
	ADFS,
	
	// this one is kinda special ...
	CAC, 
	WINDOWS_USER,
	
	// these are not used as much ...
	FACEBOOK, 
	TWITTER, 
	DROPBOX, 
	PRODUCT_HUNT, 
	IN, 
	BB,

	// catch all for everything
	GENERIC,
	;

	public String toString() {
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
	
	public static AuthProvider getProviderFromString(String authProv) {
		AuthProvider provider = null;
		try {
			provider = AuthProvider.valueOf(authProv.toUpperCase());
		} catch(Exception e){
			provider = AuthProvider.GENERIC;
		}
		return provider;
	}
	
	/**
	 * Get the keys are they should be in the social.properties files
	 * All keys should be the same as the enum name but lower case
	 * @return
	 */
	public static Set<String> getSocialPropKeys() {
		Set<String> vals = new HashSet<>();
		for(AuthProvider auth : AuthProvider.values()) {
			vals.add(auth.name().toLowerCase());
		}
		
		return vals;
	}

}
