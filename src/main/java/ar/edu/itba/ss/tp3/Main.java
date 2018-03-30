	
	package ar.edu.itba.ss.tp3;
	
	import java.io.BufferedWriter;
import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
	import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
	import java.util.Map;
	
	import com.fasterxml.jackson.core.JsonParseException;
	import com.fasterxml.jackson.databind.JsonMappingException;
	
	import ar.edu.itba.ss.core.Particle;
import ar.edu.itba.ss.tp2.core.MobileParticle;
import ar.edu.itba.ss.tp3.core.EventDrivenSimulation;
	import ar.edu.itba.ss.tp3.core.MassiveGenerator;
	import ar.edu.itba.ss.tp3.core.MassiveParticle;
	import ar.edu.itba.ss.tp3.core.ParticleCollider;
	
			/**
		* <p>Punto de entrada principal de la simulación. Se encarga de
		* configurar los parámetros de operación y de desplegar el
		* sistema requerido.</p>
		*/
	
	public final class Main {
		
		private static final String HELP_TEXT = "Brownian Motion.\n" +
				"Possible modes: \n" + 
				"generate\n" +
				"simulate\n" +
				"animate\n";
	
		enum EXIT_CODE {
			NO_ARGS(-1), 
			BAD_N_ARGUMENTS(-2),
			BAD_ARGUMENT(-3);
			
			private final int code;
		
			EXIT_CODE(final int code) {
				this.code = code;
			}
		
			public int getCode() {
				return code;
			}
		}
		
		private static void exit(final EXIT_CODE exitCode) {
			System.exit(exitCode.getCode());
		}
	
		public static void main(final String [] arguments) throws JsonParseException, JsonMappingException, IOException {
			
			if (arguments.length == 0) {
				System.out.println("[FAIL] - No arguments passed. Try 'help' for more information.");
				exit(EXIT_CODE.NO_ARGS);
			} else if (arguments.length != 1) {
				System.out.println("[FAIL] - Wrong number of arguments. Try 'help' for more information.");
				exit(EXIT_CODE.BAD_N_ARGUMENTS);
			}
					
			if (arguments[0].equals("help")) {
				System.out.println(HELP_TEXT);
			} else {
				final long start = System.nanoTime();
				
				switch (arguments[0]) {
					case "help":
						System.out.println(HELP_TEXT);
						break;
					case "generate":
						generateMode();
						break;
					case "simulate":
						simulateMode();
						break;
					case "animate":
						animateMode();
						break;
					default:
						System.out.println("[FAIL] - Invalid argument. Try 'help' for more information.");
						exit(EXIT_CODE.BAD_ARGUMENT);
						break;
				}
			}
			
		}
		
		private static void generateMode() throws JsonParseException, JsonMappingException, IOException {
			Integer n = 10000;			// Cantidad de partículas
			long events = 100;				// Cantidad máxima de eventos
			double tmax = 60.0;		// Tiempo máximo de simulación
			double l = 10.0;			// Dimensión del espacio
			double r = 0.005;			// Radio de las partículas
			double rbig = 0.05;		// Radio distinguido
			double speed = 0.1;		// Rapidez máxima
			double mass = 0.0001;		// Masa de las partículas (en Kg.)
			double massbig = 0.1;		// Masa distinguida (en Kg.)
			//final double T = 300.0;		// Temperatura del sistema
			//final double Δt = 0.1;		// Intervalo de animación
			
			final Configurator config = new Configurator();
			config.load();
			
			n = config.getConfiguration().getN();
			events = config.getConfiguration().getEvents();
			tmax = config.getConfiguration().getTmax();
			l = config.getConfiguration().getL();
			r = config.getConfiguration().getR();
			rbig = config.getConfiguration().getRbig();
			speed = config.getConfiguration().getSpeed();
			mass = config.getConfiguration().getMass();
			massbig = config.getConfiguration().getMassbig();
			String inputFilename = config.getConfiguration().getInputfile(); 
			
			/*
			EventDrivenSimulation
				.of(ParticleCollider.of(n)
					.from(MassiveGenerator.over(l)
						.withBrownianMotion(rbig, 0, massbig) // ESTE EJEMPLO NO VA MAS
						.radius(r)
						.speed(speed)
						.mass(mass)
						.build())
					.build())
				.limitedByTime(tmax)
				.limitedByEvents(events)
				.run(); */
			
			List<MassiveParticle> particles = new ArrayList<MassiveParticle>();
			generateInputFile(particles, n, inputFilename);
		}
		
		private static void simulateMode() {
			
		}
		
		private static void animateMode() {
			
		}
		
		// wrong parameters, need to change them
		private static void generateOutputFile(final List<MassiveParticle> particles, final long start, final String output_filename, final String L, final String Rc) throws FileNotFoundException {
			System.out.println("The output has been written into a file.");
			final String filename = "./" + output_filename + ".txt";
			File file = new File(filename);
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setOut(ps);
			
			particles.forEach((particle) -> {
				System.out.println( 
						// <event-time-1> <id>
						particle.getX() + " " + 
						particle.getY() + " " +
						particle.getVx() + " " +
						particle.getVy() + " "
						);
			});
		}
		
		private static void generateAnimatedFile(List<MassiveParticle> list, int cycle) {
			if(cycle == 0){
				try{
					PrintWriter pw = new PrintWriter("animatedFile.data");
					pw.close();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("animatedFile.data", true)))) {
				out.write(String.valueOf(list.size()) + "\n");
				out.write(String.valueOf(cycle) + "\n");
				for(MobileParticle p: list){
					out.write(p.getX() + " " +  p.getY() + "\n");
				}
			}catch (IOException e) {
			    e.printStackTrace();
			}
		}
		
		
		// CHECKED - wrong parameters, need to change them
		private static void generateInputFile(final List<MassiveParticle> particles, final int N, final String input_filename) throws FileNotFoundException {
			System.out.println("The output has been written into a file.");
			final String filename = "./" + input_filename + ".txt";
			File file = new File(filename);
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			System.setOut(ps);
			
			System.out.println(N);
			particles.forEach((particle) -> {
				System.out.println( 
						particle.getX() + " " + 
						particle.getY() + " " +
						particle.getRadius() + " " +
						particle.getVx() + " " +
						particle.getVy() + " " +
						particle.getMass() + " "
						);
			});
		}
	
		
	
	}
