
	package ar.edu.itba.ss.tp3.core.interfaces;

		/**
		* <p>Representa un evento relevante dentro de una simulación guiada
		* por eventos. El modelo simulado avanza únicamente bajo la presencia
		* de un evento.</p>
		*/

	public interface Event {

		public boolean isInvalid();
		public double getTime();
	}
