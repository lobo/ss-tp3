
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

		public static void main(final String [] arguments) throws JsonParseException, JsonMappingException, IOException {

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
	}
