
	%{
		Plotea la PDF (Probability Density Function), para los intervalos de
		colisión. El formato que recibe es el siguiente:

			<event-time-0>
			<event-time-1>
			...

		Donde cada evento representa una colisión. Es posible plotear la
		función de probabilidad en lugar de la PDF. Se pueden proporcionar
		varios archivos fuente para determinar las frecuencias promedio entre
		varias simulaciones. Todas las simulaciones deben ser equivalentes.

		@example frequency({'frequency.data'}, 'probability', 0.1);
	%}

	function [] = frequency(sources, type, step)

		events = [];
		times = [];
		for k = 1:size(sources, 2)
			disp(['Reading ', sources{k}, ' ...']);
			events(:, k) = table2array(readtable(sources{k}, ...
				'ReadVariableNames', false, ...
				'FileType', 'text', ...
				'Delimiter', '\t'));
			times(:, k) = events(:, k) - [0; events(1:end - 1, k)];
		end

		allTimes = times(:);
		freq = size(events, 1) ./ events(end, :);
		freqError = std(freq, 0);
		freq = mean(freq);

		% Begin plotting...

		display = figure();
		display.Name = 'Brownian Motion';
		display.NumberTitle = 'off';

		hold on;

		h = histogram(allTimes, 0:step:(max(allTimes, [], 1) + step));
		h.Normalization = type;
		h.FaceColor = [1, 0.6706, 0.0588];
		h.EdgeColor = [0, 0, 0];
		h.FaceAlpha = 0.9;

		strFrequency = num2str(round(freq, 3), '%.3f');
		strError = num2str(round(freqError, 3), '%.3f');
		display.CurrentAxes.Title.String = ['Frecuencia de Colisiones = ', strFrequency, ' \pm ', strError, ' [col/s]'];
		display.CurrentAxes.Title.FontSize = 16;
		display.CurrentAxes.Title.FontWeight = 'bold';
		display.CurrentAxes.Title.Color = [0, 0, 0];
		display.CurrentAxes.XLabel.String = 'Tiempo entre Colisiones';
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
			display.CurrentAxes.YLabel.String = 'Colisiones';
		else
			display.CurrentAxes.YLabel.String = '';
		end
	end
