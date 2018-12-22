# Airfoil Speaker Driver

I created this driver to control airfoil speakers on my network via hubitat.

## Pre-requisites

* [Airfoil](https://rogueamoeba.com/airfoil/)
* [Airfoil API](https://github.com/jnewland/airfoil-api)

## Install

Copy the groovy file contents into a new driver


## Configuration

You will need
Ip/Port address of your airfoil endpoint

After saving these preferences the driver will fetch a list of speakers for you to configure against.

### Caveat

The airfoil api uses mac os bindings (applescript), there is support in airfoil for the windows COM api however, I do not know if anyone has an api wrapper for it.
