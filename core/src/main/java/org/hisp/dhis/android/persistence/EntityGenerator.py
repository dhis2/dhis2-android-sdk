import os
import re
import csv

# Paths
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
SNAPSHOT_PATH = os.path.abspath(os.path.join(SCRIPT_DIR, '../../../../../../assets/snapshots/snapshot.sql'))
PERSISTENCE_DIR = SCRIPT_DIR  # This script is in the persistence folder
CSV_PATH = os.path.join(SCRIPT_DIR, 'tabla_comb_2.csv')

CREATE_TABLE_REGEX = re.compile(r'^CREATE TABLE ([A-Za-z0-9_]+)')

def read_csv_table_folder_map(csv_path):
    """Returns a dict: table_name -> first folder in path_domain_model."""
    table_folder = {}
    with open(csv_path, newline='', encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile, delimiter=';')
        for row in reader:
            table = row['nombre_tabla']
            path = row['path_domain_model']
            first_folder = path.split('/')[0] if '/' in path else os.path.splitext(path)[0]
            table_folder[table] = first_folder
    return table_folder

def main():
    table_to_folder = read_csv_table_folder_map(CSV_PATH)
    if not os.path.isfile(SNAPSHOT_PATH):
        print(f"snapshot.sql not found at {SNAPSHOT_PATH}")
        return

    with open(SNAPSHOT_PATH, encoding='utf-8') as sqlfile:
        for line in sqlfile:
            match = CREATE_TABLE_REGEX.match(line.strip())
            if not match:
                continue
            table_name = match.group(1)
            folder_name = table_to_folder.get(table_name)
            if not folder_name:
                print(f"No folder mapping for table {table_name}")
                continue
            folder_path = os.path.join(PERSISTENCE_DIR, folder_name)
            os.makedirs(folder_path, exist_ok=True)
            kt_file = os.path.join(folder_path, f"{table_name}DB.kt")
            if os.path.exists(kt_file):
                print(f"File exists, skipping: {kt_file}")
                continue
            with open(kt_file, 'w', encoding='utf-8') as f:
                f.write(f"// {line.strip()}\n")
            print(f"Created: {kt_file}")

if __name__ == "__main__":
    main()

