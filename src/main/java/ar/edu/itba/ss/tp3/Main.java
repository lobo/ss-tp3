	
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
	import java.util.Scanner;
	import static java.util.stream.Collectors.toList;

	import com.fasterxml.jackson.core.JsonParseException;
	import com.fasterxml.jackson.databind.JsonMappingException;

	import ar.edu.itba.ss.tp3.core.Collision;
	import ar.edu.itba.ss.tp3.core.EventDrivenSimulation;
	import ar.edu.itba.ss.tp3.core.InputMassiveParticle;
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
		
		private static final String COLLISIONS_FILE = "./resources/data/collisions.txt";
		private static final String ANIMATED_FILE = "./resources/data/animation.txt";
		//private static final String DIFFUSION_FILE_DISTINGUISHED = "./resources/data/diffusion-distinguished.txt";
		//private static final String DIFFUSION_FILE_SINGLE = "./resources/data/diffusion-single.txt";
		//private static final String DIFFUSION_FILE_TOTAL = "./resources/data/diffusion-total.txt";
		private static final String SPEED_FILE = "./resources/data/speed";
		
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
				//final long start = System.nanoTime();
				
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
					/*	
					case "animate":
						animateMode();
						break;*/
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
			Double deltat = config.getConfiguration().getDeltat();
			Double mass = config.getConfiguration().getMass();
			Double massbig = config.getConfiguration().getMassbig();
			String inputFilename = "./resources/data/" + config.getConfiguration().getInputfile().toString();
			String outputFilename = "./resources/data/" + config.getConfiguration().getOutputfile().toString();
			
			PrintWriter pw = new PrintWriter(outputFilename);
			
			List<MassiveParticle> particles = new ArrayList<MassiveParticle>();
			MassiveGenerator mg = MassiveGenerator.over(l)
					.withBrownianMotion(xbig, ybig, rbig, 0, massbig) 
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
							mps.add(ps.stream().collect(toList()));
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
					
			// Add baseTime:
			Double cuttingTime = (cols.get(cols.size()-1).getTime()+cols.get(cols.size()-1).getBaseTime()) / 3;
			
			PrintWriter pwSpeed1 = new PrintWriter(SPEED_FILE + "1.txt");
			PrintWriter pwSpeed2 = new PrintWriter(SPEED_FILE + "2.txt");
			PrintWriter pwSpeed3 = new PrintWriter(SPEED_FILE + "3.txt");
			
			PrintWriter collisionsFrequency = new PrintWriter(COLLISIONS_FILE);
			
			for (int i = 0; i < cols.size(); i++) {
				calculateFrequency(cols.get(i), collisionsFrequency, COLLISIONS_FILE);
				
				final double eventTime = cols.get(i).getBaseTime() + cols.get(i).getTime();
				if (eventTime < cuttingTime) {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed1, SPEED_FILE + "1.txt");
				} else if (eventTime < cuttingTime * 2) {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed2, SPEED_FILE + "2.txt");
				} else {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed3, SPEED_FILE + "3.txt");
				}
				
			}
			
			// Generate input file
			generateInputFile(particles, particles.size(), inputFilename);
			animation(deltat, l, cols, mps);
		}

		private static void simulateMode() throws JsonParseException, JsonMappingException, IOException {
						
			final SimulateConfigurator config = new SimulateConfigurator();
			config.load();
			
			Long events = config.getConfiguration().getEvents();
			Double tmax = config.getConfiguration().getTmax();
			Double l = config.getConfiguration().getL();
			String inputFilename = "./resources/data/" + config.getConfiguration().getInputfile().toString();
			String outputFilename = "./resources/data/" + config.getConfiguration().getOutputfile().toString();
			Double deltat = config.getConfiguration().getDeltat();
			
			InputMassiveParticle in = new InputMassiveParticle(inputFilename);
			List<MassiveParticle> particles = in.getParticles();
			final Generator generator = StaticGenerator.from(particles).over(l).build();
			
			
			PrintWriter pw = new PrintWriter(outputFilename);
						
			List<Collision> cols = new ArrayList<Collision>();
			List<List<MassiveParticle>> mps = new ArrayList<List<MassiveParticle>>();

			EventDrivenSimulation
				.of(ParticleCollider.of(particles.size()) 
					.eventSpy((e, ps) -> {
						try {
							cols.add(e);
							mps.add(ps.stream().collect(toList()));
							/*ps.stream().forEachOrdered(p -> {
								System.out.println(p.getX() + " " + p.getY());
							});*/
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
			
			Double cuttingTime = (cols.get(cols.size()-1).getTime() + cols.get(cols.size()-1).getBaseTime()) / 3;
				
			PrintWriter pwSpeed1 = new PrintWriter(SPEED_FILE + "1.txt");
			PrintWriter pwSpeed2 = new PrintWriter(SPEED_FILE + "2.txt");
			PrintWriter pwSpeed3 = new PrintWriter(SPEED_FILE + "3.txt");
			
			PrintWriter collisionsFrequency = new PrintWriter(COLLISIONS_FILE);
			
			
			/*
			 * ¿Es necesario imprimir todo o solamente un evento en particular?
			 * Es decir, solo 3 eventos (1er tercio, 2do y último).
			 */
			for (int i = 0; i < cols.size(); i++) {
				calculateFrequency(cols.get(i), collisionsFrequency, COLLISIONS_FILE);
				
				// Corrección del tiempo de corte:
				final double eventTime = cols.get(i).getBaseTime() + cols.get(i).getTime();
				if (eventTime < cuttingTime) {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed1, SPEED_FILE + "1.txt");
				} else if (eventTime < cuttingTime * 2) {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed2, SPEED_FILE + "2.txt");
				} else {
					calculateSpeed(cols.get(i), mps.get(i), pwSpeed3, SPEED_FILE + "3.txt");
				}
				
			}
			animation(deltat, l, cols, mps);
		}

		/* *******************************************************************/

		protected static void animation(
				final double Δt, final double L,
				final List<Collision> collisions,
				final List<List<MassiveParticle>> state) {

			if (collisions.size() < 2) {
				System.out.println("Debe haber al menos 2 eventos para animar.");
				return;
			}
			try (final PrintWriter writer =
					new PrintWriter(new FileWriter(ANIMATED_FILE))) {

				double t = 0;
				final int N = state.get(0).size() + 2;
				for (int i = 0; i < collisions.size() - 1; ++i) {
					final Collision nextCollision = collisions.get(i + 1);
					final double limitTime = nextCollision.getBaseTime() + nextCollision.getTime();
					final List<MassiveParticle> particles = state.get(i);
					for (final double base = t; t < limitTime; t += (2.0 * Δt)) {
						final double Δ = t - base;
						writer.println(N);
						writer.println(t);
						particles.stream()
							.map(p -> p.move(Δ))
							.forEachOrdered(p -> {
								writer.println(
									p.getX() + " " + p.getY() + " " +
									p.getRadius() + " " + p.getSpeed());
							});
						writer.println("0.0 0.0 0.001 0.0");
						writer.println(L + " " + L + " 0.001 0.0");
					}
				}
				System.out.println(
					"El archivo de animación (a " +
					Math.round(1/Δt) + " FPS) fue creado con éxito.");
			}
			catch (final IOException exception) {
				System.out.println(
					"No se pudo generar el archivo de animación.");
			}
		}

		/* *******************************************************************/

		private static void calculateFrequency(Collision col, PrintWriter pw, final String input_filename) {			
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(input_filename, true)))) {
				// Agrego el 'base-time':
				out.write(((Double)(col.getTime()+col.getBaseTime())).toString() + "\n");
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
				
		protected static void calculateDiffusion(final String diffusionDistinguishedFilepath, final String diffusionSingleFilepath, final String finalPathfile) {
			File distFilepath = new File(diffusionDistinguishedFilepath);
			File singleFilepath = new File(diffusionSingleFilepath);
			
			try { 

				// parse both files:
				Scanner distRead = new Scanner(distFilepath);
				Scanner singleRead = new Scanner(singleFilepath);
				
				List<Double> distinguishedZs = new ArrayList<Double>();
				List<Double> singleZs = new ArrayList<Double>();
			
				while(distRead.hasNext() && singleRead.hasNext()){
					distRead.next(); // avoid first column
					singleRead.next(); // avoid first column
					
					distinguishedZs.add(Double.parseDouble(distRead.next()));
					singleZs.add(Double.parseDouble(singleRead.next()));
				}
				
				distRead.close();
				singleRead.close();
				
				// calculate averages:
				Double averageDistinguished = calculateAverage(distinguishedZs);
				Double averageSingle = calculateAverage(singleZs);
				
				
				// log everything in a 3rd file:
				File file = new File(finalPathfile);
				FileOutputStream fos = new FileOutputStream(file);
				PrintStream ps = new PrintStream(fos);
				System.setOut(ps);
				
				System.out.println("Z of the distinguished particle is: " + averageDistinguished + "\n");
				System.out.println("z of the selected particle is: " + averageSingle + "\n");
				System.out.println("Z / z is: " + (averageDistinguished/averageSingle));
				
			} catch (Exception e) {
				System.out.println("Error scanning file");
			}
			
			
		}
		
		private static Double calculateAverage(List <Double> zs) {
			Double sum = 0.0;
			if(!zs.isEmpty()) {
				for (Double mark : zs) {
					sum += mark;
			    }
				return sum.doubleValue() / zs.size();
			}
			return sum;
		}
		
		// wrong parameters, need to change them
		private static void generateOutputFile(Collision event, List<MassiveParticle> particles, PrintWriter pw, final String output_filename) throws FileNotFoundException {
			
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output_filename, true)))) {

				// Línea mágica que arregla todo el bodrio de una:
				final double eventTime = event.getBaseTime() + event.getTime();
				out.write(String.valueOf(eventTime));
				for (int id : event.getIDs()) {
					if (id != -1) out.write(" " + id);
				}
				out.write("\n"); 

				//LINE 2
				for(MassiveParticle p: particles){
					out.write(p.getX() + " " +  p.getY() + " " + p.getVx() + " " + p.getVy() + "\n");
				}
				
			}catch (IOException e) {
			    e.printStackTrace();
			}
			
		}

		private static void generateInputFile(final List<MassiveParticle> particles, final int N, final String input_filename) throws FileNotFoundException {
			System.out.println("The output has been written into a file: " + input_filename);
			final String filename = input_filename;
			File file = new File(filename);
			FileOutputStream fos = new FileOutputStream(file);
			PrintStream ps = new PrintStream(fos);
			
			// WTF?
			PrintStream oldOut = System.out;
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
			System.setOut(oldOut);
		}
		
	}