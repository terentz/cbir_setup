public enum Feature {
	COLOUR_HISTOGRAM("CH"),
	COLOUR_MOMENTS("CM"),
	TEXTURE("TX");
	
	private final String abbr;
	
	Feature(String abbr) {
		this.abbr = abbr;
	}
}
