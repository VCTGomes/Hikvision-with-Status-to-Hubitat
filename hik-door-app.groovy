/** Hikvision with status to Hubitat is inspired on matthewpetro project, 
  * which I modded to deliver HTTP POST command on ISAPI from Hikvision doorbells 
  *
  * The app needs the IP from intercom and its password to give HTTP POST command. 
  * To deliver lock/unlock state, you can select a Contact Sensor. If you don't need this status, use the lighter drive:
  * https://github.com/VCTGomes/Hikvision-Door-Control-to-Hubitat
  */

definition(
        name: 'Hikvision with Status',
        namespace: 'vctgomes',
        author: 'Vitor Gomes',
        description: 'Combine a contact sensor with your Hikvision Doorbell to unlock your door',
        category: 'Convenience',
        importUrl: 'https://raw.githubusercontent.com/VCTGomes/Hikvision-with-Status-to-Hubitat/main/hik-door-app.groovy',
        iconUrl: '',
        iconX2Url: ''
)

preferences {
    section('Devices') {
        input("ipAddress", "text", title: "IP Address", required: true)
        input("password", "password", title: "Password", required: true)
        input 'closedContactSensor', 'capability.contactSensor', title: 'Contact sensor that detects lock closed state', required: true
    }
    section('Logging') {
        input('debug', 'bool', title: 'Enable debug logging?', required: true, defaultValue: false)
    }
}

def installed() {
    logDebug 'Installed'
    updated()
    def deviceNetworkId = "${app.id}-simulated-lock-device"
    def lockDevice = addChildDevice('vctgomes', 'Hikvision Locker', deviceNetworkId, null, [label: 'Hikvision Locker'])
    state.deviceNetworkId = deviceNetworkId
    subscribe(lockDevice, 'lock', 'lockChangeHandler')
}

def updated() {
    logDebug 'Updated'
    unsubscribe('closedSensorHandler')
    unsubscribe('openSensorHandler')
    subscribe(closedContactSensor, 'contact', 'closedSensorHandler')
    if (null != openContactSensor) {
        subscribe(openContactSensor, 'contact', 'openSensorHandler')
    }
}

def uninstalled() {
    logDebug 'Uninstalled'
    unsubscribe(closedContactSensor)
    if (null != openContactSensor) {
        unsubscribe(openContactSensor)
    }
    deleteChildDevice(getChildDevice(state.deviceNetworkId))
}

def lockChangeHandler(event) {
    logDebug "lockChangeHandler() called: ${event.name} ${event.value}"
    def actualLockState = actualLockState()
    logDebug "actualLockState: ${actualLockState}"
    if (event.value == 'unlocking' && actualLockState == 'unlocked') {
        activateLock()
    } else if (event.value == 'locking' && actualLockState == 'locked') {
        disableLock()
    }
}

private syncLockState() {
    logDebug 'syncLockState()'
    def lockDevice = getChildDevice(state.deviceNetworkId)
    def lockDeviceState = lockDevice.currentValue('lock')
    if (!['locked', 'unlocked'].contains(lockDeviceState)) {
        def actualLockState = actualLockState()
        if (['locked', 'unlocked'].contains(actualLockState)) {
            lockDevice.lockChangeHandler(actualLockState)
        }
    }
}

private actualLockState() {
    if (null != openContactSensor) {
        if (openContactSensor.currentValue('contact') == 'closed') {
            return 'locked'
        } else if (closedContactSensor.currentValue('contact') == 'closed') {
            return 'unlocked'
        } else {
            return 'unknown'
        }
    } else {
        if (closedContactSensor.currentValue('contact') == 'closed') {
            return 'unlocked'
        } else {
            return 'locked'
        }
    }
}

private activateLock() {
    logDebug 'activateLock()'
    sendCommand("open")
}

private disableLock() {
    logDebug 'disableLock()'
    sendCommand("open")
} 

def closedSensorHandler(event) {
    logDebug "closedSensorHandler() called: ${event.name} ${event.value}"
    def lockDevice = getChildDevice(state.deviceNetworkId)
    def lockDeviceState = lockDevice.currentValue('lock')

    if (event.value == 'open' && lockDeviceState != 'openning') {
        lockDevice.lockChangeHandler('unlocked')
    } else if (event.value == 'closed') {
        lockDevice.lockChangeHandler('locked')
    }
}

// HTTP POST on Hikvision Doorbell

def sendCommand(String cmd) {
    def headers = [
        'Content-Type': 'application/xml',
    ]
    def data = "<RemoteControlDoor><cmd>${cmd}</cmd></RemoteControlDoor>"
    def params = [
        uri: "http://admin:${settings.password}@${settings.ipAddress}/ISAPI/AccessControl/RemoteControl/door/1",
        requestContentType: 'application/xml',
        headers: headers,
        body: data
    ]

    try {
        httpPut(params) { response ->
            log.debug "Server response: ${response.status}"
            if (response.status == 200) {
                log.debug "Command sent"
            } else {
                log.error "Command faild: ${response.status}"
            }
        }
    } catch (e) {
        log.error "Fail to sent command: ${e}"
    }
}


private void logDebug(message) {
    if (debug) log.debug message
}

