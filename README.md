## Running Gatling locally and making changes

Please see the detailed documentation in the `puglift` repository

## Running Gatling in the cloud
> **NOTE**
> Google Cloud has a limit on the number of simultaneous connections per client so you need to connect to google with at least 4 clients to get any meaningful scale on the app.

### Linux

1. Ensure that the first line of the file `run-publift-gatling-cluster.sh` is `#!/bin/bash` 
1. From the root directory type `./run-publift-gatling-cluster.sh [arg1]`

### Mac

1. Please [install bash v4 via Homebrew](http://clubmate.fi/upgrade-to-bash-4-in-mac-os-x/).
1. Change the first line of the file `run-publift-gatling-cluster.sh` to `#!/usr/local/bin/bash`

### Windows

1. Enable and install [Windows Subsystem for Linux](https://msdn.microsoft.com/en-au/commandline/wsl/about)
1. Navigate to `puglift-gatling-test` inside your new bash terminal
1. Ensure that the first line of the file `run-publift-gatling-cluster.sh` is `#!/bin/bash` 
1. From the root directory type `./run-publift-gatling-cluster.sh [arg1]`

### Optional argument

> **NOTE** if the gcloud Gatling instances are not running and you do not specify `first` or `single` the test will not execute properly. There is no failure warning until the end of the test.

You can run the script with the following options
* `first` tells the script this is the first time you are running the test today and it will start the servers
* `last` tells the script this is the last time you are running the test today and it will stop the servers after finishing
* `single` tells the script this is the only time you are running the test today and it will start the servers and stop them after finishing

## Building a new gatling instance in the cloud

1. Clone a current gatling instance
2. SSH into the new gatling instance
3. Install required dependencies using command: `sudo apt update && sudo apt upgrade -y && sudo apt install git openjdk-8-jre -y`
1. clone the git repository into the `/home/alex/` folder
1. add the new instance to the file `run-publift-gatling-cluster.sh`

