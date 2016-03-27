public enum Moment {
	MEAN("AV"),
	STD_DEV("SD"),
	SKEWNESS("SK");
	
	private final String abbr;
	
	Moment(String abbr) {
		this.abbr = abbr;
	}
}
