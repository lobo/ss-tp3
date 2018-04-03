
	%{
		Plotea la trayectoria de una partícula específica a lo largo del
		tiempo. El formato de entrada debe corresponderse con:

			<N>
			<t0>
			<x> <y> <r> <v>
			...

		El 'id' especificado puede variar entre 0 y (N - 3), debido a que las
		últimas 2 partículas de cada frame se corresponden con partículas
		fijas utilizadas para bloquear la celda durante la animacion. Luego,
		para cada tiempo se extrae la posición (x, y) de la partícula
		especificada.

		@example trace('animation.txt', 0, 0.5);
	%}

	function [] = trace(source, id, L)

		file = fopen(source, 'r');
		disp(['Reading ', source, ' ...']);
		N = str2num(fgetl(file));
		xy = [];

		if id >= N || id < 0
			disp('Error: el ID debe ser mayor o igual a 0 y menor a N.');
			return;
		end

		while true
			time = fgetl(file);
			if time == -1
				break;
			end
			for k = 1:id
				fgetl(file);
			end
			xyrv = str2num(fgetl(file));
			xy(end + 1, 1:2) = xyrv(1, 1:2);
			for k = (id + 1):(N - 1)
				fgetl(file);
			end
			fgetl(file);
		end

		fclose(file);

		% Begin plotting...

		display = figure();
		display.Name = 'Brownian Motion';
		display.NumberTitle = 'off';

		hold on;

		comet(xy(:, 1), xy(:, 2));

		display.CurrentAxes.PlotBoxAspectRatio = [1 1 1];
		display.CurrentAxes.Title.String = ['Trayectoria de la particula con ID = ', num2str(id)];
		display.CurrentAxes.Title.FontSize = 16;
		display.CurrentAxes.Title.FontWeight = 'bold';
		display.CurrentAxes.Title.Color = [0, 0, 0];
		display.CurrentAxes.XLabel.String = 'Coordenada - X';
		display.CurrentAxes.XLabel.FontSize = 16;
		display.CurrentAxes.XLabel.FontWeight = 'bold';
		display.CurrentAxes.XLabel.Color = [0, 0, 0];
		display.CurrentAxes.YLabel.FontSize = 16;
		display.CurrentAxes.YLabel.FontWeight = 'bold';
		display.CurrentAxes.YLabel.Color = [0, 0, 0];
		display.CurrentAxes.XGrid = 'on';
		display.CurrentAxes.YGrid = 'on';
		display.CurrentAxes.XLim = [0 L];
		display.CurrentAxes.YLim = [0 L];
		display.CurrentAxes.YLabel.String = 'Coordenada - Y';
	end
