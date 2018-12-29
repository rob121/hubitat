/**
 *  Airfoil TTS
 *
 *  Copyright 2018 Daniel Ogorchock
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * 
 */

metadata {
    definition (name: "Airfoil TTS", namespace: "ra", author: "Rob Alfonso") {
        capability "Speech Synthesis"
    }
}

preferences {  
    input("host", "text", title: "URL", description: "The URL of your server running airfoil server ")
    input("port", "text", title: "Port", description: "The server port.")
}


def speak(message) {
    log.debug "Speaking message = '${message}'"
    
    log.debug "Connect Speaker ${settings.speakerid}"
	   log.debug "http://${settings.host}:${settings.port}/say"
    
    def paramsForPost = [
    	uri: "http://${settings.host}:${settings.port}",
    	path: "/say",
      body: "${message}"
	  ]
    
    httpPost(paramsForPost) {response -> 
        def content = response.data
        //log.debug content
    }
    
    log.debug "Command Completed"
}

def installed() {
    initialize()
}

def updated() {
    initialize()   
}

def initialize() {
}
