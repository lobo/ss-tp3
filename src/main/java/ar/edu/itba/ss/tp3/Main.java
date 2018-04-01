	
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

import com.fasterxml.jackson.core.JsonParseException;
	import com.fasterxml.jackson.databind.JsonMappingException;

import ar.edu.itba.ss.core.Particle;
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
		private static final String ANIMATED_FILE = "./resources/data/animatedFile.data";
		private static final String DIFFUSION_FILE_DISTINGUISHED = "./resources/data/diffusion-distinguished.txt";
		private static final String DIFFUSION_FILE_SINGLE = "./resources/data/diffusion-single.txt";
		private static final String DIFFUSION_FILE_TOTAL = "./resources/data/diffusion-total.txt";
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
			//Double deltat = config.getConfiguration().getDeltat();
			
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
			generateInputFile(particles, n, inputFilename);
	
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
			
			Double cuttingTime = (cols.get(cols.size()-1).getTime()+cols.get(cols.size()-1).getBaseTime()) / 3;
				
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
			
			animateMode(particles, cols, mps, deltat);
						
		} 
		
		private static void animateMode(List<MassiveParticle> particles, List<Collision> cols, List<List<MassiveParticle>> mps, Double deltat) throws FileNotFoundException {
			PrintWriter pwAnimated = new PrintWriter(ANIMATED_FILE);
			PrintWriter pwDiffusionDistinguished = new PrintWriter(DIFFUSION_FILE_DISTINGUISHED);
			PrintWriter pwDiffusionSingle = new PrintWriter(DIFFUSION_FILE_DISTINGUISHED);
						
			double xt = 0.0;
			double yt = 0.0;
			
			// primera parte: usa el input file
			for (double t1 = 0.0; t1 < cols.get(0).getTime(); t1+= deltat) {
				for (int j = 0; j < particles.size(); j++) {
					MassiveParticle p = particles.get(j);
					xt = p.getX() + p.getVx() * deltat;
					yt = p.getY() + p.getVy() * deltat;
					
					if (j == 0) { // it's the distinguished particle
						logDiffusion(t1, xt, yt, pwDiffusionDistinguished, DIFFUSION_FILE_DISTINGUISHED);
					}
					if (j == 0) { // ACA VA EL j DE LA PARTICULA SELECCIONADA
						logDiffusion(t1, xt, yt, pwDiffusionSingle, DIFFUSION_FILE_SINGLE);
					}
					
					generateAnimatedFile(particles.size(), t1, xt, yt, pwAnimated, ANIMATED_FILE);
				}
			}
			
			// segunda parte
			for (int k = 1; k < cols.size(); k++) {
				for (double t1 = 0.0; t1 < cols.get(k).getTime() - cols.get(k-1).getTime(); t1+= deltat) {
					for (int j = 0; j < mps.get(k-1).size(); j++) {
						MassiveParticle p = particles.get(j);
						xt = xt + p.getX() + p.getVx() * deltat;
						yt = yt + p.getY() + p.getVy() * deltat;
						
						if (j == 0) { // it's the distinguished particle
							logDiffusion(t1, xt, yt, pwDiffusionDistinguished, DIFFUSION_FILE_DISTINGUISHED);
						}
						if (j == 0) { // ACA VA EL j DE LA PARTICULA SELECCIONADA
							logDiffusion(t1, xt, yt, pwDiffusionSingle, DIFFUSION_FILE_SINGLE);
						}
						
						generateAnimatedFile(mps.get(k-1).size(), t1, xt, yt, pwAnimated, ANIMATED_FILE);
					}
				}
			}
			
			calculateDiffusion(DIFFUSION_FILE_DISTINGUISHED, DIFFUSION_FILE_SINGLE);

			/*
			 * x(t) = x0 + vx*t
			 * y(t) = y0 + vy*t 
			 * 
			 * 0 < t < cols.get(cols.size() - 1).getTime()
			 * usando ese t saco 4 valores
			 * 
			 * t0 = input-file
				t1 (hasta evento1) =
				x(t) = x(t - 1) + deltat * vx(t)
				y(t) = y(t - 1) + deltat * vy(t)
				
				t2 (hasta evento2) =
				x(t - 1) = lo que te quedo de antes
				vx(t) = es del evento1
				x(t) = x(t - 1) + deltat * vx(t)
				y(t) = y(t - 1) + deltat * vy(t)
			 * 
			 * 
			 * try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("animatedFile.data", true)))) {
				out.write(String.valueOf(mps.size()) + "\n"); 
				out.write(String.valueOf(cycle) + "\n");			  // ciclo

				// position_x position_y
				for(MobileParticle p: particles){
					out.write(p.getX() + " " +  p.getY() + "\n");
				}
			}catch (IOException e) {
			    e.printStackTrace();
			}
			 */
			
			
			
						
		}
		
		
		
		
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
		
		private static void generateAnimatedFile(final Integer n, final Double t, Double xt, Double yt, final PrintWriter pw, final String animatedFilename) {
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(animatedFilename, true)))) {
				out.write(n.toString() + "\n");
				out.write(t.toString() + "\n");
				out.write(xt.toString() + " " + yt.toString() + "\n");
			}catch (IOException e) {
			    e.printStackTrace();
			}
		}
				
		private static void logDiffusion(Double eventTime, Double xt, Double yt, PrintWriter pw, final String input_filename) {
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(input_filename, true)))) {
				
				Double z = Math.pow(Math.sqrt(Math.pow(xt, 2) + Math.pow(yt, 2)), 2); // calculate z^2
				out.write(eventTime.toString() + " " + z.toString()); // log in this format: event_time z
				
			}catch (IOException e) {
			    e.printStackTrace();
			}
		}
		
		private static void calculateDiffusion(final String diffusionDistinguishedFilepath, final String diffusionSingleFilepath) {
			File distFilepath = new File(diffusionDistinguishedFilepath);
			File singleFilepath = new File(diffusionSingleFilepath);
			
			try { 
				double radius;
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
				
				Double averageDistinguished = calculateAverage(distinguishedZs);
				Double averageSingle = calculateAverage(singleZs);
				
				System.out.println("Z of the distinguished particle is: " + averageDistinguished);
				System.out.println("z of the selected particle is: " + averageSingle);
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
			
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(output_filename, true)))) {			
				// LINE 1
				StringBuilder ids = new StringBuilder();
				for (Integer id : event.getIDs()) {
				    ids.append(id.toString() + " ");
				}
				String totalIds = ids.toString();
				
				// Línea mágica que arregla todo el bodrio de una:
				final double eventTime = event.getBaseTime() + event.getTime();
				out.write(eventTime + " " + totalIds + "\n"); 
				
				//LINE 2
				for(MassiveParticle p: particles){
					out.write(p.getX() + " " +  p.getY() + " " + p.getVx() + " " + p.getVy() + "\n");
				}
				
			}catch (IOException e) {
			    e.printStackTrace();
			}
			
		}

		
		private static void generateInputFile(final List<MassiveParticle> particles, final int N, final String input_filename) throws FileNotFoundException {
			System.out.println("The output has been written into a file:" + input_filename);
			final String filename = input_filename;
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