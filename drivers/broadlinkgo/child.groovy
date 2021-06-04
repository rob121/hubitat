/*
 * Broadlinkgo Child
 *
 * Calls URIs with HTTP
 * 
 */
metadata {
    definition(name: "Broadlinkgo Child", namespace: "ra", author: "Rob Alfonso", importUrl: "") {
        capability "Actuator"
        capability "PushableButton"
        capability "Sensor"
        command "push"
  
    }
}

preferences {
    section("URI") {
        input "onPress", "text", title: "On Press", required: true
        input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
    }
}

def on(){
 sendEvent(name: "switch", value: "on", isStateChange: true)
 buttonEvent()    
}

def off(){
 sendEvent(name: "switch", value: "off", isStateChange: true)
 //buttonEvent()    disabled because it breaks things running 2x
}

def push(button) {
  buttonEvent()	
}

def parse(String description) {
    if (logEnable) log.debug(description)
}

// installed() runs just after a sensor is paired
def installed() {
	state.prefsSetCount = 0
	displayInfoLog("Installing")
}

// configure() runs after installed() when a sensor is paired or reconnected
def configure() {
	init()
	state.prefsSetCount = 1
	return
}


def init() {

}


def updated() {
    log.info "updated..."
    log.warn "debug logging is: ${logEnable == true}"
    if (logEnable) runIn(1800, logsOff)
}



private def buttonEvent() {
    if (logEnable) log.debug "Sending on GET Button request to [${settings.onURI}]"

    try {
        httpGet(settings.onPress) { resp ->
            if (resp.success) {
                sendEvent(name:"push",value: "true")
                
            }
            if (logEnable)
                if (resp.data) log.debug "${resp.data}"
        }
    } catch (Exception e) {
        log.warn "Call to on failed: ${e.message}"
    }
}



