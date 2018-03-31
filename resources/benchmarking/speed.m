
	%{
		Plotea la PDF (Probability Density Function), para los módulos de las
		velocidades de cada partícula. El formato que recibe es el siguiente:

			<speed-0>
			<speed-1>
			...

		Donde cada módulo se corresponde con una partícula durante un evento
		de colisión (ya que solo en estos casos la velocidad varía). Además,
		se deben incluir los módulos de velocidad original, antes de simular.

		Es posible plotear la función de probabilidad en lugar de la PDF. Se
		pueden proporcionar varios archivos fuente para determinar las
		variaciones a lo largo de varias simulaciones. Todas las simulaciones
		deben ser equivalentes (en cuánto a parámetros de simulación).

		@example speed({'speed.data'}, 'probability', 0.01);
	%}

	function [] = speed(sources, type, step)

		speeds = [];
		for k = 1:size(sources, 2)
			disp(['Reading ', sources{k}, ' ...']);
			speeds(:, k) = table2array(readtable(sources{k}, ...
				'ReadVariableNames', false, ...
				'FileType', 'text', ...
				'Delimiter', '\t'));
		end

		allSpeeds = speeds(:);

		meanSpeeds = mean(speeds, 1);
		meanSpeedsError = std(meanSpeeds, 0);
		meanSpeed = mean(meanSpeeds);

		% Begin plotting...

		display = figure();
		display.Name = 'Brownian Motion';
		display.NumberTitle = 'off';

		hold on;

		h = histogram(allSpeeds, 0:step:(max(allSpeeds, [], 1) + step));
		h.Normalization = type;
		h.FaceColor = [0.1333, 0.6941, 0.2980];
		h.EdgeColor = [0, 0, 0];
		h.FaceAlpha = 0.9;

		strSpeed = num2str(round(meanSpeed, 3), '%.3f');
		strError = num2str(round(meanSpeedsError, 3), '%.3f');
		display.CurrentAxes.Title.String = ['Promedio de modulos de velocidad = ', strSpeed, ' \pm ', strError, ' [m/s]'];
		display.CurrentAxes.Title.FontSize = 16;
		display.CurrentAxes.Title.FontWeight = 'bold';
		display.CurrentAxes.Title.Color = [0, 0, 0];
		display.CurrentAxes.XLabel.String = 'Modulo de la velocidad';
		display.CurrentAxes.XLabel.FontSize = 16;
		display.CurrentAxes.XLabel.FontWeight = 'bold';
		display.CurrentAxes.XLabel.Color = [0, 0, 0];
		display.CurrentAxes.YLabel.FontSize = 16;
		display.CurrentAxes.YLabel.FontWeight = 'bold';
		display.CurrentAxes.YLabel.Color = [0, 0, 0];
		display.CurrentAxes.XGrid = 'on';
		display.CurrentAxes.YGrid = 'on';
		if (strcmp(type, 'probability'))
			display.CurrentAxes.YLabel.String = 'Probabilidad';
		else if (strcmp(type, 'count'))
			display.CurrentAxes.YLabel.String = 'Particulas';
		else
			display.CurrentAxes.YLabel.String = '';
		end
	end
