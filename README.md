## Building a new gatling instance in the cloud

1. Clone a current gatling instance
2. SSH into the new gatling instance
3. Install required dependencies using command: `sudo apt update && sudo apt upgrade -y && sudo apt install git openjdk-8-jre -y`
1. clone the git repository into the `/home/alex/` folder
1. add the new instance to the file `run-publift-gatling-cluster.sh`

You can run the script with the following options
* `first` tells the script this is the first time you are running the test today and it will start the servers
* `last` tells the script this is the last time you are running the test today and it will stop the servers after finishing
* `single` tells the script this is the only time you are running the test today and it will start the servers and stop them after finishing
