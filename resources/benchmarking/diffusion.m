
	%{
		Plotea el desplazamiento cuadrático medio de una partícula utilizando
		varias muestras de animación con el siguiente formato:

			<N>
			<t0>
			<x> <y>
			...

		El 'id' especificado puede variar entre 0 y (N - 1). Luego, para cada
		tiempo se extrae la posición (x, y) de la partícula especificada.
		Finalmente, utiliza una aproximación lineal para computar el
		coeficiente de difusión, luego de calcular el desplazamiento
		cuadrático medio.

		@example diffusion({'difussion.data'}, id);
	%}

	function [] = difussion(sources, id)
	end
