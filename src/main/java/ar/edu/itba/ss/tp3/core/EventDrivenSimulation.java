
	package ar.edu.itba.ss.tp3.core;

	import static java.util.Comparator.comparingDouble;

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

		public EventDrivenSimulation(final Builder builder) {
			System.out.println(
					"Event-Driven Simulation (T-max = " + builder.maxTime +
					" [s], Max. Events = " + builder.maxEvents + ")");
			this.events = new PriorityQueue<>(comparingDouble(Event::getTime));
			this.events.addAll(builder.system.bootstrap());
			this.system = builder.system;
			this.maxEvents = builder.maxEvents;
			this.maxTime = builder.maxTime;
		}

		public EventDrivenSimulation run() {
			final long startTime = System.nanoTime();
			long evolutions = 0;
			double time = 0;
			while (!events.isEmpty()) {
				final Event event = events.poll();
				if (system.isValid(event)) {
					//System.out.println("Time: " + time);
					if (maxEvents < (1 + evolutions)) break;
					if (maxTime < (time + event.getTime())) break;
					++evolutions;
					time += event.getTime();
					final List<? extends Event> nextEvents = system.evolve(event);
					events.addAll(nextEvents);
					System.out.print("\t\tEvolutions: " + evolutions + "\r");
				}
			}
			System.out.println(
				"\n\n\tEnd simulation in " +
				1E-9 * (System.nanoTime() - startTime) + " sec.");
			System.out.println("\t\tTime reached: " + time + " [s]");
			System.out.println("\t\tEvents simulated: " + evolutions + "\n");
			return this;
		}

		public static Builder of(final EventSystem<? extends Event> system) {
			return new Builder(system);
		}

		public static class Builder {

			protected final EventSystem<? extends Event> system;
			protected long maxEvents = 60;
			protected double maxTime = 60;

			public Builder(final EventSystem<? extends Event> system) {
				this.system = system;
			}

			public EventDrivenSimulation run() {
				return new EventDrivenSimulation(this).run();
			}

			public Builder limitedByTime(final double maxTime) {
				this.maxTime = maxTime;
				return this;
			}

			public Builder limitedByEvents(final long maxEvents) {
				this.maxEvents = maxEvents;
				return this;
			}
		}
	}
