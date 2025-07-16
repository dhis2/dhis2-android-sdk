import requests
import os
import glob
import ast
import time

def find_apk_path(project, folder):
    script_dir = os.path.dirname(os.path.abspath(__file__))
    search_path = os.path.join(script_dir, '..', project, 'build', 'outputs', 'apk', folder, '**', '*.apk')
    apk_files = glob.glob(search_path, recursive=True)
    return apk_files[0]

def parse_config_file(filepath):
    config = {}

    with open(filepath, 'r') as f:
        for line in f:
            line = line.strip()
            key, value = line.split('=', 1)

            # Remove surrounding quotes if present
            value = value.strip()

            # Convert booleans, numbers, or safely evaluate quoted strings
            if value.lower() == 'true':
                config[key] = True
            elif value.lower() == 'false':
                config[key] = False
            elif value.isdigit():
                config[key] = int(value)
            elif (value.startswith('"') and value.endswith('"')) or \
                 (value.startswith("'") and value.endswith("'")):
                # Evaluate quoted string, including escaped quotes
                config[key] = ast.literal_eval(value).replace("\"", "")
            else:
                config[key] = value  # Fallback as string

    return config


class BrowserstackClient:

    bs_automate_url = "https://api-cloud.browserstack.com/app-automate"

    def __init__(self, username, password):
        self.username = username
        self.password = password

    def get_session(self):
        session = requests.Session()
        session.auth = (self.username, self.password)
        return session

    def get_build_info(self, build_id):
        url = self.bs_automate_url + "/espresso/builds/" + build_id
        
        return self.get_session().get(url).json()

    def get_device_logs(self, build_info, config):
        session_details_url = build_info['devices'][config['browserstack_device_list']]['session_details']

        session = self.get_session()

        test_details = session.get(session_details_url).json()['test_details']

        package_key = next(iter(test_details))
        package_details = test_details[package_key]
        test_key = next(iter(package_details))
        device_logs_url = package_details[test_key]['device_log']

        return session.get(device_logs_url).text
    
    def upload_app_apk(self, apk_path):
        session = self.get_session()
        url = self.bs_automate_url + "/upload"
        with open(apk_path, 'rb') as f:
            response = session.post(
                url,
                files={'file': f}
            )

        response.raise_for_status()  # Raise exception for HTTP errors
        app_url = response.json().get('app_url')
        return app_url
    
    def upload_test_apk(self, apk_path):
        session = self.get_session()
        url = self.bs_automate_url + "/espresso/test-suite"
        with open(apk_path, 'rb') as f:
            response = session.post(
                url,
                files={'file': f}
            )

        response.raise_for_status()
        test_url = response.json().get('test_url')
        return test_url
    
    def execute_build(self, test_config):
        session = self.get_session()
        url = self.bs_automate_url + "/espresso/v2/build"

        headers = {"Content-Type": "application/json"}

        response = session.post(
            url,
            headers=headers,
            json=test_config
        )

        response.raise_for_status()
        build_id = response.json().get("build_id")
        return build_id
    
    def wait_for_build_finish(self, build_id, config):
        build_status = "running"
        time.sleep(config['build_time_average'])

        build_status_response = None

        while build_status == "running":
            build_status_response = self.get_build_info(build_id)
            build_status = build_status_response['status']

            time.sleep(config['polling_interval'])

        return build_status_response