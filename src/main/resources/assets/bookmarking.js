function createCopyButton(text) {
    removeCopyButton();
    console.log("Adding button");
    const button = document.createElement("button");
    button.id = 'copy-button';

    button.style.bottom = '25px';
    button.style.right = '25px';
    button.style.borderRadius = '50%';
    button.style.width = '25vw';
    button.style.height = '25vw';
    button.style.position = 'fixed';

    button.onclick = () => {
        console.log("Saving bookmark", text);
        fetch("/bookmarks", {
            method: 'POST',
            body: JSON.stringify({
                path: window.location.pathname,
                text: text
            })
        }).then((response) => {
            if (response.ok) {
                removeCopyButton();
            }
        })
    }

    document.body.appendChild(button);
    return button;
}

function removeCopyButton() {
    const button = document.getElementById("copy-button");
    console.log("Removing button", button);

    if (button) {
        button.remove();
    }
}

const handleSelectionChange = (text) => {
    console.log(text);

    if (!text) {
        removeCopyButton();
        return;
    }

    createCopyButton(text);
}

let selectionTimer = null;
document.onselectionchange = () => {
    const text = window.getSelection().toString();

    if (selectionTimer != null) {
        clearTimeout(selectionTimer);
    }

    selectionTimer = setTimeout(() => { handleSelectionChange(text) }, 200);
}