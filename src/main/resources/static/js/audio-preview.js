function getAudioComponent(id) {
    return document.querySelector('[data-id="audio-preview-' + id + '"]');
}

function getPlayPauseButton(id) {
    return document.querySelector('[data-id="play-pause-button-' + id + '"]');
}

function getProgressBar(id) {
    return document.querySelector('[data-id="progress-bar-' + id + '"]');
}

function pause(audio) {
    audio.pause();
    getPlayPauseButton(audio.id).innerHTML = '&#9654;';
}

function play(audio) {
    audio.play();
    getPlayPauseButton(audio.id).innerHTML = '&#10074;&#10074;';
}

function togglePlayPause(id) {
    pauseAll(id);
    let audio = getAudioComponent(id);
    let playPauseButton = getPlayPauseButton(id);
    if (audio.paused) {
        play(audio);
    } else {
        pause(audio);
    }
}

function updateProgress(id) {
    let audio = getAudioComponent(id);
    let percentage = (audio.currentTime / audio.duration) * 100;
    getProgressBar(id).style.width = percentage + '%';
}

function pauseAll(id) {
    Array.from(document.querySelectorAll(".audio-preview")).filter(ap => ap.id !== id).forEach(ap => {
        pause(ap);
    });
}

window.onload = function() {
    document.querySelectorAll(".audio-preview").forEach(ap => {
        ap.addEventListener('timeupdate', function(event) {
            updateProgress(ap.id)
       });
       ap.addEventListener('ended', function() {
            getPlayPauseButton(ap.id).innerHTML = '&#9654;'; // Play symbol
            getProgressBar(ap.id).style.width = '0%';
      });
    });
};
