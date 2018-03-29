package ar.edu.itba.ss.tp3;

/**
* <p>Esta clase representa la estructura del archivo de configuración,
* la cual será automáticamente completada durante el inicio del
* sistema. La clase debe ser completamente mapeable con respecto al
* archivo origen.</p>
*/

public final class Configuration {

	// Archivo de configuración origen:
	public static final String CONFIGURATION_FILENAME
		= "hyper.json";
	
	private Integer n = 0;
	private Long events = 0L;
	
	public Integer getN() {
		return n;
	}
	
	public Long getEvents() {
		return events;
	}
	

}
