/*
 * Http GET Button Pad w/ up to 5 buttons
 *
 * Calls URIs with HTTP GET for button
 * 
 */
metadata {
    definition(name: "Http Button Pad", namespace: "ra", author: "Rob Alfonso", importUrl: "") {
        capability "Actuator"
        capability "PushableButton"
        capability "Sensor"
        capability "Momentary"
        command "push", ["button"]      
    }
}

preferences {
    section("URI") {
        input "onPress1", "text", title: "On Press Btn 1", required: true
        input "onPress2", "text", title: "On Press Btn 2", required: false
        input "onPress3", "text", title: "On Press Btn 3", required: false
        input "onPress4", "text", title: "On Press  Btn 4", required: false
        input "onPress5", "text", title: "On Press  Btn 5", required: false
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
  buttonEvent(button)	
  sendEvent(name: "pushed", value: "${button}", isStateChange: true)  
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



private def buttonEvent(String button) {
    if (logEnable) log.debug "Sending on GET Button request to [${settings.onURI}]"

    
    switch(button){
      case "1":
       url = settings.onPress1
       break;
      case "2":
       url = settings.onPress2
       break;
      case "3":
       url = settings.onPress3
       break;
      case "4":
       url = settings.onPress4
       break;
      case "5":
       url = settings.onPress5
       break;
      default:
       url = settings.onPress1
       break;
    }
    
    
    
    try {
        httpGet(url) { resp ->
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



