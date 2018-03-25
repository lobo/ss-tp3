
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

	public class Collision implements Event {

		protected final List<Integer> ids;
		protected final List<MassiveParticle> particles;
		protected final CollisionType type;

		public Collision(
				final Integer [] ids,
				final MassiveParticle ... particles) {
			this.ids = Arrays.asList(ids);
			this.particles = Arrays.asList(particles);
			this.type = CollisionType.OVER_PARTICLE;
		}

		public List<Integer> getIDs() {
			return ids;
		}

		public List<MassiveParticle> collide() {
			switch (type) {
				case OVER_HORIZONTAL_WALL: {
					particles.replaceAll(MassiveParticle::horizontalCollide);
					break;
				}
				case OVER_VERTICAL_WALL: {
					particles.replaceAll(MassiveParticle::verticalCollide);
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

		@Override
		public boolean isInvalid() {
			return false;
		}

		@Override
		public double getTime() {
			return 0;
		}
	}
