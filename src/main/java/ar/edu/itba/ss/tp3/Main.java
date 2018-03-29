
	package ar.edu.itba.ss.tp3;

	import java.io.IOException;

	import com.fasterxml.jackson.core.JsonParseException;
	import com.fasterxml.jackson.databind.JsonMappingException;

	import ar.edu.itba.ss.tp3.core.EventDrivenSimulation;
	import ar.edu.itba.ss.tp3.core.MassiveGenerator;
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
						break;
					case "simulate":
						break;
					case "animate":
						break;
					default:
						System.out.println("[FAIL] - Invalid argument. Try 'help' for more information.");
						exit(EXIT_CODE.BAD_ARGUMENT);
						break;
				}
			}
			
		}
		
		private static void generateMode() throws JsonParseException, JsonMappingException, IOException {
			final int N = 10000;			// Cantidad de partículas
			final long E = 100;				// Cantidad máxima de eventos
			final double tMax = 60.0;		// Tiempo máximo de simulación
			final double L = 10.0;			// Dimensión del espacio
			final double R = 0.005;			// Radio de las partículas
			final double RBig = 0.05;		// Radio distinguido
			final double speed = 0.1;		// Rapidez máxima
			final double mass = 0.0001;		// Masa de las partículas (en Kg.)
			final double massBig = 0.1;		// Masa distinguida (en Kg.)
			//final double T = 300.0;		// Temperatura del sistema
			//final double Δt = 0.1;		// Intervalo de animación
			
			final Configurator config = new Configurator();
			config.load();
			final Long hola = config.getConfiguration().getEvents();
			System.out.println(hola);
			/*
			EventDrivenSimulation
				.of(ParticleCollider.of(N)
					.from(MassiveGenerator.over(L)
						.withBrownianMotion(RBig, 0, massBig)
						.radius(R)
						.speed(speed)
						.mass(mass)
						.build())
					.build())
				.limitedByTime(tMax)
				.limitedByEvents(E)
				.run();*/
		}
		
		private static void simulateMode() {
			
		}
		
		private static void animateMode() {
			
		}

		

	}
