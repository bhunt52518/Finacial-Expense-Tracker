import csv
import pandas as pd

def process_statement(file_path):
    # Read the CSV file into a Pandas Dataframe
    file_path = 'https://raw.githubusercontent.com/bhunt52518/Finacial-Expense-Tracker/main/10000%20BT%20Records.csv'
    df = pd.read_csv(file_path)

    # Display the first few rows to check if it loaded correctly
    print("Original DataFrame:")
    print(df.head())

    # Return DataFrrame for further processing
    return df

# Test usage
if __name__ == '__main__':
    statement_df = process_statement('file_path')