
	%{
		Plotea el desplazamiento cuadrático medio de una partícula utilizando
		varias muestras de animación con el siguiente formato:

			<N>
			<t0>
			<x> <y> <r> <v>
			...

		El 'id' especificado puede variar entre 0 y (N - 1). Luego, para cada
		tiempo se extrae la posición (x, y) de la partícula especificada.
		Finalmente, utiliza una aproximación lineal para computar el
		coeficiente de difusión, luego de calcular el desplazamiento
		cuadrático medio.

		@example dcm({'animation.txt'}, id, 9.0, 0.025);
	%}

	function [] = dcm(sources, id, time, delta, x, y)

		% Leer las coordenadas como en trace, de cada archivo
		% Calcular z^2 para cada coordenada
		% (Las series deben tener el mismo largo)
		% Interpolar el resultado de forma lineal
		% Mostrar la pendiente como D (siendo <z^2> = Dt)
		% Barras de error!

		z2 = [];
		for k = 1:size(sources, 2)
			disp(['Reading ', sources{k}, ' ...']);
			z2(:, k) = getXY(sources{k}, id, time, delta, x, y);
		end

		dcm = mean(z2, 2);
		errors = std(z2, 0, 2);

		x = (0:delta:(time-delta))';
		p = polyfit(x, dcm, 1);

		% Plotting...
		display = figure();
		display.Name = 'Brownian Motion';
		display.NumberTitle = 'off';

		hold on;

		errorbar(x, dcm, errors(:, 1), '-', 'Color', [0.7, 0.7, 0.7]);
		scatter(x, dcm, 10, [1.0, 0.3, 0.3]),
		plot(x, polyval(p, x));

		display.CurrentAxes.Title.String = ['Coeficiente de Difusion = ', num2str(p(1)), ' [m^2/s]'];
		display.CurrentAxes.Title.FontSize = 16;
		display.CurrentAxes.Title.FontWeight = 'bold';
		display.CurrentAxes.Title.Color = [0, 0, 0];
		display.CurrentAxes.XLabel.String = 'Tiempo [s]';
		display.CurrentAxes.XLabel.FontSize = 16;
		display.CurrentAxes.XLabel.FontWeight = 'bold';
		display.CurrentAxes.XLabel.Color = [0, 0, 0];
		display.CurrentAxes.YLabel.String = 'Desplazamiento Cuadratico Medio [m^2]';
		display.CurrentAxes.YLabel.FontSize = 16;
		display.CurrentAxes.YLabel.FontWeight = 'bold';
		display.CurrentAxes.YLabel.Color = [0, 0, 0];
		display.CurrentAxes.XGrid = 'on';
		display.CurrentAxes.YGrid = 'on';
		display.CurrentAxes.XLim = [0 time];
		display.CurrentAxes.YLim = [0 Inf];
		display.CurrentAxes.addprop('Legend');
		display.CurrentAxes.Legend = legend({
			'Error (desviacion estandar)',
			'<z^2>',
			'Interpolacion Lineal (Dt - <z^2> = 0)',
		});
	end

	function [z2] = getXY(source, id, time, delta, x, y)

		samples = round(time/delta);
		file = fopen(source, 'r');
		N = str2num(fgetl(file));
		xy = [];

		if id >= N || id < 0
			disp('Error: el ID debe ser mayor o igual a 0 y menor a N.');
			return;
		end

		while size(xy, 1) < samples
			time = fgetl(file);
			if time == -1
				break;
			end
			for k = 1:id
				fgetl(file);
			end
			xyrv = str2num(fgetl(file));
			xy(end + 1, 1:2) = xyrv(1, 1:2) - [x, y];
			for k = (id + 1):(N - 1)
				fgetl(file);
			end
			fgetl(file);
		end

		fclose(file);

		z2 = xy .^ 2;
		z2 = z2(:, 1) + z2(:, 2);
	end
