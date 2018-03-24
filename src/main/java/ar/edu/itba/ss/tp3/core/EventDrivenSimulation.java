
	package ar.edu.itba.ss.tp3.core;

	import java.util.List;
	import java.util.PriorityQueue;

	import ar.edu.itba.ss.tp3.core.interfaces.Event;
	import ar.edu.itba.ss.tp3.core.interfaces.EventSystem;

		/**
		* <p>Permite implementar una simulación basada en eventos. El motor
		* interno es completamente genérico e independiente del sistema
		* modelado.</p>
		*/

	public class EventDrivenSimulation {

		protected final PriorityQueue<Event> events;
		protected final EventSystem<? extends Event> system;
		protected final long maxEvents;
		protected final double maxTime;

		public EventDrivenSimulation(
				final EventSystem<? extends Event> system,
				final long maxEvents, final double maxTime) {
			this.events = new PriorityQueue<>();
			this.system = system;
			this.events.addAll(system.bootstrap());
			this.maxEvents = maxEvents;
			this.maxTime = maxTime;
		}

		public void run() {
			long evolutions = 0;
			while (!events.isEmpty()) {
				final Event event = events.poll();
				if (event.isInvalid()) {}
				else {
					final double time = event.getTime();
					if (maxEvents < ++evolutions) break;
					if (maxTime < time) break;
					final List<? extends Event> nextEvents = system.evolve(time);
					events.addAll(nextEvents);
				}
			}
		}
	}