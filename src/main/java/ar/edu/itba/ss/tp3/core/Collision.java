
	package ar.edu.itba.ss.tp3.core;

	import java.util.Arrays;
	import java.util.List;

	import ar.edu.itba.ss.tp2.core.Pair;
	import ar.edu.itba.ss.tp3.core.interfaces.Event;

		/**
		* <p>Representa una colisión entre dos objetos rígidos, es decir,
		* entre dos partículas o entre una partícula y el recipiente que la
		* contiene.</p>
		*/

	public class Collision implements Event { // Hacer un builder!!!

		protected final List<Long> collisions;
		protected final List<Integer> ids;
		protected final CollisionType type;
		protected final double time;

		public Collision(
				final CollisionType type,
				final double time,
				final Long [] collisions,
				final Integer ... ids) {
			this.collisions = Arrays.asList(collisions);
			this.ids = Arrays.asList(ids);
			this.type = type;
			this.time = time;
		}

		@Override
		public double getTime() {
			return time;
		}

		public List<Long> getCollisions() {
			return collisions;
		}

		public List<Integer> getIDs() {
			return ids;
		}

		public CollisionType getType() {
			return type;
		}

		public List<MassiveParticle> collide(
				final List<MassiveParticle> particles) {
			switch (type) {
				case OVER_HORIZONTAL_WALL: {
					particles.set(0, particles.get(0).horizontalCollide());
					break;
				}
				case OVER_VERTICAL_WALL: {
					particles.set(0, particles.get(0).verticalCollide());
					break;
				}
				case OVER_PARTICLE: {
					final Pair<MassiveParticle> pair = particles
							.get(0)
							.collide(particles.get(1));
					particles.set(0, pair.getLeft());
					particles.set(1, pair.getRight());
					break;
				}
				default: {
					throw new IllegalStateException(
						"El tipo de colisión es inválido.");
				}
			}
			return particles;
		}
	}
