
	package ar.edu.itba.ss.tp3;

	import ar.edu.itba.ss.tp3.core.EventDrivenSimulation;
	import ar.edu.itba.ss.tp3.core.MassiveGenerator;
	import ar.edu.itba.ss.tp3.core.ParticleCollider;

		/**
		* <p>Punto de entrada principal de la simulación. Se encarga de
		* configurar los parámetros de operación y de desplegar el
		* sistema requerido.</p>
		*/

	public final class Main {

		public static void main(final String [] arguments) {

			System.out.println("A brownian motion...");

			final int N = 10;				// Cantidad de partículas
			final long E = 100;				// Cantidad máxima de eventos
			final double tMax = 60.0;		// Tiempo máximo de simulación
			final double L = 5.0;			// Dimensión del espacio
			final double R = 0.005;			// Radio de las partículas
			final double RBig = 0.05;		// Radio distinguido
			final double speed = 0.1;		// Rapidez máxima
			final double mass = 0.0001;		// Masa de las partículas
			final double massBig = 0.1;		// Masa distinguida
			//final double T = 300.0;		// Temperatura del sistema
			//final double Δt = 0.1;		// Intervalo de animación

			new EventDrivenSimulation(
				new ParticleCollider(
					new MassiveGenerator(L, R, speed, mass)
						.withBrownianMotion(RBig, 0, massBig), N),
					E, tMax)
						.run();
		}
	}
