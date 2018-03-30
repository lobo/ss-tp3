package ar.edu.itba.ss.tp3;

/**
* <p>Esta clase representa la estructura del archivo de configuración,
* la cual será automáticamente completada durante el inicio del
* sistema. La clase debe ser completamente mapeable con respecto al
* archivo origen.</p>
*/

public final class GenerateConfiguration {

	// Archivo de configuración origen:
	public static final String CONFIGURATION_FILENAME
		= "config-generate.json";
	
	private Integer n = 0;
	private Long events = 0L;
	private Double tmax = 0.0;
	private Double l = 0.0;
	private Double r = 0.0;
	private Double rbig = 0.0;
	private Double xbig = 0.0;
	private Double ybig = 0.0;
	private Double speed = 0.0;
	private Double mass = 0.0;
	private Double massbig = 0.0;
	private Double deltat = 0.0;
	private String inputfile = "";
	private String outputfile = "";
	
	/*
	{
	"n"			: "10",
	"events"		: "100",
	"tmax"		: "60.0",
	"l"			: "10.0",
	"r"			: "0.005",
	"speed"		: "0.1",
	"mass"		: "0.0001",
	"rbig"		: "0.05",
	"massbig"	: "0.1",
	"xbig"		: "5.0",
	"ybig"		: "5.0",
	"inputfile"	: "input1.data",
	"outputfile"	: "output1.data",
	"deltat"		: "0.05"
	
	final int n = 10000;			// Cantidad de partículas
	final long e = 100;				// Cantidad máxima de eventos
	final double tmax = 60.0;		// Tiempo máximo de simulación
	final double l = 10.0;			// Dimensión del espacio
	final double r = 0.005;			// Radio de las partículas
	final double rbig = 0.05;		// Radio distinguido
	final double speed = 0.1;		// Rapidez máxima
	final double mass = 0.0001;		// Masa de las partículas (en Kg.)
	final double massbig = 0.1;		// Masa distinguida (en Kg.)
	//final double Δt = 0.1;		// Intervalo de animación
	}
	 */
	
	public Integer getN() {
		return n;
	}
		
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

	public Double getR() {
		return r;
	}

	public Double getRbig() {
		return rbig;
	}

	public Double getSpeed() {
		return speed;
	}

	public Double getMass() {
		return mass;
	}

	public Double getMassbig() {
		return massbig;
	}

	public Double getDeltat() {
		return deltat;
	}
	
	public Double getXbig() {
		return xbig;
	}

	public Double getYbig() {
		return ybig;
	}
	
	public String getInputfile() {
		return inputfile;
	}

	public String getOutputfile() {
		return outputfile;
	}	

}
