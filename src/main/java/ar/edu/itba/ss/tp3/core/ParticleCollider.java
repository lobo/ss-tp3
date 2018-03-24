
	package ar.edu.itba.ss.tp3.core;

	import java.util.ArrayList;
	import java.util.List;

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
			// generar colisiones iniciales
			// para cada partícula
			// sino, verificar si choca contra otra (si pasa, verificar contra la pared?)
			// verificar si choca con la pared (siempre va a pasar esto)
			return new ArrayList<>();
		}

		@Override
		public List<Collision> evolve(final double time) {
			// avanzar hacia el tiempo determinado
			// ejecutar colisión
			// actualizar velocidades
			// determinar próximas colisiones
			return new ArrayList<>();
		}
	}
