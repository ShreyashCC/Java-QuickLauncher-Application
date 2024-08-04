#!/bin/bash

SESSION_FILE="$HOME/session.txt"

save_session() {
    echo "Saving session..."
    # Save list of running applications (excluding terminal and session manager itself)
    ps -eo comm | grep -v grep | grep -v "bash" | grep -v "session_manager.sh" > "$SESSION_FILE"
    
    # Save list of open browser tabs (Example for Google Chrome)
    BROWSER_TABS=$(wmctrl -l -p | grep "Google Chrome" | awk '{print $3}')
    echo "$BROWSER_TABS" >> "$SESSION_FILE"
}

restore_session() {
    echo "Restoring session..."
    if [ -f "$SESSION_FILE" ]; then
        while IFS= read -r line; do
            # Check if the line is a Chrome tab
            if [[ "$line" == *"chrome"* ]]; then
                # Open Chrome with the saved URL (Assuming the URL is saved, for simplicity, opening a new window)
                google-chrome &
            else
                # Start other applications
                $line &
            fi
        done < "$SESSION_FILE"
    else
        echo "No session file found."
    fi
}

case "$1" in
    save)
        save_session
        ;;
    restore)
        restore_session
        ;;
    *)
        echo "Usage: $0 {save|restore}"
        exit 1
        ;;
esac

