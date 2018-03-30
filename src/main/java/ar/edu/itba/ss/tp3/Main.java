	
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
	
	import com.fasterxml.jackson.core.JsonParseException;
	import com.fasterxml.jackson.databind.JsonMappingException;
	
	import ar.edu.itba.ss.core.Particle;
	import ar.edu.itba.ss.tp2.core.MobileParticle;
	import ar.edu.itba.ss.tp3.core.Collision;
	import ar.edu.itba.ss.tp3.core.EventDrivenSimulation;
import ar.edu.itba.ss.tp3.core.Input;
import ar.edu.itba.ss.tp3.core.MassiveGenerator;
	import ar.edu.itba.ss.tp3.core.MassiveParticle;
	import ar.edu.itba.ss.tp3.core.ParticleCollider;
	import ar.edu.itba.ss.tp3.core.StaticGenerator;
import ar.edu.itba.ss.tp3.core.interfaces.Generator;
	
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

			
			final GenerateConfigurator config = new GenerateConfigurator();
			config.load();
			
			Integer n = config.getConfiguration().getN();
			Long events = config.getConfiguration().getEvents();
			Double tmax = config.getConfiguration().getTmax();
			Double l = config.getConfiguration().getL();
			Double r = config.getConfiguration().getR();
			Double rbig = config.getConfiguration().getRbig();
			Double xbig = config.getConfiguration().getXbig();
			Double ybig = config.getConfiguration().getYbig();
			Double speed = config.getConfiguration().getSpeed();
			
			Double mass = config.getConfiguration().getMass();
			Double massbig = config.getConfiguration().getMassbig();
			String inputFilename = config.getConfiguration().getInputfile();
			String outputFilename = config.getConfiguration().getOutputfile();
			
			PrintWriter pw = new PrintWriter(outputFilename);
			
			List<MassiveParticle> particles = new ArrayList<MassiveParticle>();
			MassiveGenerator mg = MassiveGenerator.over(l)
					.withBrownianMotion(xbig, ybig, r, 0, mass) 
					.spy(p -> {
						particles.add(p);
					})
					.radius(r)
					.speed(speed)
					.mass(mass)
					.build();
			
			List<Collision> cols = new ArrayList<Collision>();
			List<List<MassiveParticle>> mps = new ArrayList<List<MassiveParticle>>();
			
			EventDrivenSimulation
				.of(ParticleCollider.of(n)
					.eventSpy((e, ps) -> {
						try {
							cols.add(e);
							mps.add(ps);
							generateOutputFile(e, ps, pw, outputFilename);
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} 
					})
					.from(mg)
					.build())
				.limitedByTime(tmax)
				.limitedByEvents(events)
				.run();
					
			Double cuttingTime = cols.get(cols.size()-1).getTime() / 3;
			
			PrintWriter pwSpeed1 = new PrintWriter("speed1.txt");
			PrintWriter pwSpeed2 = new PrintWriter("speed2.txt");
			PrintWriter pwSpeed3 = new PrintWriter("speed3.txt");
			
			PrintWriter collisionsFrequency = new PrintWriter("collisionsFrequency.txt");
			
			
			
			for (int i = 0; i < cols.size(); i++) {
				calculateFrequency(cols.get(i), collisionsFrequency, "collisionsFrequency.txt");
				
				if (cols.get(i).getTime() < cuttingTime) {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed1, "speed1.txt");
				} else if (cols.get(i).getTime() < cuttingTime * 2) {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed2, "speed2.txt");
				} else {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed3, "speed3.txt");
				}
				
			}
			
			// Generate input file
			generateInputFile(particles, n, inputFilename);
	
		}
		
		
		private static void simulateMode() throws JsonParseException, JsonMappingException, IOException {
						
			final SimulateConfigurator config = new SimulateConfigurator();
			config.load();
			
			Long events = config.getConfiguration().getEvents();
			Double tmax = config.getConfiguration().getTmax();
			Double l = config.getConfiguration().getL();
			String inputFilename = config.getConfiguration().getInputfile();
			String outputFilename = config.getConfiguration().getOutputfile();
			
			// LAS PARTICULAS LAS LEO DEL INPUT FILE QUE HICE
			Input in = new Input(inputFilename);
			List<MassiveParticle> particles = new ArrayList<>();
			final Generator generator = StaticGenerator.from(particles).over(l).build();
			
			
			PrintWriter pw = new PrintWriter(outputFilename);
						
			List<Collision> cols = new ArrayList<Collision>();
			List<List<MassiveParticle>> mps = new ArrayList<List<MassiveParticle>>();
			
			EventDrivenSimulation
				.of(ParticleCollider.of(particles.size()) 
					.eventSpy((e, ps) -> {
						try {
							cols.add(e);
							mps.add(ps);
							generateOutputFile(e, ps, pw, outputFilename);
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} 
					})
					.from(generator)
					.build())
				.limitedByTime(tmax)
				.limitedByEvents(events)
				.run();
			
			Double cuttingTime = cols.get(cols.size()-1).getTime() / 3;
			
			PrintWriter pwSpeed1 = new PrintWriter("speed1.txt");
			PrintWriter pwSpeed2 = new PrintWriter("speed2.txt");
			PrintWriter pwSpeed3 = new PrintWriter("speed3.txt");
			
			PrintWriter collisionsFrequency = new PrintWriter("collisionsFrequency.txt");
			
			
			
			for (int i = 0; i < cols.size(); i++) {
				calculateFrequency(cols.get(i), collisionsFrequency, "collisionsFrequency.txt");
				
				if (cols.get(i).getTime() < cuttingTime) {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed1, "speed1.txt");
				} else if (cols.get(i).getTime() < cuttingTime * 2) {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed2, "speed2.txt");
				} else {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed3, "speed3.txt");
				}
				
			}
						
		} 
		
		private static void animateMode() {
			
		}
		
		private static void calculateFrequency(Collision col, PrintWriter pw, final String input_filename) {			
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(input_filename, true)))) {
				out.write(((Double)col.getTime()).toString() + "\n");
			}catch (IOException e) {
			    e.printStackTrace();
			}
		}
		
		private static void calculateSpeed(Collision col, List<MassiveParticle> particles, PrintWriter pw, final String input_filename) {
			List<Double> speedModules = new ArrayList<Double>();
			
			
			for (MassiveParticle p: particles) {
				speedModules.add(Math.sqrt(Math.pow(p.getVx(), 2) + Math.pow(p.getVy(), 2)));
			}
			
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(input_filename, true)))) {
				for(Double speed: speedModules){
					out.write(speed.toString() + "\n");
				}
			}catch (IOException e) {
			    e.printStackTrace();
			}
			
		}
		
		private static void calculateDiffusion() {
			
		}
		
		// wrong parameters, need to change them
		private static void generateOutputFile(Collision event, List<MassiveParticle> particles, PrintWriter pw, final String output_filename) throws FileNotFoundException {
			
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output_filename, true)))) {
								
				// Do something similar to the SPEED MODE but for the FREQUENCY MODE 
				
				// LINE 1
				List<String> ids = new ArrayList<String>();
				for (Integer id : event.getIDs()) {
					ids.add(id.toString() + " ");
				}
				out.write(event.getTime() + " " + ids + "\n"); 
				
				//LINE 2
				for(MassiveParticle p: particles){
					out.write(p.getX() + " " +  p.getY() + " " + p.getVx() + " " + p.getVy() + "\n");
				}
				
			}catch (IOException e) {
			    e.printStackTrace();
			}
			
		}
		
		private static void generateAnimatedFile(List<MassiveParticle> particles, int cycle) {
			if(cycle == 0){
				try{
					PrintWriter pw = new PrintWriter("animatedFile.data");
					pw.close();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("animatedFile.data", true)))) {
				out.write(String.valueOf(particles.size()) + "\n"); // N
				out.write(String.valueOf(cycle) + "\n");			  // ciclo

				// position_x position_y
				for(MobileParticle p: particles){
					out.write(p.getX() + " " +  p.getY() + "\n");
				}
			}catch (IOException e) {
			    e.printStackTrace();
			}
		}
		
		
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
