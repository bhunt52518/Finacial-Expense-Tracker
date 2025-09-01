import csv
import pandas as pd
import warnings
import json

# Ignore a specific UserWarning from pandas to keep the output clean
warnings.filterwarnings('ignore', category=UserWarning, module='pandas')

def process_statement(file_path):
    """
    Reads a bank statement CSV, cleans the data, categorizes transactions,
    and aggregates financial data into a JSON format.
    """
    try: 
        df = pd.read_csv(file_path, on_bad_lines='skip')

        # --- Data Cleaning ---
        # removes commas and converts them to numeric calues
        df['Deposits'] = pd.to_numeric(df['Deposits'].astype(str).str.replace(',',''), errors='coerce')
        df['Withdrawls'] = pd.to_numeric(df['Withdrawls'].astype(str).str.replace(',',''), errors='coerce')

        df.fillna(0.0, inplace=True)

        # --- Transaction Categorization ---
        # Create a dictionary of keywords to categories.
        categories = {
            'Reversal|Income|Interest|Deposit|Salary|Cheque|Cash|Transfer|NEFT|RTGS|IMPS|ATM': 'Income',
            'Bill|Purchase|Tax|Commission|Debit Card|Miscellaneous': 'Expenses'
        }

        df['Category'] = 'Uncategorized'

        # Loop through the categories and keywords to assign a category to each transaction.
        for keyword, category_name in categories.items():
            df.loc[df['Description'].str.contains(keyword, case=False, na=False), 'Category'] = category_name

        # --- Data Aggregation ---
        total_income = df['Deposits'].sum()
        total_expenses = df ['Withdrawls'].sum()

        # Group by the new Category column and sum the Deposits and Withdrawls
        spending_by_category = df.groupby('Category').agg(
            TotalDeposites=('Deposits', 'sum'),
            TotalWithdrawls=('Withdrawls', 'sum')
        ).reset_index()

        # Convery to (JSON) for Java backend
        result_json = {
            'total_transactions': len(df),
            'total_income': total_income,
            'total_expenses': total_expenses,
            'spending_by_category': json.loads(spending_by_category.to_json(orient='records'))
        }

        return result_json

    except Exception as e:
        return f"An error occurred: {e}"

    

if __name__ == '__main__':
    processed_data = process_statement('cleaned_bank_data.csv')
    print("Processed Data (JSON format):")
    print(json.dumps(processed_data, indent=4))