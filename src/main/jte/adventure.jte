@import com.adventure.model.Adventure
@import com.adventure.model.AdventureStatus

@param Adventure adventure

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Aventura - Spring AI Adventure</title>
    <link rel="stylesheet" type="text/css" href="styles_adventure.css">
</head>
<body>
    <div class="adventure-container">
        <div class="adventure-header">
            <h1 class="adventure-title">🚗 Aventura Megalodon</h1>
            <div class="adventure-info">
                <div class="info-item">
                    <span class="info-label">Turno</span>
                    <span class="info-value">${adventure.getCurrentTurn()}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Estado</span>
                    <span class="status-badge status-${adventure.getStatus().name().toLowerCase().replace('_', '-')}">${adventure.getStatus()}</span>
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

        @if(adventure.getStatus() == AdventureStatus.IN_PROGRESS && adventure.getAvailableChoices() != null && !adventure.getAvailableChoices().isEmpty())
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
        <div class="action-buttons">
            <a href="/" class="btn btn-primary">Nueva Aventura</a>
        </div>
        @endif
    </div>
</body>
</html>