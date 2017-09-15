#!/bin/bash

##################################################################################################################
#Gatling scale out/cluster run script:
#Before running this script some assumptions are made:
#1) Public keys were exchange inorder to ssh with no password promot (ssh-copy-id on all remotes)
#2) Check  read/write permissions on all folders declared in this script.
#3) Gatling installation (HOME variable) is the same on all hosts
#4) Assuming all hosts has the same user name (if not change in script)
##################################################################################################################

#Assuming same user name for all hosts
USER_NAME='alex'

#Remote hosts list
declare -A GCLOUD_HOST1=( 
  [hostname]="gatling-test-1"
  [zone]="australia-southeast1-a"
)

declare -A GCLOUD_HOST2=(
  [hostname]="gatling-test-2"
  [zone]="australia-southeast1-b"
)

declare -A GCLOUD_HOST3=(
  [hostname]="gatling-test-3"
  [zone]="australia-southeast1-c"
)

#Gatling root locations
SERVER_HOME=/home/alex/puglift-gatling-test
LOCAL_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SIMULATIONS_DIR=user-files/simulations
RUNNER=bin/gatling.sh
REPORT_DIR=results
GATHER_REPORTS_DIR=reports

#LOCALHOST locations
LOCAL_SIMULATIONS_DIR=$LOCAL_HOME/$SIMULATIONS_DIR
LOCAL_RUNNER=$LOCAL_HOME/$RUNNER
LOCAL_REPORT_DIR=$LOCAL_HOME/$REPORT_DIR
LOCAL_GATHER_REPORTS_DIR=$LOCAL_HOME/$GATHER_REPORTS_DIR

#Remote locations
SERVER_SIMULATIONS_DIR=$SERVER_HOME/$SIMULATIONS_DIR
SERVER_RUNNER=$SERVER_HOME/$RUNNER
SERVER_REPORT_DIR=$SERVER_HOME/$REPORT_DIR
SERVER_GATHER_REPORTS_DIR=$SERVER_HOME/$GATHER_REPORTS_DIR

#Change to your simulation class name
SIMULATION_NAME='publift.HttpStressSimulation'

echo "Starting Gatling cluster run for simulation: $SIMULATION_NAME"

for id in "${!GCLOUD_HOST@}"
do
  declare -n START_HOST=$id
  echo "Starting host: ${START_HOST[hostname]}"
  gcloud compute instances start ${START_HOST[hostname]} --zone ${START_HOST[zone]}
done

echo "Cleaning previous runs from localhost"
rm -rf $LOCAL_GATHER_REPORTS_DIR
mkdir $LOCAL_GATHER_REPORTS_DIR
rm -rf $LOCAL_REPORT_DIR

for id in "${!GCLOUD_HOST@}"
do
  declare -n CLEAN_HOST=$id
  echo "Cleaning previous runs from host: ${CLEAN_HOST[hostname]}"
  gcloud compute ssh --ssh-flag="-n" --ssh-flag="-f" --zone ${CLEAN_HOST[zone]} $USER_NAME@${CLEAN_HOST[hostname]} --command "sh -c 'rm -rf $SERVER_REPORT_DIR'"
done

for id in "${!GCLOUD_HOST@}"
do
  declare -n COPY_HOST=$id
  echo "Copying simulations to host: ${COPY_HOST[hostname]}"
  gcloud compute scp --scp-flag="-r" $LOCAL_SIMULATIONS_DIR/* $USER_NAME@${COPY_HOST[hostname]}:$SERVER_SIMULATIONS_DIR --zone ${COPY_HOST[zone]}
done

for id in "${!GCLOUD_HOST@}"
do
  declare -n RUN_HOST=$id
  echo "Running simulation on host: ${RUN_HOST[hostname]}"
  gcloud compute ssh --ssh-flag="-n" --ssh-flag="-f" --zone ${RUN_HOST[zone]} $USER_NAME@${RUN_HOST[hostname]} --command "sh -c 'nohup $SERVER_RUNNER -nr -s $SIMULATION_NAME > $SERVER_HOME/run.log 2>&1 &'"
done

echo "Running simulation on localhost"
$LOCAL_RUNNER -nr -s $SIMULATION_NAME

echo "Gathering result file from localhost"
ls -t $LOCAL_REPORT_DIR | head -n 1 | xargs -I {} mv ${LOCAL_REPORT_DIR}/{} ${LOCAL_REPORT_DIR}/report
cp ${LOCAL_REPORT_DIR}/report/simulation.log $LOCAL_GATHER_REPORTS_DIR

for id in "${!GCLOUD_HOST@}"
do
  declare -n GATHER_HOST=$id
  echo "Gathering result file from host: ${GATHER_HOST[hostname]}"
  gcloud compute ssh --ssh-flag="-n" --ssh-flag="-f" --zone ${GATHER_HOST[zone]} $USER_NAME@${GATHER_HOST[hostname]} --command "sh -c 'ls -t $SERVER_REPORT_DIR | head -n 1 | xargs -I {} mv ${SERVER_REPORT_DIR}/{} ${SERVER_REPORT_DIR}/report'"
  gcloud compute scp --scp-flag="-r" $USER_NAME@${GATHER_HOST[hostname]}:${SERVER_REPORT_DIR}/report/simulation.log ${LOCAL_GATHER_REPORTS_DIR}/simulation-${GATHER_HOST[hostname]}.log --zone ${GATHER_HOST[zone]}
done

for id in "${!GCLOUD_HOST@}"
do
  declare -n STOP_HOST=$id
  echo "Stopping host: ${STOP_HOST[hostname]}"
  gcloud compute instances stop ${STOP_HOST[hostname]} --zone ${STOP_HOST[zone]}
done

mv $LOCAL_GATHER_REPORTS_DIR $LOCAL_REPORT_DIR
echo "Aggregating simulations"
$LOCAL_RUNNER -ro reports
