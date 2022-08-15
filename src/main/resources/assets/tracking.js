

function report(time) {
    const y = window.scrollY;
    let maxScroll = window.scrollMaxY;
    let progress = -1;

    if (!maxScroll) {
        maxScroll = -1;
    } else {
        progress = y / maxScroll;
    }

    fetch("/report", {
        method: 'POST',
        body: JSON.stringify({
            path: window.location.pathname,
            t: time,
            y: y,
            my: maxScroll,
            p: progress,
            v: document.visibilityState === 'visible'
        })
    })
}

let scrollTimer = null;
window.onscroll = (e) => {
    if (scrollTimer !== null) {
        clearTimeout(scrollTimer);
    }

    let time = Date.now();

    scrollTimer = setTimeout(() => { report(time) }, 200);
}

document.onvisibilitychange = (e) => {
    report(Date.now());
}

report(Date.now());
