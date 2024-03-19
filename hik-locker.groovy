metadata {
    definition(
            name: 'Hikvision Locker',
            namespace: 'vctgomes',
            author: 'Vitor Gomes',
            importUrl: 'https://raw.githubusercontent.com/matthewpetro/hubitat-projects/main/garage-door/garage-door-device.groovy'
    ) {
        capability 'Lock'
    }
}

preferences {
    section('Logging') {
        input('debug', 'bool', title: 'Enable debug logging?', required: true, defaultValue: false)
    }
}

def installed() {
    logDebug 'Installed'
}

def updated() {
    logDebug 'Updated'
}

def configure() {
    logDebug 'Configured'
}

def lock() {
    logDebug 'lock()'
    if (device.currentValue('lock') != 'locked') {
        sendEvent(name: 'lock', value: 'locked', isStateChange: true)
    }
}

def unlock() {
    logDebug 'unlock()'
    if (device.currentValue('lock') != 'unlocked') {
        sendEvent(name: 'lock', value: 'unlocked', isStateChange: true)
    }
}

def lockChangeHandler(newValue) {
    logDebug "lockChangeHandler() called: ${newValue}"
    sendEvent(name: 'lock', value: newValue)
    if (newValue == 'locked') {
    } else if (newValue == 'unlocked') {
    }
}

private void logDebug(message) {
    if (debug) log.debug message
}

