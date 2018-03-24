
	package ar.edu.itba.ss.tp3.core.interfaces;

	import java.util.List;

		/**
		* <p>Un sistema de eventos permite evolucionar el estado frente a
		* eventos relevantes, y no de forma continua, lo cual permite
		* incrementar la performance de las simulaciones.</p>
		*/

	public interface EventSystem<T extends Event> {

		public List<T> bootstrap();
		public List<T> evolve(final double time);
	}
