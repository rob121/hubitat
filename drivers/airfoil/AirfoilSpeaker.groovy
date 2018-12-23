/**
 *  Airfoil Speaker Control
 *  Original Author     : me@robalfonso.com
 *  Creation Date       : 2018-12-08


Requires https://github.com/jnewland/airfoil-api setup on your network

 */

import groovy.json.JsonSlurper

metadata {
    definition (name: "Airfoil Speaker Control", namespace: "ra", author: "rob121") {
	capability "AudioVolume"
	capability "Switch Level"
        capability "Switch"
        capability "Refresh"
    }
}

preferences {
   

    input("host", "text", title: "URL", description: "The URL of your server running airfoil server ")
    input("port", "text", title: "Port", description: "The server port.")
	

		
	try{
       input("speakerid", "enum", title: "Speaker ID", description: "Add url/port then save for options", multiple: false, required: false, options: getSpeakers())
	}catch(e){
	log.debug "Unable to render speakerid - possible network issue"
	}	
	    
	
	

} 
 


def on() {
	
	state.connected = true
	
    log.debug "Connect Speaker ${settings.speakerid}"
	log.debug "http://${settings.host}:${settings.port}/speakers/${settings.speakerid}/connect"
    
    def paramsForPost = [
    	uri: "http://${settings.host}:${settings.port}",
    	path: "/speakers/${settings.speakerid}/connect"
	]
    httpPost(paramsForPost) {response -> 
        def content = response.data
        
       //  log.debug content
    }
    
    log.debug "Command Completed"
    
    sendEvent(name: "switch", value: "on");
	
	sendEvent(name: "connected", value: state.connected, unit: "")
			
    refresh()

}

def off() {
	
	state.connected = false
	
    log.debug "Disconnect Speaker ${settings.speakerid}"
    
    def paramsForPost = [
    	uri: "http://${settings.host}:${settings.port}",
    	path: "/speakers/${settings.speakerid}/disconnect"
	]
    httpPost(paramsForPost) {response -> 
        def content = response.data
        
        // log.debug content
    }
    
    log.debug "Command Completed"
    
    sendEvent(name: "switch", value: "off");
	sendEvent(name: "connected", value: state.connected, unit: "")
}

def getSpeakers() {
	
    log.debug "Fetching Speaker List"
	def ids = []
	
	if(settings==null){
  	return ids
	}
	
	if(settings.host==null || settings.port==null){
	
	return ids
	
	}
	
    def url = "http://${settings.host}:${settings.port}/speakers"
	
	httpGet(url) { response->
		
     def id_raw = response.data 
			 
            id_raw.each {sp->
	            ids << ["${sp.id}":"${sp.name}"]
			}	
		    log.debug "${ids}"
	}	
	
    return ids
  
}


def refresh()
{
    log.debug "Refreshing speaker ${settings.speakerid}.."
	

	
	httpGet("http://${settings.host}:${settings.port}/speakers") { response->
		
	

        response.data.each { 
		 
			
			log.debug "Speaker Found: ${it.id}"
			if (it.id == settings.speakerid){
			
			
			 state.volume = it.volume
				
			 
				
			 def level = ((state.volume * 100) as double).round(2)
			 
				
			 if(it.connected=="true"){	
			 		sendEvent(name: "switch", value: "on")	
				    state.connected = true
			 }else{
					 sendEvent(name: "switch", value: "off")	
				    state.connected = false
			 }
			 sendEvent(name: "connected", value: state.connected, unit: "")
			 sendEvent(name: "volume", value: level, unit: "%")
			
			}
			 
	    }
		
      
		
		
		def content = response.data
		
		log.debug content
	
	}	
  
}

def mute(){
	
		
  log.debug "Mute Volume for ${settings.speakerid}"

  def paramsforPost = [
    	uri: "http://${settings.host}:${settings.port}",
    	path: "/speakers/${settings.speakerid}/volume",
        body: "0"
	]
    try {
        httpPost(paramsforPost) { resp ->
         //   log.debug "Post response code: ${resp.status}"
			
			sendEvent(name: "volume", value: "0", unit: "%")
        }
    } catch (e) {
        //ALWAYS seems to throw an exception?
        //Platform bug maybe? 
        log.debug "HTTP Exception Received on PUT: $e"
    }


}

def unmute(){
	
	if (state.volume == null){
	
	state.volume = 0.50
		
	}
	
	def level = ((state.volume * 100) as double).round(2)

  log.debug "Unmute Volume for ${settings.speakerid}"
  def paramsforPost = [
    	uri: "http://${settings.host}:${settings.port}",
    	path: "/speakers/${settings.speakerid}/volume",
	  body: "${state.volume}"
	]
    try {
        httpPost(paramsforPost) { resp ->
         //   log.debug "Post response code: ${resp.status}"
			sendEvent(name: "volume", value: level, unit: "%")
        }
    } catch (e) {
        //ALWAYS seems to throw an exception?
        //Platform bug maybe? 
        log.debug "HTTP Exception Received on PUT: $e"
    }


	
}

def volumeUp(){
	
	
    if (state.volume != null){
	
		state.volume = state.volume + 0.1
		
		if (state.volume > 1) {
			state.volume = 1
		}
		
	}else{ 
	 
		state.volume = 0.1
	}		
	
	def level = ((state.volume * 100) as double).round(2)

    log.debug "Increase Volume for ${settings.speakerid}"
	log.debug "${state.volume}"
  
	def paramsforPost = [
    	uri: "http://${settings.host}:${settings.port}",
    	path: "/speakers/${settings.speakerid}/volume",
	    body: "${state.volume}"
	]
	
    try {
        httpPost(paramsforPost) { resp ->
           // "Post response code: ${resp.status}"
			
			sendEvent(name: "volume", value: level, unit: "%")
        }
    } catch (e) {
        //ALWAYS seems to throw an exception?
        //Platform bug maybe? 
        log.debug "HTTP Exception Received on PUT: $e"
    }


}

def volumeDown(){

	
    if (state.volume != null){
	
		state.volume = state.volume - 0.1
		
		if (state.volume < 0) {
			state.volume = 0
		}
		
	}else{ 
	 
		state.volume = 0.1
	}	
	
	log.debug "Decrease Volume for ${settings.speakerid}" 
	log.debug "${state.volume}"

	def level = ((state.volume * 100) as double).round(2)

  def paramsforPost = [
    	uri: "http://${settings.host}:${settings.port}",
    	path: "/speakers/${settings.speakerid}/volume",
	    body: "${state.volume}"
	]
    try {
        httpPost(paramsforPost) { resp ->
           // log.debug "Post response code: ${resp.status}"
			sendEvent(name: "volume", value: level, unit: "%")
        }
    } catch (e) {
        //ALWAYS seems to throw an exception?
        //Platform bug maybe? 
        log.debug "HTTP Exception Received on PUT: $e"
    }



}
def setLevel(level){

   setVolume(level)

}
def setVolume(level){
	
  def volume = level / 100	
	
  state.volume = volume
  log.debug "Set Volume for ${settings.speakerid}"
  log.debug "${volume}"
	
 
		
  def paramsforPost = [
    	uri: "http://${settings.host}:${settings.port}",
    	path: "/speakers/${settings.speakerid}/volume",
	    body: "${volume}"
	]
    try {
        httpPost(paramsforPost) { resp ->
           // log.debug "Post response code: ${resp.status}"
			sendEvent(name: "volume", value: level, unit: "%")
        }
    } catch (e) {
        //ALWAYS seems to throw an exception?
        //Platform bug maybe? 
        log.debug "HTTP Exception Received on PUT: $e"
    }


	
}


def installed() {
	updated()
}

def updated() {

	state.volume = 0
	state.connected = false
	log.info "${device.speakerid} updated with state: ${state}"

}
       

