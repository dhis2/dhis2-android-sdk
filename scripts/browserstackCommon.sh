#!/bin/bash

#
#  Copyright (c) 2004-2025, University of Oslo
#  All rights reserved.
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions are met:
#  Redistributions of source code must retain the above copyright notice, this
#  list of conditions and the following disclaimer.
#
#  Redistributions in binary form must reproduce the above copyright notice,
#  this list of conditions and the following disclaimer in the documentation
#  and/or other materials provided with the distribution.
#  Neither the name of the HISP project nor the names of its contributors may
#  be used to endorse or promote products derived from this software without
#  specific prior written permission.
#
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
#  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
#  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
#  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
#  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
#  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
#  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
#  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
#  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
#  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

bs_automate_url="https://api-cloud.browserstack.com/app-automate"
bs_auth="$BROWSERSTACK_USR:$BROWSERSTACK_PSW"

function upload_app_apk() {
  local apk_path=$1
  upload_app_response="$(curl -u "$bs_auth" -X POST $bs_automate_url/upload -F file=@$apk_path)"
  echo "$upload_app_response" | jq .app_url
}

function upload_test_apk() {
  local apk_path=$1
  upload_test_response="$(curl -u "$bs_auth" -X POST $bs_automate_url/espresso/test-suite -F file=@$apk_path)"
  echo "$upload_test_response" | jq .test_url
}

function execute_build() {
  local build_json_config=$1
  test_execution_response="$(curl -u "$bs_auth" -X POST $bs_automate_url/espresso/v2/build -d \ "$build_json_config" -H "Content-Type: application/json")"
  # Get build id
  echo "$test_execution_response" | jq -r .build_id
}

function get_build_info() {
  local response
  local build_id=$1
  response="$(curl -u "$bs_auth" -X GET "$bs_automate_url/espresso/builds/$build_id")"
  echo "$response"
}

function get_build_status() {
  local response_json=$1
  echo "$response_json" | jq -r .status
}

function get_build_session() {
  local response_json=$1
  echo "$response_json" | jq -r ".devices.$browserstack_device_list.session_id"
}

function get_build_session_details_url() {
  local response_json=$1
  echo "$response_json" | jq -r ".devices.$browserstack_device_list.session_details"
}

function get_device_logs() {
  local response
  local session_details_url=$1
  response="$(curl -u "$bs_auth" -X GET "$session_details_url")"
  echo "$response" | jq -r ".test_details[] | .[] | .device_log"
}

function findApkPath() {
  local project=$1
  local folder=$2
  find "$(dirname $0)"/../"$project"/build/outputs/apk/"$folder" -iname "*.apk"
}

function waitForBuildFinish() {
  local build_id=$1
  # Monitor build status
  build_status="running"
  sleep $build_time_average

  while [[ $build_status = "running" ]];
  do
    # Get build status
    build_status_response="$(get_build_info "$build_id")"
    build_status="$(get_build_status "$build_status_response")"
    build_session_id="$(get_build_session "$build_status_response")"

    # Sleep until next poll
    sleep $polling_interval
  done

  echo $build_status_response
}