from browserstack_common import *
from collections import defaultdict
import subprocess
import os
import sys
import json

iterations = 5
timeAnalysisLabel = "SDKPerformanceAnalysisTime"
memoryAnalysisLabel = "SDKPerformanceAnalysisMemory"

def extract_statistics(device_logs):
    # At this point, we have something like:
    # 2025-07-07 16:19:06.494 +0000 D/SDKPerformanceAnalysisTime(20150): Wipe data and dowload again: 14797 ms
    # 2025-07-07 16:19:06.494 +0000 D/SDKPerformanceAnalysisTime(20150): Delete data and push changes: 5150 ms
    # 2025-07-07 16:19:06.494 +0000 D/SDKPerformanceAnalysisMemory(20150): D2 Instantiation: 11 MB
    # 2025-07-07 16:19:06.495 +0000 D/SDKPerformanceAnalysisMemory(20150): D2 Login: 4 MB

    log_lines = device_logs.split("\n")

    statistics = {
        "time": {},
        "memory": {}
    }

    for line in log_lines:
        if timeAnalysisLabel in line:
            tokens = line.split(timeAnalysisLabel)[1].split(": ")
            key = tokens[1]
            value = tokens[2]
            statistics['time'][key] = int(value.split(' ')[0])

        elif memoryAnalysisLabel in line:
            tokens = line.split(memoryAnalysisLabel)[1].split(": ")
            key = tokens[1]
            value = tokens[2]
            statistics['memory'][key] = int(value.split(' ')[0])

    return statistics


def run_iteration(app_apk_path, test_apk_path, config):
    username = os.environ.get('BROWSERSTACK_USR')
    password = os.environ.get('BROWSERSTACK_PSW')
    client = BrowserstackClient(username, password)

    app_url = client.upload_app_apk(app_apk_path)
    test_url = client.upload_test_apk(test_apk_path)
    
    test_config = {
        "app": app_url,
        "testSuite": test_url,
        "devices": [config['browserstack_device_list']],
        "package": [config['browserstack_package_benchmark']],
        "video": config['browserstack_video'],
        "local": config['browserstack_local'],
        "deviceLogs": config['browserstack_deviceLogs'],
        "allowDeviceMockServer": config['browserstack_mock_server'],
        "singleRunnerInvocation": config['browserstack_singleRunnerInvocation']
    }

    build_id = client.execute_build(test_config)
    build_info = client.wait_for_build_finish(build_id, config)
    device_logs = client.get_device_logs(build_info, config)

    return extract_statistics(device_logs)

def format_results(iteration_results):
    combined = {
        "time": defaultdict(list),
        "memory": defaultdict(list)
    }

    for entry in iteration_results:
        for key in ['time', 'memory']:
            for metric, value in entry[key].items():
                combined[key][metric].append(value)

    # Convert defaultdicts back to normal dicts
    combined["time"] = dict(combined["time"])
    combined["memory"] = dict(combined["memory"])
    return combined


def main():
    log_file = sys.argv[1] if len(sys.argv) > 1 else None

    script_dir = os.path.dirname(os.path.abspath(__file__))
    benchmark_config_file = os.path.join(script_dir, '..', 'instrumented-test-app', 'src', 'androidTest', 'assets', 'benchmark.json')
    benchmark_config_file = os.path.normpath(benchmark_config_file)

    SERVER_URL = os.environ.get('BENCHMARK_SERVER_URL') or 'https://play.dhis2.org/demo'
    USERNAME = os.environ.get('BENCHMARK_USERNAME') or 'android'
    PASSWORD = os.environ.get('BENCHMARK_PASSWORD') or 'Android123'

    benchmark_config = {
        "serverUrl": SERVER_URL,
        "username": USERNAME,
        "password": PASSWORD
    }
    
    # Write config to JSON file
    with open(benchmark_config_file, 'w') as f:
        json.dump(benchmark_config, f, indent=2)

    # Run gradle build commands
    subprocess.run(['./gradlew', ':core:assembleDebug'], check=True)

    SDK_VERSION = os.environ.get('SDK_VERSION', '')
    subprocess.run(['./gradlew', ':instrumented-test-app:assembleDebugAndroidTest', f'-PsdkVersion={SDK_VERSION}'], check=True)
    subprocess.run(['./gradlew', ':instrumented-test-app:assembleDebug', f'-PsdkVersion={SDK_VERSION}'], check=True)

    app_apk_path = find_apk_path("instrumented-test-app", "debug")
    test_apk_path= find_apk_path("instrumented-test-app", "androidTest/debug")

    config_file = os.path.join(script_dir, 'config_jenkins.init')
    config = parse_config_file(config_file)

    results = []
    for i in range(iterations):
        print(f"Running iteration #{i}")
        iteration_result = run_iteration(app_apk_path, test_apk_path, config)
        results.append(iteration_result)

    combined = format_results(results)

    with open(log_file, "a") as f:
        f.write("# Benchmark result\n")
        f.write(f"**SDK Version:** {SDK_VERSION or 'current'}\n")
        f.write("\n")
        f.write(f"**Server url:** {SERVER_URL}\n")
        
        f.write("## Time Metrics\n")
        f.write(f"|Operation|{f' | '.join(f'#{i}' for i in range(1, iterations + 1))}| Average (ms) |\n")
        f.write(f"| - |{f'- |' * iterations} - |\n")
        for key, values in combined['time'].items():
            average = int(sum(values) / len(values))
            f.write(f"|{key} | {' | '.join(map(str, values))} | {average} |\n")
        f.write("\n")
        
        f.write("## Memory Metrics\n")
        f.write(f"|Operation|{f' | '.join(f'#{i}' for i in range(1, iterations + 1))}| Average (MB) |\n")
        f.write(f"| - |{f'- |' * iterations} - |\n")
        for key, values in combined['memory'].items():
            average = int(sum(values) / len(values))
            f.write(f"|{key} | {' | '.join(map(str, values))} | {average} |\n")


if __name__ == '__main__':
    main()