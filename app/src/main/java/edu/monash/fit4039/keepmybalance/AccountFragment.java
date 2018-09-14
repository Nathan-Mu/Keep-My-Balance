package edu.monash.fit4039.keepmybalance;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.TransitionRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CASH;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CUSTOM_DARK_RED;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CUSTOM_GREEN;
import static edu.monash.fit4039.keepmybalance.Time.getCurrentDateTime;
import static edu.monash.fit4039.keepmybalance.Validation.isNullEmptyBlank;
import static edu.monash.fit4039.keepmybalance.Validation.leftTwoDecimal;

/**
 * Created by nathan on 17/5/17.
 */

public class AccountFragment extends Fragment{
    private View vAccount;
    private UserInfo user;
    private AccountAdapter adapter;
    private ListView aLvAccount;
    private TextView aTvTotalNumber, aTvTotalReminding;
    private Button aBtnAddAccount, aBtnTransfer;
    private AlertDialog dialog;
    private EditText dEtAccountType, dEtBalance;
    private List<Account> accounts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vAccount = inflater.inflate(R.layout.fragment_account, container, false);

        this.getActivity().setTitle("Account");

        //get user object from intent
        Intent intent = this.getActivity().getIntent();
        user = intent.getParcelableExtra("user");

        aLvAccount = (ListView) vAccount.findViewById(R.id.aLvAccountList);
        aTvTotalNumber = (TextView) vAccount.findViewById(R.id.aTvTotalNumber);
        aTvTotalReminding = (TextView) vAccount.findViewById(R.id.aTvTotalReminding);
        aBtnAddAccount = (Button) vAccount.findViewById(R.id.aBtnAddAccount);
        aBtnTransfer = (Button) vAccount.findViewById(R.id.aBtnTransfer);

        initUI();

        aBtnAddAccount.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                showAddAccountDialog();
            }
        });

        //if user long click the item of list view, then show a dialog and ask for confirming deleting that item
        aLvAccount.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (accounts.get(position).getAccountType().equalsIgnoreCase(CASH)) {
                    showText("You cannot remove cash");
                    return false;
                } else {
                    askForDelete(accounts.get(position));
                    return false;
                }
            }
        });

        //if user short click the item, then show a dialog and ask for the new balance (adjust balance of that account)
        aLvAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                askForAdjust(accounts.get(position));
            }
        });

        aBtnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForTransfer(accounts);
            }
        });

        return vAccount;
    }

    //AsyncTask: http://moodle.vle.monash.edu/pluginfile.php/5474351/mod_resource/content/1/FIT5046-AndroidTute7-AsyncTask-2017.pdf
    private class AccountFragmentData extends AsyncTask<Integer, Void, List<JSONObject>>
    {
        @Override
        protected List<JSONObject> doInBackground(Integer... params) {
            return RestClient.getAllAccounts(params[0]);
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsons) {
            accounts = new ArrayList<>();
            //use big decimal instead of double. Because double cannot used for arithmetic
            BigDecimal remindingBD = BigDecimal.valueOf(0);
            //create the account objects into list by the json objects received from server
            for (JSONObject j: jsons)
            {
                Account account = new Account(j);
                remindingBD = remindingBD.add(BigDecimal.valueOf(account.getBalance()));
                accounts.add(account);
            }
            //instantiate adapter
            adapter = new AccountAdapter(vAccount.getContext());
            //set adapter
            aLvAccount.setAdapter(adapter);
            aTvTotalNumber.setText("Number of Accounts:" + String.valueOf(accounts.size()));
            double reminding = remindingBD.doubleValue();
            aTvTotalReminding.setText(new DecimalFormat("#0.00").format(reminding));
            //set color (green when reminding >= 0, red when reminding < 0)
            if (reminding < 0) {
                aTvTotalReminding.setTextColor(CUSTOM_DARK_RED);
            } else {
                aTvTotalReminding.setTextColor(CUSTOM_GREEN);
            }
        }
    }

    private class AccountAdapter extends BaseAdapter {
        private Context context;

        public AccountAdapter(Context context) {
            this.context = context;
        }

        //get size
        @Override
        public int getCount()
        {
            return accounts.size();
        }

        //get one item
        @Override
        public Object getItem(int i)
        {
            return accounts.get(i);
        }

        //get item id
        @Override
        public long getItemId(int i)
        {
            return i;
        }

        //get view
        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                //inflate view
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_account_list, null);
            }

            TextView iTvAccount = (TextView) view.findViewById(R.id.iTvAccount);
            TextView iTvBalance = (TextView) view.findViewById(R.id.iTvBalance);

            //set text of account and balance
            iTvAccount.setText(accounts.get(i).getAccountType());
            iTvBalance.setText(new DecimalFormat("#0.00").format(accounts.get(i).getBalance()));
            //set color (green if balance >= 0, red if balance < 0)
            if (accounts.get(i).getBalance() < 0) {
                iTvBalance.setTextColor(CUSTOM_DARK_RED);
            } else {
                iTvBalance.setTextColor(CUSTOM_GREEN);
            }
            return view;
        }
    }

    //create a new account (check if account exists firstly)
    private void addNewAccount(Account newAccount) {
        final Account account = newAccount;
        new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Object... params) {
                //check if account exists on server side
                return RestClient.isAccountExist((UserInfo) params[0], (Account) params[1]);
            }

            @Override
            protected void onPostExecute(Boolean isExist) {
                if (isExist) {
                    //if that account exists, tell user it exists
                    showText("Account has already existed");
                } else {
                    //Or create the account
                    doAddAccount(account);
                }
            }
        }.execute(user, account);
    }

    //create a new account
    private void doAddAccount(Account account) {
        new AsyncTask<Account, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Account... params) {
                return RestClient.createAccount(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean isCreated) {
                //if the account is created successfully, show success message and close the dialog and then refresh UI
                if (isCreated) {
                    showText("Add account successfully");
                    dialog.dismiss();
                    initUI();
                } else {
                    //something unexpected, main problem is losing network connection or server is down
                    dEtAccountType.setError("Failed to create account. Please try again");
                }
            }
        }.execute(account);
    }

    //show a notification on screen
    private void showText(String string) {
        Toast.makeText(vAccount.getContext(), string, Toast.LENGTH_SHORT).show();
    }

    //show a dialog for user to add account
    //resource: https://developer.android.com/guide/topics/ui/dialogs.html
    //resource: https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
    //resource: https://www.tutorialspoint.com/android/android_alert_dialoges.htm
    //resource: https://www.mkyong.com/android/android-custom-dialog-example/
    private void showAddAccountDialog() {
        dialog = new AlertDialog.Builder(vAccount.getContext()).create();
        dialog.setView(LayoutInflater.from(vAccount.getContext()).inflate(R.layout.dialog_add_account, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_add_account);
        Button btnPositive = (Button) dialog.findViewById(R.id.dBtnConfirm);
        Button btnNegative = (Button) dialog.findViewById(R.id.dBtnCancel);
        dEtAccountType = (EditText) dialog.findViewById(R.id.dEtAccountType);
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String accountType = dEtAccountType.getText().toString();
                //validate user input
                if (isNullEmptyBlank(accountType)) {
                    dEtAccountType.setError("The account type cannot be empty");
                } else {
                    //if user input is not empty or only space, create the account and post to server
                    Account account = new Account(accountType, user, 0);
                    addNewAccount(account);
                }
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //init UI and load the data
    private void initUI() {
        AccountFragmentData accountFragmentData = new AccountFragmentData();
        accountFragmentData.execute(user.getUserId());
    }

    //remove an account （firstly check if the account can be removed)
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private void removeAccount(Account account) {
        final Account removeAccount = account;
        new AsyncTask<Account, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Account... params) {
                //check if the account can be removed
                return RestClient.isAccountRemovable(params[0].getAccountId());
            }

            @Override
            protected void onPostExecute(Boolean isAccountRemovable) {
                if (isAccountRemovable) {
                    //if the account can be removed, then remove the account
                    doRemoveAccount(removeAccount);
                } else {
                    //if the account cannot be removed, tell user the account cannot be removed
                    showText("Account cannot be deleted if there are records on this account");
                }
            }
        }.execute(removeAccount);
    }

    //remove the account
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private void doRemoveAccount(Account account) {
        new AsyncTask<Account, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Account... params) {
                return RestClient.deleteAccount(params[0].getAccountId());
            }

            @Override
            protected void onPostExecute(Boolean isAccountRemoved) {
                if (isAccountRemoved) {
                    //if the account is removed on server, show success message and refresh UI
                    showText("Account has been removed.");
                    initUI();
                } else {
                    //something unexpected. Main reason might be losing network connection or server is down
                    showText("Account has not been removed.");
                }
            }
        }.execute(account);
    }

    //show a dialog to ask user to confirm deleting the choose account
    //resource: https://developer.android.com/guide/topics/ui/dialogs.html
    //resource: https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
    //resource: https://www.tutorialspoint.com/android/android_alert_dialoges.htm
    //resource: https://www.mkyong.com/android/android-custom-dialog-example/
    private void askForDelete(Account account) {
        final Account removeAccount = account;
        AlertDialog.Builder builder = new AlertDialog.Builder(vAccount.getContext());
        builder.setTitle("Remove Account");
        builder.setMessage("Confirm to remove this account(" + removeAccount.getAccountType() + ") ?");
        builder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //remove the account from the server (firstly check if the account can be removed
                        removeAccount(removeAccount);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    //show a dialog to ask user to input the new balance
    //resource: https://developer.android.com/guide/topics/ui/dialogs.html
    //resource: https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
    //resource: https://www.tutorialspoint.com/android/android_alert_dialoges.htm
    //resource: https://www.mkyong.com/android/android-custom-dialog-example/
    private void askForAdjust(final Account account) {
        dialog = new AlertDialog.Builder(vAccount.getContext()).create();
        dialog.setView(LayoutInflater.from(vAccount.getContext()).inflate(R.layout.dialog_number_input, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_number_input);
        Button btnPositive = (Button) dialog.findViewById(R.id.dnBtnConfirm);
        Button btnNegative = (Button) dialog.findViewById(R.id.dnBtnCancel);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.dnTvTile);
        tvTitle.setText("Adjust Balance");
        dEtBalance = (EditText) dialog.findViewById(R.id.dnEtInputNumber);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String newBalanceString = dEtBalance.getText().toString();
                //validate user input (not null or blank or empty)
                //validate user input (cannot be the same number)
                if (isNullEmptyBlank(newBalanceString)) {
                    dEtBalance.setError("This field cannot be empty");
                } else if (account.getBalance() == leftTwoDecimal(Double.valueOf(newBalanceString))) {
                    //check if the new balance is same as the old balance
                    dEtBalance.setError("You cannot adjust the balance with same amount");
                } else {
                    account.setBalance(leftTwoDecimal(Double.valueOf(newBalanceString)));
                    updateBalance(account);
                }
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //show a dialog to ask user to choose and input details of transfer
    //resource: https://developer.android.com/guide/topics/ui/dialogs.html
    //resource: https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
    //resource: https://www.tutorialspoint.com/android/android_alert_dialoges.htm
    //resource: https://www.mkyong.com/android/android-custom-dialog-example/
    private void askForTransfer(final List<Account> accounts) {
        dialog = new AlertDialog.Builder(vAccount.getContext()).create();
        dialog.setView(LayoutInflater.from(vAccount.getContext()).inflate(R.layout.dialog_transfer, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_transfer);
        final Button btnPositive = (Button) dialog.findViewById(R.id.dtBtnConfirm);
        final Button btnNegative = (Button) dialog.findViewById(R.id.dtBtnCancel);
        final EditText etAmount = (EditText) dialog.findViewById(R.id.dEtAmount);
        final Spinner spInputAccount = (Spinner) dialog.findViewById(R.id.dSpInputAccount);
        final Spinner spOutputAccount = (Spinner) dialog.findViewById(R.id.dSpOutputAccount);

        String[] accountTypes = new String[accounts.size() + 1];
        accountTypes[0] = "----Please Choose----";
        //give value to an array which is the options in the spinner
        for (int i = 0; i < accounts.size(); i++) {
            accountTypes[i + 1] = accounts.get(i).getAccountType();
        }
        //load spinner data
        loadSpinnerData(spInputAccount, accountTypes);
        loadSpinnerData(spOutputAccount, accountTypes);

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountString = etAmount.getText().toString();
                int inputAccountIndex = spInputAccount.getSelectedItemPosition();
                int outputAccountIndex = spOutputAccount.getSelectedItemPosition();
                //validate user input
                //1. the transfer from and transfer in spinner must be checked
                //2. input account cannot be same as output account
                //3. amount of transfer cannot be empty
                //4. amount cannot be 0
                if (outputAccountIndex == 0 || inputAccountIndex == 0) {
                    showText("Please choose accounts");
                } else if (outputAccountIndex == inputAccountIndex) {
                    showText("The accounts cannot be same");
                } else if (amountString.isEmpty()) {
                    etAmount.setError("Please input the amount to transfer");
                } else if (Double.valueOf(amountString) == 0) {
                    etAmount.setError("Amount cannot be 0");
                } else {
                    Double amount = Double.valueOf(amountString);
                    Account inputAccount = accounts.get(inputAccountIndex - 1);
                    Account outputAccount = accounts.get(outputAccountIndex - 1);
                    AccountTransaction transaction = new AccountTransaction(inputAccount, outputAccount, amount, getCurrentDateTime());
                    //if user input is valid, then transfer money
                    transfer(transaction);
                }
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //update balance
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private void updateBalance(Account account) {
        new AsyncTask<Account, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Account... params) {
                return RestClient.updateAccount(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean isUpdated) {
                if (isUpdated) {
                    //if balance is updated, show success message
                    //close the dialog and init UI
                    showText("Adjust successfully");
                    dialog.dismiss();
                    initUI();
                } else {
                    //something unexpected, like losing network connection or server is down
                    showText("Failed to adjust balance. Please try again.");
                }
            }
        }.execute(account);
    }

    //transfer money
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private void transfer(AccountTransaction transaction) {
        new AsyncTask<AccountTransaction, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(AccountTransaction... params) {
                return RestClient.createTransaction(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean isCreated) {
                if (isCreated) {
                    //if transfer successfully (transaction is created), show success message
                    //close dialog and init UI
                    showText("Transfer successfully");
                    dialog.dismiss();
                    initUI();
                } else {
                    //something unexpected, like losing network connection or server is down
                    showText("Failed to transfer. Please try again");
                }
            }
        }.execute(transaction);
    }

    //load data to spinner using ArrayAdapter
    //resource: https://stackoverflow.com/questions/2784081/android-create-spinner-programmatically-from-array
    private void loadSpinnerData(Spinner spinner, String[] array) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}