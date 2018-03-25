
	package ar.edu.itba.ss.tp3.core;

	import java.util.ArrayList;
	import java.util.List;

	import ar.edu.itba.ss.tp3.core.interfaces.Event;
	import ar.edu.itba.ss.tp3.core.interfaces.EventSystem;

		/**
		* <p>Un colisionador de partículas basado en eventos. En particular,
		* el sistema evoluciona cuando se detecta una nueva colisión entre 2
		* partículas, o entre una partícula y el confinamiento.</p>
		*/

	public class ParticleCollider implements EventSystem<Collision> {

		protected final List<MassiveParticle> particles;
		protected final MassiveGenerator generator;
		protected final int size;

		public ParticleCollider(
				final MassiveGenerator generator, final int size) {
			this.particles = generator.create(size).getParticles();
			this.generator = generator;
			this.size = size;
		}

		public List<Collision> bootstrap() {
			return impendingCollisions();
		}

		@Override
		public List<Collision> evolve(final Event event) {
			final Collision collision = (Collision) event;
			final double time = collision.getTime();
			/**/System.out.println("Evolve to: " + time + " sec.");
			this.particles.replaceAll(p -> p.move(time));

			// Ejecutar colisión:
			final List<Integer> ids = collision.getIDs();
			final List<MassiveParticle> particles = collision.collide();
			for (int i = 0; i < particles.size(); ++i) {
				this.particles.set(ids.get(i), particles.get(i));
			}

			return impendingCollisions();
		}

		protected List<Collision> impendingCollisions() {
			// generar colisiones iniciales
			// para cada partícula
			// verificar si choca contra otra (si pasa, verificar contra la pared?)
			// sino, verificar si choca con la pared (siempre va a pasar esto)
			/*
			* para cada partícula p1 verifico el tiempo de colisión con el resto (p2)
			* deben ser distintas. Si el tiempo no es infinito, genero un evento de colisión
			* colecto todos los eventos de esa partícula en un mapa (p1, optional(p2))
			* o bien, cada colisión posee la partícula y opcionalmente el objetivo
			* tipos de colisión?
			* puedo tener 3 tipos, pero todos devuelven Tc.
			* con este Tc armo un evento que contiene la información necesaria.
			* ...
			* al evolucionar, necesito mover las trayectorias y ejecutar el evento propiamente dicho
			* ejecutarlo implica generar 1 o 2 partículas nuevas (Lista de partículas)
			* reemplazar las viejas (usar índices, no id's, sino no tiene sentido)
			* actualizar la cuenta de colisiones
			* invalidar eventos?
			*/
			return new ArrayList<>();
		}
	}
