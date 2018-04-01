package ar.edu.itba.ss.tp3;

/**
* <p>Esta clase representa la estructura del archivo de configuración,
* la cual será automáticamente completada durante el inicio del
* sistema. La clase debe ser completamente mapeable con respecto al
* archivo origen.</p>
*/

public final class SimulateConfiguration {

	// Archivo de configuración origen:
	public static final String CONFIGURATION_FILENAME
		= "config-simulate.json";
	

	private Long events = 0L;
	private Double tmax = 0.0;
	private Double l = 0.0;
	private Double deltat = 0.0;
	private String inputfile = "";
	private String outputfile = "";
		
	public static String getConfigurationFilename() {
		return CONFIGURATION_FILENAME;
	}

	public Long getEvents() {
		return events;
	}

	public Double getTmax() {
		return tmax;
	}

	public Double getL() {
		return l;
	}
	
	public String getInputfile() {
		return inputfile;
	}

	public String getOutputfile() {
		return outputfile;
	}

	public Double getDeltat() {
		return deltat;
	}

}
