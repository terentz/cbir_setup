public enum ResultStatus {
	RAW('R'),
	NORMALISED('N');
	
	private final char abbr;
	
	ResultStatus(char abbr) {
		this.abbr = abbr;
	}
}