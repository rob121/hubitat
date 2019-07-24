/**
 *  Qubino Flush Dimmer 0-10V
 *	Device Handler 
 *	Version 1.01
 *  Date: 31.3.2017
 *	Author: Kristjan Jam&scaron;ek (Kjamsek), Goap d.o.o.
 *  Copyright 2017 Kristjan Jam&scaron;ek
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
 * |---------------------------- DEVICE HANDLER FOR QUBINO FLUSH DIMMER 0-10V Z-WAVE DEVICE -------------------------------------------------------|  
 *	The handler supports all unsecure functions of the Qubino Flush Dimmer 0-10V device, except configurable inputs. Configuration parameters and
 *	association groups can be set in the device's preferences screen, but they are applied on the device only after
 *	pressing the 'Set configuration' and 'Set associations' buttons on the bottom of the details view. 
 *
 *	This device handler supports data values that are currently not implemented as capabilities, so custom attribute 
 *	states are used. Please use a SmartApp that supports custom attribute monitoring with this device in your rules.
 * |-----------------------------------------------------------------------------------------------------------------------------------------------|
 *
 *
 *	TO-DO:
 *  - Implement secure mode
 *
 *	CHANGELOG:
 *	0.99: Final release code cleanup and commenting
 *	1.00: Added comments to code for readability
 *  1.01: Added support for configurable I1 input and MC Associations
 */
metadata {
	definition (name: "Qubino Flush Dimmer 0-10V", namespace: "Goap", author: "Kristjan Jam&scaron;ek") {
		capability "Actuator"
		capability "Switch"
		capability "Switch Level"
		
		capability "Relay Switch"	// - Tagging capability
		capability "Light"			// - Tagging capability
		
		capability "Configuration" //Needed for configure() function to set MultiChannel Lifeline Association Set
		capability "Temperature Measurement" //This capability is valid for devices with temperature sensors connected
		capability "Refresh"
		attribute "i1Sensor", "number" // custom attribute to store and display sensor value when I1 is configured as an additional sensor

		command "setConfiguration" //command to issue Configuration Set commands to the module according to user preferences
		command "setAssociation" //command to issue Association Set commands to the modules according to user preferences

        fingerprint mfr:"0159", prod:"0001", model:"0053"  //Manufacturer Information value for Qubino Flush Dimmer 0-10V
	}





	preferences {
/**
*			--------	CONFIGURATION PARAMETER SECTION	--------
*/
				input (
					type: "paragraph",
					element: "paragraph",
					title: "CONFIGURATION PARAMETERS:",
					description: "Configuration parameter settings."
				)
				input name: "param1", type: "enum", required: false,
					options: ["0" : "0 - mono-stable switch type (push button)",
							  "1" : "1 - Bi-stable switch type",
							  "2" : "2 - Potentiometer",
							  "3" : "3 - 0-10V Temperature sensor",
							  "4" : "4 - 0-10V Illumination sensor",
							  "5" : "5 - 0-10V General purpose sensor"],
					title: "1. Input 1 type.\n " +
						   "By this parameter the user can set input based on device type (switch, potentiometer, 0-10V sensor,..).\n" +
						   "Available settings:\n" +
						   "0 - Mono-stable switch type (push button) – button quick press turns between previous set dimmer value and zero.\n" +
						   "1 - Bi-stable switch type.\n" +
						   "2 - Potentiometer (Flush Dimmer 0-10V  is using set value the last received from potentiometer or from z-wave controller).\n" +
						   "3 - 0-10V Temperature sensor (regulated output).\n" +
						   "4 - 0-10V Illumination sensor (regulated output).\n" +
						   "5 - 0-10V General purpose sensor (regulated output).\n" +
						   "Default value: 0.\n" +
						   "NOTE: After parameter change to value 3, 4 or 5 first exclude the module (without setting parameters to default value) then wait at least 30s and then re include the module!"
    
				input name: "param10", type: "enum", required: false,
					options: ["0" : "0 - ALL ON is not active, ALL OFF is not active",
							  "1" : "1 - ALL ON is not active, ALL OFF active",
							  "2" : "2 - ALL ON active, ALL OFF is not active",
							  "255" : "255 - ALL ON active, ALL OFF active"],
					title: "10. Activate / deactivate functions ALL ON / ALL OFF.\n " +
						   "Available settings:\n" +
							"255 - ALL ON active, ALL OFF active.\n" +
							"0 - ALL ON is not active, ALL OFF is not active.\n" +
							"1 - ALL ON is not active, ALL OFF active.\n" +
							"2 - ALL ON active, ALL OFF is not active.\n" +
							"Default value: 255.\n" +
							"Flush Dimmer 0-10V module responds to commands ALL ON / ALL OFF that may be sent by the main controller or by other controller belonging to the system."
				
				input name: "param11", type: "number", range: "0..32536", required: false,
					title: "11. Automatic turning off output after set time.\n " +
						   "Available settings:\n" +
							"0 - Auto OFF disabled.\n" +
							"1 - 32536 = 1 second - 32536 seconds Auto OFF enabled with define time, step is 1 second.\n" +
							"Default value: 0."
							
				input name: "param12", type: "number", range: "0..32536", required: false,
					title: "12. Automatic turning on output after set time.\n" +
						   "Available settings:\n" +
							"0 - Auto ON disabled.\n" +
							"1 - 32536 = 1second - 32536 seconds Auto ON enabled with define time, step is 1 second.\n" +
							"Default value: 0."
							
				input name: "param21", type: "enum", required: false,
					options: ["0" : "0 - Double click disabled",
							  "1" : "1 - Double click enabled"],
					title: "21. Enable/Disable Double click function.\n" +
						   "If Double click function is enabled, a fast double click on the push button will set dimming power at maximum dimming value.\n" +
						   "Available settings:\n" +
							"0 - Double click disabled.\n" +
							"1 - Double click enabled.\n" +
							"Default value: 0.\n" +
							"NOTE: Valid only if input is set as mono-stable (push button)."
							
				input name: "param30", type: "enum", required: false,
					options: ["0" : "0 - Flush Dimmer 0-10V module saves its state before power failure (it returns to the last position saved before a power failure)",
							  "1" : "1 - Flush Dimmer 0-10V module does not save the state after a power failure, it returns to 'off' position"],
					title: "30. Saving the state of the device after a power failure.\n" +
						   "Available settings:\n" +
							"0 - Flush Dimmer 0-10V module saves its state before power failure (it returns to the last position saved before a power failure).\n" +
							"1 - Flush Dimmer 0-10V module does not save the state after a power failure, it returns to 'off' position.\n" +
							"Default value: 0."

				input name: "param52", type: "enum", required: false,
					options: ["0" : "0 - Manual",
							  "1" : "1 - Auto"],
					title: "52. Auto or manual selection.\n" +
						   "This parameter is influencing on the software only when the value of parameter number 1 is set to value 3, 4 or 5.\n" +
						   "Available settings:\n" +
							"0 - Manual.\n" +
							"1 - Auto.\n" +
							"Default value: 0.\n"+
							"NOTE: In manual mode regulation (how the input influence on output) is disabled."

				input name: "param53", type: "enum", required: false,
					options: ["0" : "0 - (PID value equal ZERO)",
							  "1" : "1 - PID value set to LAST VALUE"],
					title: "53. PID value inside deadband.\n" +
						   "Available settings:\n" +
							"0 - (PID value equal ZERO).\n" +
							"1 - PID value set to LAST VALUE.\n" +
							"Default value: 0.\n"+
							"NOTE: When ZERO PID inside deadband is forced to zero. LASTVALUE means that PID remains on same level as was before entering into deadband."

				input name: "param54", type: "number", range: "0..100", required: false,
					title: "54. PID deadband.\n" +
						   "Available settings:\n" +
							"0 - 100 - 0% - 100%, step is 1%.\n" +
							"Default value: 1 (1%).\n" +
							"NOTE: This parameter defines the zone where PID is not active. If the temperature difference between actual and setpoint is bigger than PID deadband, then the PID will start to regulate the system, otherwise the PID is zero or fixed."

				input name: "param55", type: "number", range: "0..127", required: false,
					title: "55. Integral sampling time.\n" +
						   "Available settings:\n" +
							"0 - 127 - 0s to 127s, step is 1s.\n" +
							"Default value: 5 (5s).\n" +
							"Parameter defines the time between samples. On each sample the controller capture difference between SP-act.."

				input name: "param56", type: "number", range: "0..1000", required: false,
					title: "56. P parameter.\n" +
						   "Available settings:\n" +
							"0 -1000 - P value, step is 1.\n" +
							"Default value: 100."

				input name: "param57", type: "number", range: "0..1000", required: false,
					title: "57. I parameter.\n" +
						   "Available settings:\n" +
							"0 - 1000 - I value, step is 1.\n" +
							"Default value: 1."

				input name: "param58", type: "number", range: "0..1000", required: false,
					title: "58. D parameter.\n" +
						   "Available settings:\n" +
							"0 - 1000 - D value, step is 1.\n" +
							"Default value: 1."

				input name: "param60", type: "number", range: "1..98", required: false,
					title: "60. Minimum dimming value.\n" +
						   "Available settings:\n" +
							"1 - 98 = 1% - 98%, step is 1%. Minimum dimming values is set by entered value.\n" +
							"Default value: 1 (1%).\n" +
							"NOTE: The minimum level may not be higher than the maximum level! 1% min. dimming value is defined by Z-Wave multilevel device class. When the switch type is selected as Bi-stable, it is not possible to dim the value between min and max. If Switch_multilevel_set is set to the value “0”, the output is turned OFF. If Switch_multilevel_set is set to the value “1”, the output is set to the minimum diming value."

				input name: "param61", type: "number", range: "1..99", required: false,
					title: "61. Maximum dimming value.\n" +
						   "Available settings:\n" +
							"2 - 99 = 2% - 99%, step is 1%. Maximum dimming values is set by entered value.\n" +
							"Default value: 99 (99%).\n" +
							"NOTE: The maximum level may not be lower than the minimum level! 99% max. dimming value is defined by Z-Wave multilevel device class. When the switch type is selected as Bi-stable, it is not possible to dim the value between min and max."

				input name: "param65", type: "number", range: "50..255", required: false,
					title: "65. Dimming time (soft on/off).\n" +
						   "Set value means time of moving the Flush Dimmer 0-10V between min. and max. dimming values by short press of push button I1 or controlled through UI (BasicSet).\n" +
						   "Available settings:\n" +
							"50 - 255 = 500 mseconds - 2550 mseconds (2,55s), step is 10 mseconds.\n" +
							"Default value: 100 (1s).\n" +
							"NOTE: The maximum level may not be lower than the minimum level! 99% max. dimming value is defined by Z-Wave multilevel device class. When the switch type is selected as Bi-stable, it is not possible to dim the value between min and max."

				input name: "param66", type: "number", range: "1..255", required: false,
					title: "66. Dimming time when key pressed.\n" +
						   "Available settings:\n" +
							"1 - 255 = 1 second - 255 seconds.\n" +
							"Default value: 3 (3s)."

				input name: "param67", type: "enum", required: false,
					options: ["0" : "0 - (respect start level)",
							  "1" : "1 - (ignore start level)"],
					title: "67. Ignore start level.\n" +
						   "This parameter is used with association group 3.\n" +
						   "A receiving device SHOULD respect the start level if the Ignore Start Level bit is 0. A receiving device MUST ignore the start level if the Ignore Start Level bit is 1.\n" +
						   "Available settings:\n" +
							"0 - (respect start level).\n" +
							"1 - (ignore start level).\n" +
							"Default value: 0."

				input name: "param68", type: "number", range: "0..127", required: false,
					title: "68. Dimming time when key pressed.\n" +
						   "This parameter is used with association group 3.\n" +
						   "The Duration field MUST specify the time that the transition should take from the current value to the new target value. A supporting device SHOULD respect the specified Duration value.\n" +
						   "Available settings:\n" +
							"0 – 127 (from 1 to 127 seconds).\n" +
							"Default value: 0 (dimming duration according to parameter 66)."

				input name: "param110", type: "number", range: "1..32536", required: false,
					title: "110. Temperature sensor offset settings.\n" +
						   "Set value is added or subtracted to actual measured value by sensor..\n" +
						   "Available settings:\n" +
							"32536 - offset is 0.0C.\n" +
							"From 1 to 100 - value from 0.1 °C to 10.0 °C is added to actual measured temperature.\n" +
							"From 1001 to 1100 - value from -0.1 °C to -10.0 °C is subtracted to actual measured temperature.\n" +
							"Default value: 32536."

				input name: "param120", type: "number", range: "0..127", required: false,
					title: "120. Digital temperature sensor reporting.\n" +
						   "If digital temperature sensor is connected, module reports measured temperature on temperature change defined by this parameter.\n" +
						   "Available settings:\n" +
							"0 - Reporting disabled.\n" +
							"1 - 127 = 0,1°C - 12,7°C, step is 0,1°C.\n" +
							"Default value: 5 = 0,5°C change."

				input name: "param140", type: "number", range: "0..10000", required: false,
					title: "140. Input I1 Sensor reporting.\n" +
						   "If analogue sensor is connected, module reports measured value on change defined by this parameter.\n" +
						   "Available settings:\n" +
							"0 - Reporting disabled.\n" +
							"1 - 10000 = 0,1 - 1000 step is 0,1.\n" +
							"Default value: 5 = 0,5 change."

				input name: "param141", type: "number", range: "0..100", required: false,
					title: "141. Input I1 0-10V reporting threshold.\n" +
						   "Parameter is associated with Association group No. 2. Below this value, the Association No. 2 will report Basic Set 0xFF and above this value will report Basic Set 0xFF. Basic Set is reported only, when the input value changes for more than 10% (1V).\n" +
						   "Available settings:\n" +
							"0 - Reporting disabled.\n" +
							"1 - 100 - (0,1 - 10V).\n" +
							"Default value: 5 (0,5V)."

				input name: "param143", type: "number", range: "0..20000", required: false,
					title: "143. Minimum sensor range value.\n" +
						   "Value that must correspond to minimum sensor range value. Valid only if parameter 1 is set to values 3, 4 or 5.\n" +
						   "Available settings:\n" +
							"0 - 10000 – value from 0 to 1000  (resolution 0,1).\n" +
							"10001 – 20000 – value from -0,1 to  -1000  (resolution 0,1).\n" +
							"Default value: 0 = 0.0°C / 0Lux / 0.0%rh."

				input name: "param144", type: "number", range: "0..20000", required: false,
					title: "144. Maximum sensor range value.\n" +
						   "Value that must correspond to maximum sensor range value. Valid only if parameter 1 is set to values 3, 4 or 5.\n" +
						   "Available settings:\n" +
							"0 - 10000 – value from 0 to 1000  (resolution 0,1).\n" +
							"10001 – 20000 – value from -0,1 to  -1000  (resolution 0,1).\n" +
							"Default value: 1000 = 100.0°C / 100Lux / 100%rh."
							
/**
*			--------	ASSOCIATION GROUP SECTION	--------
*/
				input (
					type: "paragraph",
					element: "paragraph",
					title: "ASSOCIATION GROUPS:",
					description: "Singlechannel association settings."
				)
				input name: "assocGroup2", type: "text", required: false,
					title: "Association group 2: \n" +
						   "Basic On/Off command will be sent to associated nodes, according to the state of I1 input, when the module detects an I1 input state change.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup3", type: "text", required: false,
					title: "Association group 3: \n" +
						   "StartLevelChange or StopLevelChange command will be sent to associated nodes, according to the state of I1 input, when the module detects an I1 input state change.\n" +
						   "Not that the configuration parameter 1 needs to be set to monostable switch type for this group to work correctly." +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup4", type: "text", required: false,
					title: "Association group 4: \n" +
						   "Multilevel Switch Set command will be sent to associated nodes, according to the output state of the device, when the device's output state changes.\n" +
						   "Not that the configuration parameter 1 needs to be set to monostable switch type for this group to work correctly." +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup5", type: "text", required: false,
					title: "Association group 5: \n" +
						   "Multilevel Sensor Reports will be sent to associated nodes, according to the state of a connected analogue sensor.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input name: "assocGroup6", type: "text", required: false,
					title: "Association group 6: \n" +
						   "Multilevel Sensor Reports will be sent to associated nodes, according to the state of a connected temperature sensor.\n" +
						   "NOTE: Insert the node Id value of the devices you wish to associate this group with. Multiple nodeIds can also be set at once by separating individual values by a comma (2,3,...)."
						   
				input (
					type: "paragraph",
					element: "paragraph",
					title: "MULTICHANNEL ASSOCIATION GROUPS:",
					description: "Multichannel association settings. These are valid when the device is included with a temperature sensor or I1 is configured as a sensor."
				)
				input name: "ep1McAssocGroup2", type: "text", required: false,
					title: "MC Endpoint 1 Association group 2: \n" +
						   "Basic On/Off command will be sent to associated nodes, according to the state of I1 input, when the module detects an I1 input state change.\n" +
						   "NOTE: Insert the combination of Node Id / Endpoint Id values (for example; for node Id 4 and endpoint 2 the values should be entered as 4/2, if no endpoint should be used just the node Id is required, for example; 4) of the devices you wish to associate this group with. Multiple nodes and endpoints can also be set at once by separating individual values by a comma (3/2,4,5/3,...)."
						   
				input name: "ep1McAssocGroup3", type: "text", required: false,
					title: "MC Endpoint 1 Association group 3: \n" +
						   "StartLevelChange or StopLevelChange command will be sent to associated nodes, according to the state of I1 input, when the module detects an I1 input state change.\n" +
						   "Not that the configuration parameter 1 needs to be set to monostable switch type for this group to work correctly." +
						   "NOTE: Insert the combination of Node Id / Endpoint Id values (for example; for node Id 4 and endpoint 2 the values should be entered as 4/2, if no endpoint should be used just the node Id is required, for example; 4) of the devices you wish to associate this group with. Multiple nodes and endpoints can also be set at once by separating individual values by a comma (3/2,4,5/3,...)."
						   
				input name: "ep1McAssocGroup4", type: "text", required: false,
					title: "MC Endpoint 1 Association group 4: \n" +
						   "Multilevel Switch Set command will be sent to associated nodes, according to the output state of the device, when the device's output state changes.\n" +
						   "Not that the configuration parameter 1 needs to be set to monostable switch type for this group to work correctly." +
						   "NOTE: Insert the combination of Node Id / Endpoint Id values (for example; for node Id 4 and endpoint 2 the values should be entered as 4/2, if no endpoint should be used just the node Id is required, for example; 4) of the devices you wish to associate this group with. Multiple nodes and endpoints can also be set at once by separating individual values by a comma (3/2,4,5/3,...)."
						
				input name: "ep2McAssocGroup2", type: "text", required: false,
					title: "MC Endpoint 2 Association group 2: \n" +
						   "Multilevel Sensor Reports will be sent to associated nodes, according to the state of a connected analogue sensor.\n" +
						   "NOTE: Insert the combination of Node Id / Endpoint Id values (for example; for node Id 4 and endpoint 2 the values should be entered as 4/2, if no endpoint should be used just the node Id is required, for example; 4) of the devices you wish to associate this group with. Multiple nodes and endpoints can also be set at once by separating individual values by a comma (3/2,4,5/3,...)."
						  
				input name: "ep3McAssocGroup2", type: "text", required: false,
					title: "MC Endpoint 3 Association group 2: \n" +
						   "Multilevel Sensor Reports will be sent to associated nodes, according to the state of a connected temperature sensor.\n" +
						   "NOTE: Insert the combination of Node Id / Endpoint Id values (for example; for node Id 4 and endpoint 2 the values should be entered as 4/2, if no endpoint should be used just the node Id is required, for example; 4) of the devices you wish to associate this group with. Multiple nodes and endpoints can also be set at once by separating individual values by a comma (3/2,4,5/3,...)."
						
						  
	}
}
/**
*	--------	HELPER METHODS SECTION	--------
*/
/**
 * Converts a list of String type node id values to Integer type.
 *
 * @param stringList - a list of String type node id values.
 * @return stringList - a list of Integer type node id values.
*/
def convertStringListToIntegerList(stringList){
	log.debug stringList
	if(stringList != null){
		for(int i=0;i<stringList.size();i++){
			stringList[i] = stringList[i].toInteger()
		}
	}
	return stringList
}

private def logging(message, level) {

          log.debug "$message"

}

def poll() {
    logging("poll()", 1)
	command(zwave.switchMultilevelV1.switchMultilevelGet().format())
}

def refresh() {
    logging("refresh()", 1)
    poll()
} 

/**
 * Converts temperature values to fahrenheit or celsius scales according to user's setting.
 *
 * @param scaleParam user set scale parameter.
 * @param encapCmd received temperature parsed value.
 * @return String type value of the converted temperature value.
*/
def convertDegrees(scaleParam, encapCmd){
	switch (scaleParam) {
		default:
				break;
		case "F":
				if(encapCmd.scale == 1){
					return encapCmd.scaledSensorValue.toString()
				}else{
					return (encapCmd.scaledSensorValue * 9 / 5 + 32).toString()
				}
				break;
		case "C":
				if(encapCmd.scale == 0){
					return encapCmd.scaledSensorValue.toString()
				}else{
					return (encapCmd.scaledSensorValue * 9 / 5 + 32).toString()
				}
				break;
	}
}
/*
*	--------	HANDLE COMMANDS SECTION	--------
*/
/**
 * Configuration capability command handler.
 *
 * @param void
 * @return List of commands that will be executed in sequence with 500 ms delay inbetween.
*/
def configure() {
/*
	log.debug "Qubino Flush Dimmer 0-10V: configure()"
	def assocCmds = []
	assocCmds << zwave.associationV1.associationRemove(groupingIdentifier:1).format()
	assocCmds << zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId).format()
	return delayBetween(assocCmds, 500)
	*/
	
	log.debug "Qubino Flush Dimmer 0-10V: configure()"
	state.numEndpoints = 0
	log.debug state.numEndpoints
	def assocCmds = []
	assocCmds << zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId).format()
	assocCmds << zwave.multiChannelV3.multiChannelEndPointGet().format()
	return delayBetween(assocCmds, 500)
}
/**
 * Switch capability command handler for ON state. It issues a Switch Multilevel Set command with value 0xFF and instantaneous dimming duration.
 * This command is followed by a Switch Multilevel Get command, that updates the actual state of the dimmer.
 *		
 * @param void
 * @return void.
*/
def on() {
        delayBetween([
				zwave.switchMultilevelV3.switchMultilevelSet(value: 0xFF, dimmingDuration: 0x00).format(),
				zwave.switchMultilevelV1.switchMultilevelGet().format()
        ], 1000)  
}
/**
 * Switch capability command handler for OFF state. It issues a Switch Multilevel Set command with value 0x00 and instantaneous dimming duration.
 * This command is followed by a Switch Multilevel Get command, that updates the actual state of the dimmer.
 *		
 * @param void
 * @return void.
*/
def off() {
        delayBetween([
				zwave.switchMultilevelV3.switchMultilevelSet(value: 0x00, dimmingDuration: 0x00).format(),
				zwave.switchMultilevelV1.switchMultilevelGet().format()
        ], 1000)
}
/**
 * Switch Level capability command handler for a positive dimming state. It issues a Switch Multilevel Set command with value contained in the parameter value and instantaneous dimming duration.
 * This command is followed by a Switch Multilevel Get command, that updates the actual state of the dimmer. We need to limit the max valueto 99% by Z-Wave protocol definitions.
 *		
 * @param level The desired value of the dimmer we are trying to set.
 * @return void.
*/
def setLevel(level) {
	if(level > 99) level = 99
    delayBetween([
		zwave.switchMultilevelV3.switchMultilevelSet(value: level, dimmingDuration: 0x00).format(),
		zwave.switchMultilevelV1.switchMultilevelGet().format()
    ], 1000)
}
/**
 * setAssociations command handler that sets user selected association groups. In case no node id is insetred the group is instead cleared.
 * Lifeline association hidden from user influence by design.
 *
 * @param void
 * @return List of Association commands that will be executed in sequence with 500 ms delay inbetween.
*/

def setAssociation() {
	log.debug "Qubino Flush Dimmer 0-10V: setAssociation()"
	def assocSet = []
	
	//Singlechannel Association group section
	if(settings.assocGroup2 != null){
		def group2parsed = settings.assocGroup2.tokenize(",")
		if(group2parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:assocGroup2).format()
		}else{
			group2parsed = convertStringListToIntegerList(group2parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:2, nodeId:group2parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:2).format()
	}
	if(settings.assocGroup3 != null){
		def group3parsed = settings.assocGroup3.tokenize(",")
		if(group3parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:3, nodeId:assocGroup3).format()
		}else{
			group3parsed = convertStringListToIntegerList(group3parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:3, nodeId:group3parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:3).format()
	}
	if(settings.assocGroup4 != null){
		def group4parsed = settings.assocGroup4.tokenize(",")
		if(group4parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:4, nodeId:assocGroup4).format()
		}else{
			group4parsed = convertStringListToIntegerList(group4parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:4, nodeId:group4parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:4).format()
	}
	if(settings.assocGroup5 != null){
		def group5parsed = settings.assocGroup5.tokenize(",")
		if(group5parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:5, nodeId:assocGroup5).format()
		}else{
			group5parsed = convertStringListToIntegerList(group5parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:5, nodeId:group5parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:5).format()
	}
	if(settings.assocGroup6 != null){
		def group6parsed = settings.assocGroup6.tokenize(",")
		if(group6parsed == null){
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:6, nodeId:assocGroup6).format()
		}else{
			group6parsed = convertStringListToIntegerList(group6parsed)
			assocSet << zwave.associationV1.associationSet(groupingIdentifier:6, nodeId:group6parsed).format()
		}
	}else{
		assocSet << zwave.associationV2.associationRemove(groupingIdentifier:6).format()
	}
	
	if(settings.ep1McAssocGroup2 != null){
		def ep1McAssocGroup2Parsed = settings.ep1McAssocGroup2.tokenize(",")
		if(ep1McAssocGroup2Parsed != null){ //can contain numbers, number/number
			for (int i = 0; i < ep1McAssocGroup2Parsed.size(); i++) {
				if(ep1McAssocGroup2Parsed[i].contains("/")){ // multichannel combo of node//endpoint
					def ep1McAssocGroup2ParsedSplit = ep1McAssocGroup2Parsed[i].tokenize("/")
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 2, nodeId: [0,ep1McAssocGroup2ParsedSplit[0].toInteger(), ep1McAssocGroup2ParsedSplit[1].toInteger()])).format()
				}else{
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 2, nodeId: [ep1McAssocGroup2Parsed[i].toInteger()])).format()
				}
			}
		}
		else{
			assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 2, nodeId: [0])).format()
		}
	}
	else{
		assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 2)).format()
	}
	if(settings.ep1McAssocGroup3 != null){
		def ep1McAssocGroup3Parsed = settings.ep1McAssocGroup3.tokenize(",")
		if(ep1McAssocGroup3Parsed != null){ //can contain numbers, number/number
			for (int i = 0; i < ep1McAssocGroup3Parsed.size(); i++) {
				if(ep1McAssocGroup3Parsed[i].contains("/")){ // multichannel combo of node//endpoint
					def ep1McAssocGroup3ParsedSplit = ep1McAssocGroup3Parsed[i].tokenize("/")
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 3, nodeId: [0,ep1McAssocGroup3ParsedSplit[0].toInteger(), ep1McAssocGroup3ParsedSplit[1].toInteger()])).format()
				}else{
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 3, nodeId: [ep1McAssocGroup3Parsed[i].toInteger()])).format()
				}
			}
		}
		else{
			assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 3, nodeId: [0])).format()
		}
	}
	else{
		assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 3)).format()
	}
	if(settings.ep1McAssocGroup4 != null){
		def ep1McAssocGroup4Parsed = settings.ep1McAssocGroup4.tokenize(",")
		if(ep1McAssocGroup4Parsed != null){ //can contain numbers, number/number
			for (int i = 0; i < ep1McAssocGroup4Parsed.size(); i++) {
				if(ep1McAssocGroup4Parsed[i].contains("/")){ // multichannel combo of node//endpoint
					def ep1McAssocGroup4ParsedSplit = ep1McAssocGroup4Parsed[i].tokenize("/")
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 4, nodeId: [0,ep1McAssocGroup4ParsedSplit[0].toInteger(), ep1McAssocGroup4ParsedSplit[1].toInteger()])).format()
				}else{
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 4, nodeId: [ep1McAssocGroup4Parsed[i].toInteger()])).format()
				}
			}
		}
		else{
			assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 4, nodeId: [0])).format()
		}
	}
	else{
		assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 4)).format()
	}
	if(settings.ep2McAssocGroup2 != null){
		def ep2McAssocGroup2Parsed = settings.ep2McAssocGroup2.tokenize(",")
		if(ep2McAssocGroup2Parsed != null){ //can contain numbers, number/number
			for (int i = 0; i < ep2McAssocGroup2Parsed.size(); i++) {
				if(ep2McAssocGroup2Parsed[i].contains("/")){ // multichannel combo of node//endpoint
					def ep2McAssocGroup2ParsedSplit = ep2McAssocGroup2Parsed[i].tokenize("/")
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 2, nodeId: [0,ep2McAssocGroup2ParsedSplit[0].toInteger(), ep2McAssocGroup2ParsedSplit[1].toInteger()])).format()
				}else{
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 2, nodeId: [ep2McAssocGroup2Parsed[i].toInteger()])).format()
				}
			}
		}
		else{
			assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 2, nodeId: [0])).format()
		}
	}
	else{
		assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 2)).format()
	}

	if(settings.ep3McAssocGroup2 != null){
		def ep3McAssocGroup2Parsed = settings.ep3McAssocGroup2.tokenize(",")
		if(ep3McAssocGroup2Parsed != null){ //can contain numbers, number/number
			for (int i = 0; i < ep3McAssocGroup2Parsed.size(); i++) {
				if(ep3McAssocGroup2Parsed[i].contains("/")){ // multichannel combo of node//endpoint
					def ep3McAssocGroup2ParsedSplit = ep3McAssocGroup2Parsed[i].tokenize("/")
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:3).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 2, nodeId: [0,ep3McAssocGroup2ParsedSplit[0].toInteger(), ep3McAssocGroup2ParsedSplit[1].toInteger()])).format()
				}else{
					assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:3).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 2, nodeId: [ep3McAssocGroup2Parsed[i].toInteger()])).format()
				}
			}
		}
		else{
			assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:3).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 2, nodeId: [0])).format()
		}
	}
	else{
		assocSet << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:3).encapsulate(zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: 2)).format()
	}
	
	if(assocSet.size() > 0){
		return delayBetween(assocSet, 500)
	}
}

/**
 * setConfigurationParams command handler that sets user selected configuration parameters on the device. 
 * In case no value is set for a specific parameter the method skips setting that parameter.
 * Secure mode setting hidden from user influence by design.
 *
 * @param void
 * @return List of Configuration Set commands that will be executed in sequence with 500 ms delay inbetween.
*/

def setConfiguration() {
	log.debug "Qubino Flush Dimmer 0-10V: setConfiguration()"
	def configSequence = []
	if(settings.param1 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 1, size: 1, scaledConfigurationValue: settings.param1.toInteger()).format()
	}
	if(settings.param10 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 10, size: 2, scaledConfigurationValue: settings.param10.toInteger()).format()
	}
	if(settings.param11 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 11, size: 2, scaledConfigurationValue: settings.param11.toInteger()).format()
	}
	if(settings.param12 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 12, size: 2, scaledConfigurationValue: settings.param12.toInteger()).format()
	}
	if(settings.param21 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 21, size: 1, scaledConfigurationValue: settings.param21.toInteger()).format()
	}
	if(settings.param30 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 30, size: 1, scaledConfigurationValue: settings.param30.toInteger()).format()
	}
	if(settings.param52 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 52, size: 1, scaledConfigurationValue: settings.param52.toInteger()).format()
	}
	if(settings.param53 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 53, size: 1, scaledConfigurationValue: settings.param53.toInteger()).format()
	}
	if(settings.param54 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 54, size: 1, scaledConfigurationValue: settings.param54.toInteger()).format()
	}
	if(settings.param55 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 55, size: 1, scaledConfigurationValue: settings.param55.toInteger()).format()
	}
	if(settings.param56 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 56, size: 2, scaledConfigurationValue: settings.param56.toInteger()).format()
	}
	if(settings.param57 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 57, size: 2, scaledConfigurationValue: settings.param57.toInteger()).format()
	}
	if(settings.param58 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 58, size: 2, scaledConfigurationValue: settings.param58.toInteger()).format()
	}
	if(settings.param60 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 60, size: 1, scaledConfigurationValue: settings.param60.toInteger()).format()
	}
	if(settings.param61 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 61, size: 1, scaledConfigurationValue: settings.param61.toInteger()).format()
	}	
	if(settings.param65 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 65, size: 2, scaledConfigurationValue: settings.param65.toInteger()).format()
	}	
	if(settings.param66 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 66, size: 2, scaledConfigurationValue: settings.param66.toInteger()).format()
	}	
	if(settings.param67 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 67, size: 1, scaledConfigurationValue: settings.param67.toInteger()).format()
	}	
	if(settings.param68 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 68, size: 1, scaledConfigurationValue: settings.param68.toInteger()).format()
	}	
	if(settings.param110 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 110, size: 2, scaledConfigurationValue: settings.param110.toInteger()).format()
	}	
	if(settings.param120 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 120, size: 1, scaledConfigurationValue: settings.param120.toInteger()).format()
	}	
	if(settings.param140 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 140, size: 2, scaledConfigurationValue: settings.param140.toInteger()).format()
	}	
	if(settings.param141 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 141, size: 1, scaledConfigurationValue: settings.param141.toInteger()).format()
	}	
	if(settings.param143 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 143, size: 2, scaledConfigurationValue: settings.param143.toInteger()).format()
	}	
	if(settings.param144 != null){
		configSequence << zwave.configurationV1.configurationSet(parameterNumber: 144, size: 2, scaledConfigurationValue: settings.param144.toInteger()).format()
	}	
	if(configSequence.size() > 0){
		return delayBetween(configSequence, 500)
	}
}

/*
*	--------	EVENT PARSER SECTION	--------
*/
/**
 * parse function takes care of parsing received bytes and passing them on to event methods.
 *
 * @param description String type value of the received bytes.
 * @return Parsed result of the received bytes.
*/
def parse(String description) {
	log.debug "Qubino Flush Dimmer 0-10V: Parsing '${description}'"
	def result = null
    def cmd = zwave.parse(description)
    logging("Raw Report: $cmd",99)
    if (cmd) {
		result = zwaveEvent(cmd)
        log.debug "Parsed ${cmd} to ${result.inspect()}"
        logging("Zwave Result: $result",99)
    } else {
		log.debug "Non-parsed event: ${description}"
    }
    return result
}
/**
 * Event handler for received Sensor Multilevel Report frames. These are for the temperature sensor connected to TS connector.
 *
 * @param void
 * @return Event that updates the temperature values with received values.
*/
def zwaveEvent(hubitat.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd){
	log.debug "Qubino Flush Dimmer 0-10V: SensorMultilevelReport handler fired"
	
	def resultEvents = []
	resultEvents << createEvent(name:"temperature", value: convertDegrees(location.temperatureScale,cmd), unit:"°"+location.temperatureScale, descriptionText: "Temperature: "+convertDegrees(location.temperatureScale,cmd)+"°"+location.temperatureScale)
	return resultEvents
	
}
/**
 * Event handler for received Switch Multilevel Report frames.
 *
 * @param void
 * @return List of events to update the ON / OFF and analogue control elements with received values.
*/
def zwaveEvent(hubitat.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd){
	log.debug "firing switch multilevel event"
	def result = []
	result << createEvent(name:"switch", value: cmd.value ? "on" : "off")
	result << createEvent(name:"level", value: cmd.value, unit:"%", descriptionText:"${device.displayName} dimmed to ${cmd.value==255 ? 100 : cmd.value}%")
	return result
}
/**
 * Event handler for received Switch Binary Report frames. Used for ON / OFF events.
 *
 * @param void
 * @return Switch Event with on or off value.
*/
def zwaveEvent(hubitat.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
	log.debug "firing switch binary report event"
    createEvent(name:"switch", value: cmd.value ? "on" : "off")
}
/**
 * Event handler for received Configuration Report frames. Used for debugging purposes. 
 *
 * @param void
 * @return void.
*/
def zwaveEvent(hubitat.zwave.commands.configurationv2.ConfigurationReport cmd){
	log.debug "firing configuration report event"
	log.debug cmd
	log.debug cmd.scaledConfigurationValue
	if(cmd.parameterNumber == 1){
		if(cmd.scaledConfigurationValue > 2){
			log.debug "configurable input sensor enabled"
			state.i1SensorConnected = true
		}
	}
}
/**
 * Event handler for received MultiChannelEndPointReport commands. Used to distinguish when the device is in singlechannel or multichannel configuration. 
 *
 * @param cmd communication frame
 * @return commands to set up a MC Lifeline association.
*/
def zwaveEvent(hubitat.zwave.commands.multichannelv3.MultiChannelEndPointReport cmd){
	log.debug "Qubino Flush Shutter DC: firing MultiChannelEndPointReport"
	if(cmd.endPoints > 0){
		state.numEndpoints = cmd.endPoints;
	}

	def cmds = []
	cmds << response(zwave.associationV1.associationRemove(groupingIdentifier:1).format())
	cmds << response(zwave.configurationV1.configurationGet(parameterNumber: 1).format())
	cmds << response(zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: 1, nodeId: [0,zwaveHubNodeId,1]).format())
	return cmds
}
/**
 * Event handler for received MC Encapsulated Switch Multilevel Report frames.
 *
 * @param cmd communication frame, command mc encapsulated communication frame; needed to distinguish sources
 * @return List of events to update the ON / OFF and analogue control elements with received values.
*/
def zwaveEvent(hubitat.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd, hubitat.zwave.commands.multichannelv3.MultiChannelCmdEncap command){
	log.debug "Qubino Flush Shutter DC: firing MC sensor multilevel event"
	def result = []
	switch(command.sourceEndPoint){
		case 1:
			result << createEvent(name:"temperature", value: convertDegrees(location.temperatureScale,cmd), unit:"°"+location.temperatureScale, descriptionText: "Temperature: "+convertDegrees(location.temperatureScale,cmd)+"°"+location.temperatureScale, isStateChange: true)
		break;
		case 2: 
			if(state.i1SensorConnected){
				log.debug "I1 sensor enabled"
                log.debug "Got Sensor: $cmd"
				result << createEvent(name:"i1Sensor", value: cmd.scaledSensorValue, descriptionText: "I1 sensor: "+cmd.scaledSensorValue, isStateChange: true)
			}else{
				result << createEvent(name:"temperature", value: convertDegrees(location.temperatureScale,cmd), unit:"°"+location.temperatureScale, descriptionText: "Temperature: "+convertDegrees(location.temperatureScale,cmd)+"°"+location.temperatureScale, isStateChange: true)
			}
		break;
		case 3:
			result << createEvent(name:"temperature", value: convertDegrees(location.temperatureScale,cmd), unit:"°"+location.temperatureScale, descriptionText: "Temperature: "+convertDegrees(location.temperatureScale,cmd)+"°"+location.temperatureScale, isStateChange: true)
		break;
	}
	return result
}
/**
 * Event handler for received MC Encapsulated Switch Multilevel Report frames.
 *
 * @param void
 * @return List of events to update the ON / OFF and analogue control elements with received values.
*/
def zwaveEvent(hubitat.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd, hubitat.zwave.commands.multichannelv3.MultiChannelCmdEncap command){
	log.debug "firing switch multilevel event"
	log.debug state.numEndpoints
	def result = []
	result << createEvent(name:"switch", value: cmd.value ? "on" : "off")
	result << createEvent(name:"level", value: cmd.value, unit:"%", descriptionText:"${device.displayName} dimmed to ${cmd.value==255 ? 100 : cmd.value}%")
	return result
}
/**
 * Event handler for received Multi Channel Encapsulated commands.
 *
 * @param cmd encapsulated communication frame
 * @return parsed event.
*/
def zwaveEvent(hubitat.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd){
	log.debug "Qubino Flush Shutter DC: firing MC Encapsulation event"
	def encapsulatedCommand = cmd.encapsulatedCommand()
	//log.debug ("Command from endpoint ${cmd.sourceEndPoint}: ${encapsulatedCommand}")
	if (encapsulatedCommand) {
			return zwaveEvent(encapsulatedCommand, cmd)
	}
}
