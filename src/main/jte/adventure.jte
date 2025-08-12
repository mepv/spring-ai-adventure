@import com.adventure.model.Adventure
@import com.adventure.model.AdventureStatus

@param Adventure adventure

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aventura - Spring AI Adventure</title>
    <link rel="stylesheet" type="text/css" href="/styles_adventure.css">
</head>
<body>
<div class="adventure-container">
    <div class="adventure-header">
        <h1 class="adventure-title">🚗 Aventura Megalodon</h1>
        <div class="adventure-info">
            <div class="info-item">
                <span class="info-label">Turno</span>
                <span class="info-value">${adventure.getStatus() == AdventureStatus.COMPLETED ? adventure.getCurrentTurn() - 1 : adventure.getCurrentTurn()}</span>
            </div>
            <div class="info-item">
                <span class="info-label">Estado</span>
                <span class="status-badge status-${adventure.getStatus().name().toLowerCase().replace('_', '-')}">${adventure.getStatus().value()}</span>
            </div>
            @if(adventure.getParams() != null)
                <div class="info-item">
                    <span class="info-label">Protagonista</span>
                    <span class="info-value">${adventure.getParams().protagonistName()}</span>
                </div>
            @endif
        </div>
    </div>

    <div class="story-section">
        <p class="story-text">${adventure.getCurrentStory()}</p>
        @if(adventure.getInitialImage() != null && !adventure.getInitialImage().isEmpty())
            <div class="image-section">
                <img src="${adventure.getInitialImage()}" alt="Adventure Scene" class="adventure-image"
                     style="max-width: 100%; height: auto; border-radius: 8px; margin: 16px 0;">
            </div>
        @endif
        @if(adventure.getStatus() == AdventureStatus.COMPLETED && adventure.getCompletionImage() != null && !adventure.getCompletionImage().isEmpty())
            <div class="image-section">
                <h3 style="color: #2c3e50; margin-bottom: 8px;">🎯 Desenlace de la Aventura</h3>
                <img src="${adventure.getCompletionImage()}" alt="Adventure Completion" class="adventure-image"
                     style="max-width: 100%; height: auto; border-radius: 8px; margin: 16px 0;">
            </div>
        @endif
    </div>

    <div class="protagonist-states">
        <div class="state-card physical-state">
            <div class="state-title">🏃‍♂️ Estado Físico</div>
            <div>${adventure.getProtagonistPhysicalState()}</div>
        </div>
        <div class="state-card emotional-state">
            <div class="state-title">😊 Estado Emocional</div>
            <div>${adventure.getProtagonistEmotionalState()}</div>
        </div>
    </div>

    @if((adventure.getStatus() == AdventureStatus.CREATED || adventure.getStatus() == AdventureStatus.IN_PROGRESS)
                        && adventure.getAvailableChoices() != null && !adventure.getAvailableChoices().isEmpty())
        <div class="choices-section">
            <h3 class="choices-title">¿Qué decides hacer?</h3>
            <form class="choices-form" method="post" action="/adventure/${adventure.getId()}/decision">
                @for(var i = 0; i < adventure.getAvailableChoices().size(); i++)
                    <label class="choice-option">
                        <input type="radio" name="choice" value="${adventure.getAvailableChoices().get(i)}" required>
                        <span class="choice-text">${adventure.getAvailableChoices().get(i)}</span>
                    </label>
                @endfor

                <div class="action-buttons">
                    <button type="submit" class="btn btn-primary">Continuar Aventura</button>
                    <a href="/" class="btn btn-secondary">Nueva Aventura</a>
                </div>
            </form>
        </div>
    @else
        <div class="completed-adventure-section" style="display: flex; flex-direction: column; gap: 20px;">
            @if(adventure.getStatus() == AdventureStatus.COMPLETED)
                <div class="summary-section">
                    <div class="summary-buttons" style="display: flex; flex-direction: row; gap: 12px; margin-bottom: 20px;">
                        <form method="post" action="/adventure/${adventure.getId()}/summary" style="flex: 1;">
                            <button type="submit" class="btn btn-secondary" style="width: 100%; min-height: 50px;">📝 Generar Resumen</button>
                        </form>
                        <form method="get" action="/adventure/${adventure.getId()}/text-speech" target="audio-player-frame" style="flex: 1;">
                            <button type="submit" class="btn btn-secondary" style="width: 100%; min-height: 50px;" disabled="${!adventure.isSummaryGenerated()}">
                                🔊 Generar Audio
                            </button>
                        </form>
                    </div>
                    <div class="audio-container">
                        <h3 style="color: #2c3e50; margin-bottom: 12px;">🎧 Audio Resumen</h3>
                        <iframe name="audio-player-frame" style="width: 100%; height: 80px; border: none; border-radius: 5px; background-color: #f8f9fa;"></iframe>
                    </div>
                </div>
            @endif
            <div class="navigation-section" style="display: flex; justify-content: center; margin-top: 30px;">
                <a href="/" class="btn btn-primary" style="min-width: 200px; padding: 15px 30px; font-size: 1.2em;">🏠 Nueva Aventura</a>
            </div>
        </div>
    @endif
</div>
</body>
</html>