/*




*/

metadata 
{
    definition(
        name: "Broadlinkgo Parent v2", 
        namespace: "ra", 
        author: "Rob Alfonso") 
    {
        capability "Configuration"
        command "reset"
        command "createChildren"     
    }
}

preferences 
{
    section 
    {
        input   (
                name: "broadlinkgoIP",
                type: "string",
                title: "broadlinkgo IP",
                description: "IP address of broadlinkgo service", 
                required: true, 
                displayDuringSetup: false, 
                defaultValue: "127.0.0.1")
        input   (
                name: "broadlinkgoPort",
                type: "string",
                title: "broadlinkgo Port",
                description: "Port of broadlinkgo service", 
                required: true, 
                displayDuringSetup: false, 
                defaultValue: "8000")
        input   (
                name: "DebugLogging",
                type: "bool",
                title: "Debug Logging",
                description: "Enable Debug Logging?", 
                required: true, 
                displayDuringSetup: false, 
                defaultValue: true)
    }
}

def logDebug(msg) 
{
	if (DebugLogging)
    {
		log.debug(msg)	
	}
}


def installed()
{
    logDebug("broadlinkgo parent: installed")
    sendEvent(name: "children", value:"n/a", display: true, displayed: true)
}

def updated()
{
	logDebug("broadlinkgo parent: updated")
    // assume that the broadlinkgo server was changed if preferences were updated
    // so, nuke it and start over
    reset()
    configure()
}

	
def uninstalled()
{
	logDebug("broadlinkgo parent: uninstalled")
    reset()
}

def reset()
{
    logDebug("broadlinkgo parent: reset(). deleting all child devices.")
    for(child in getChildDevices())
    {
        deleteChildDevice(child.deviceNetworkId)
    }
    
    state.clear()
    clearMacroList()
}

def configure()
{
    logDebug("broadlinkgo parent: configure")    
    reset()
    //createChildren()
}

def createChild(String remote, String macAddr, String command,String label)
{
    logDebug("Broadlinkgo Parent: createChild(${command} on device ${macAddr})")
    def childDev = addChildDevice("ra", "Broadlinkgo Child", "${remote}-${label}", [label:"${remote}-${label}", isComponent:true, name:"${remote}-${label}"])
    
    // update URI settings for switch, storing in both "on" and "off" so that "toggle" will have the intended effect regardless
    childDev.updateSetting("onPress", "${broadlinkgo_cmdmsg(command)}")
    childDev.updateSetting("logEnable", false)
}

def createChildren()
{
    logDebug("Broadlinkgo Parent: createChildren")    
    sendEvent(name: "children", value:"Creating...", display: true, displayed: true)
    try
    {
        httpGet([
            uri:"${broadlinkgo_statusmsg()}",
            contentType: "application/json"
            ]
         ){ resp ->
            if (resp?.data) {
            resp.data.payload.remotes.each {
                it.Buttons.each{ 
                   but ->
                   logDebug("Creating Remote: Name:${it.Label} Device:${it.Device} Cmd: ${but.Command} Label: ${but.Label}")
                   createChild("${it.Label}","${it.Device}","${but.Command}","${but.Label}")
                }   
            }
            
            }
        }
    }
    catch (Exception e)
    {
        log.warn "GET failed with message: ${e.message}"
        return
    }
}




private broadlinkgo_IPandPort()
{
    return "${settings.broadlinkgoIP}:${settings.broadlinkgoPort}"
}                            
                            
private broadlinkgo_statusmsg()
{
    return "http://" + "${broadlinkgo_IPandPort()}" + "/api/remotes"
}

private broadlinkgo_cmdmsg(String command)
{
    return "http://" + "${broadlinkgo_IPandPort()}" + "${command}"
}

