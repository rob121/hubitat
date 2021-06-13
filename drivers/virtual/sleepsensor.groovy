/*
 * Virtual Sleep Sensor with switch
 *
 * 
 */
metadata {
    definition(name: "Virtual Sleep Sensor", namespace: "ra", author: "Rob Alfonso", importUrl: "") {
        capability "Actuator"
        capability "Switch"
        capability "SleepSensor"
        capability "Sensor"
        command "on"
        command "off"
        command "sleeping"
        command "notSleeping"
    }
}

preferences {
    section("URI") {
        input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
    }
}

def on(){
 sleeping()   
}

def off(){
 notSleeping()
}

def sleeping(){
 sendEvent(name: "switch", value: "on", isStateChange: true) 
 sendEvent(name: "sleeping", value: "sleeping", isStateChange: true) 
}

def notSleeping(){
 sendEvent(name: "switch", value: "off", isStateChange: true)
 sendEvent(name: "sleeping", value: "not_sleeping", isStateChange: true)
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
