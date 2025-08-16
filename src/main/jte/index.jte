<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring AI Adventure - Crear Aventura</title>
    <link rel="stylesheet" type="text/css" href="/styles_index.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h1 class="main-title">🚗 Spring AI Adventure</h1>
        <p class="subtitle">Aventuras Épicas con el Megalodon de Shark Cars</p>
    </div>

    <div class="welcome-section">
        <p class="welcome-text">
            ¡Bienvenido a Spring AI Adventure! Prepárate para vivir una experiencia única donde tú eres el protagonista
            de una aventura generada por inteligencia artificial. Cada decisión que tomes cambiará el rumbo de tu
            historia.
        </p>

        <div class="car-info">
            <h4>🦈 Protagonista Especial: El Megalodon</h4>
            <p>Tu aventura incluirá el increíble automóvil "Megalodon" de Shark Cars, un vehículo extraordinario que se
                adaptará perfectamente a tu historia.</p>
        </div>

        <ul class="features-list">
            <li>Historias únicas generadas por IA basadas en tus preferencias</li>
            <li>Múltiples géneros y ubicaciones para explorar</li>
            <li>Decisiones que impactan el desarrollo de la aventura</li>
            <li>Seguimiento del estado físico y emocional de tu protagonista</li>
            <li>Diferentes niveles de complejidad y duración</li>
        </ul>
    </div>

    <div class="form-section">
        <h2 class="form-title">Crear Tu Aventura</h2>

        <form method="post" action="/adventure/create">
            <div class="form-group">
                <label for="genre">Género de la Aventura <span class="required">*</span></label>
                <select id="genre" name="genre" required>
                    <option value="">Selecciona un género...</option>
                    <option value="Acción">Acción</option>
                    <option value="Aventura">Aventura</option>
                    <option value="Misterio">Misterio</option>
                    <option value="Ciencia Ficción">Ciencia Ficción</option>
                    <option value="Fantasía">Fantasía</option>
                    <option value="Thriller">Thriller</option>
                    <option value="Supervivencia">Supervivencia</option>
                    <option value="Espionaje">Espionaje</option>
                </select>
                <div class="form-description">Elige el tipo de aventura que más te emocione</div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="protagonistName">Nombre del Protagonista <span class="required">*</span></label>
                    <input type="text" id="protagonistName" name="protagonistName" required
                           placeholder="Ej: Alex Rodríguez">
                    <div class="audio-input-section" style="margin-top: 10px;">
                        <div class="audio-controls">
                            <button type="button" id="recordBtn" class="audio-btn record-btn">🎤 Grabar Nombre</button>
                            <button type="button" id="stopBtn" class="audio-btn stop-btn" disabled>⏹️ Detener</button>
                            <button type="button" id="playBtn" class="audio-btn play-btn" disabled>▶️ Reproducir
                            </button>
                            <span id="recordingStatus" class="recording-status"></span>
                        </div>
                        <audio id="audioPlayback" controls style="width: 100%; margin-top: 8px; display: none;"></audio>
                        <input type="hidden" id="audioData" name="audioData">
                    </div>
                    <div class="form-description">Escribe tu nombre o grábalo usando el micrófono</div>
                </div>

                <div class="form-group">
                    <label for="protagonistCount">Número de Protagonistas <span class="required">*</span></label>
                    <select id="protagonistCount" name="protagonistCount" required>
                        <option value="">Selecciona...</option>
                        <option value="1">1 - Solo</option>
                        <option value="2">2 - Dúo</option>
                        <option value="3">3 - Trío</option>
                        <option value="4">4 - Grupo pequeño</option>
                        <option value="5">5 - Grupo grande</option>
                    </select>
                    <div class="form-description">¿Cuántos protagonistas participarán?</div>
                </div>
            </div>

            <div class="form-group">
                <label for="protagonistDescription">Descripción del Protagonista <span class="required">*</span></label>
                <textarea id="protagonistDescription" name="protagonistDescription" required
                          placeholder="Describe a tu protagonista: personalidad, habilidades, trasfondo..."></textarea>
                <div class="form-description">Describe las características, personalidad y habilidades de tu
                    protagonista
                </div>
            </div>

            <div class="form-group">
                <label for="location">Ubicación de la Aventura <span class="required">*</span></label>
                <input type="text" id="location" name="location" required
                       placeholder="Ej: Ciudad futurista, Selva amazónica, Estación espacial...">
                <div class="form-description">¿Dónde se desarrollará tu aventura?</div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="duration">Duración de la Aventura <span class="required">*</span></label>
                    <select id="duration" name="duration" required>
                        <option value="">Selecciona duración...</option>
                        <option value="SHORT">Corta (5 decisiones)</option>
                        <option value="MEDIUM">Media (10 decisiones)</option>
                        <option value="LONG">Larga (20 decisiones)</option>
                    </select>
                    <div class="form-description">¿Qué tan larga quieres que sea tu aventura?</div>
                </div>

                <div class="form-group">
                    <label for="complexity">Complejidad <span class="required">*</span></label>
                    <select id="complexity" name="complexity" required>
                        <option value="">Selecciona complejidad...</option>
                        <option value="LOW">Baja (2 opciones por decisión)</option>
                        <option value="MEDIUM">Media (3 opciones por decisión)</option>
                        <option value="HIGH">Alta (5 opciones por decisión)</option>
                    </select>
                    <div class="form-description">¿Cuántas opciones quieres en cada decisión?</div>
                </div>
            </div>

            <div class="submit-section">
                <button type="submit" class="btn-submit">🚀 Comenzar Aventura</button>
            </div>
        </form>
    </div>
</div>

<script>
    class AudioRecorder {
        constructor() {
            this.mediaRecorder = null;
            this.audioChunks = [];
            this.recordBtn = document.getElementById('recordBtn');
            this.stopBtn = document.getElementById('stopBtn');
            this.playBtn = document.getElementById('playBtn');
            this.audioPlayback = document.getElementById('audioPlayback');
            this.recordingStatus = document.getElementById('recordingStatus');
            this.audioDataInput = document.getElementById('audioData');

            this.initEventListeners();
        }

        initEventListeners() {
            this.recordBtn.addEventListener('click', () => this.startRecording());
            this.stopBtn.addEventListener('click', () => this.stopRecording());
            this.playBtn.addEventListener('click', () => this.playAudio());
        }

        async startRecording() {
            try {
                const stream = await navigator.mediaDevices.getUserMedia({audio: true});
                this.mediaRecorder = new MediaRecorder(stream);
                this.audioChunks = [];

                this.mediaRecorder.ondataavailable = (event) => {
                    this.audioChunks.push(event.data);
                };

                this.mediaRecorder.onstop = () => {
                    const audioBlob = new Blob(this.audioChunks, {type: 'audio/wav'});
                    this.audioPlayback.src = URL.createObjectURL(audioBlob);
                    this.audioPlayback.style.display = 'block';
                    this.playBtn.disabled = false;

                    const reader = new FileReader();
                    reader.onloadend = async () => {
                        this.audioDataInput.value = reader.result;

                        this.recordingStatus.textContent = 'Convirtiendo audio a texto...';
                        const response = await fetch('/speech-text', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify(reader.result)
                        });

                        if (response.ok) {
                            const transcribedText = await response.text();
                            document.getElementById('protagonistName').value = transcribedText.trim();
                            this.recordingStatus.textContent = 'Texto convertido: "' + transcribedText.trim() + '"';
                        } else {
                            this.recordingStatus.textContent = 'Error al convertir audio a texto/test';
                        }
                    };
                    reader.readAsDataURL(audioBlob);

                    stream.getTracks().forEach(track => track.stop());
                };

                this.mediaRecorder.start();
                this.updateUIForRecording(true);
                this.recordingStatus.textContent = 'Grabando...';

            } catch (error) {
                console.error('Error accessing microphone:', error);
                alert('No se pudo acceder al micrófono. Asegúrate de dar permisos.');
            }
        }

        stopRecording() {
            if (this.mediaRecorder && this.mediaRecorder.state === 'recording') {
                this.mediaRecorder.stop();
                this.updateUIForRecording(false);
                this.recordingStatus.textContent = 'Grabación completada';
            }
        }

        playAudio() {
            if (this.audioPlayback.src) {
                this.audioPlayback.play();
            }
        }

        updateUIForRecording(isRecording) {
            this.recordBtn.disabled = isRecording;
            this.stopBtn.disabled = !isRecording;

            if (isRecording) {
                this.recordBtn.classList.add('recording');
                this.recordBtn.textContent = '🔴 Grabando...';
            } else {
                this.recordBtn.classList.remove('recording');
                this.recordBtn.textContent = '🎤 Grabar Nombre';
            }
        }
    }

    document.addEventListener('DOMContentLoaded', () => {
        new AudioRecorder();
    });
</script>
</body>
</html>