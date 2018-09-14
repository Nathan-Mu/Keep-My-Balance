package edu.monash.fit4039.keepmybalance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import static edu.monash.fit4039.keepmybalance.KMBConstant.INCOME;
import static edu.monash.fit4039.keepmybalance.Validation.isNullEmptyBlank;

public class ManageCategoriesActivity extends AppCompatActivity {
    private Spinner mcSpParentCategories;
    private ListView mcLvChildCategories;
    private Button mcBtnAddChild, mcBtnAddParent, mcBtnRemove;
    private EditText mcEtChildCategory, mcEtParentCategory;
    private List<ParentCategory> parentCategories = new ArrayList<>();
    private List<ChildCategory> childCategories = new ArrayList<>();
    private UserInfo user;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");

        mcSpParentCategories = (Spinner) findViewById(R.id.mcSpParentCategories);
        mcLvChildCategories = (ListView) findViewById(R.id.mcLvChildCategories);
        mcBtnAddChild = (Button) findViewById(R.id.mcBtnAddChild);
        mcBtnAddParent = (Button) findViewById(R.id.mcBtnAddParent);
        mcBtnRemove = (Button) findViewById(R.id.mcBtnRemove);

        initUI();

        //show child categories if user choose a parent category
        mcSpParentCategories.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadListViewData(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //show a dialog to add child category
        mcBtnAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddChildCategoryDialog();
            }
        });

        //show a dialog to add parent category
        mcBtnAddParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddParentCategoryDialog();
            }
        });

        //remove the parent category
        mcBtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (parentCategories.size() <= 2) {
                    showText("At least one category of expense should be leaved");
                } else if (mcSpParentCategories.getSelectedItem().toString().equalsIgnoreCase(INCOME)) {
                    showText("You cannot remove the income category");
                } else {
                    askForDelete(parentCategories.get(mcSpParentCategories.getSelectedItemPosition()));
                }
            }
        });

        //remove the child category
        mcLvChildCategories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                askForDelete(childCategories.get(position));
                return false;
            }
        });
    }

    //get and load parent categories
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private class ParentCategoryData extends AsyncTask<Integer, Void, List<JSONObject>> {
        @Override
        protected List<JSONObject> doInBackground(Integer... params) {
            return RestClient.getAllParentCategories(params[0]);
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsons) {
            String[] parentCategoryArray = new String[jsons.size()];
            parentCategories = new ArrayList<>();
            for (int i = 0; i < jsons.size(); i++) {
                ParentCategory parentCategory = new ParentCategory(jsons.get(i));
                parentCategories.add(parentCategory);
                parentCategoryArray[i] = parentCategory.getParentCategoryName();
            }
            //load parent categories in spinner
            loadSpinnerData(mcSpParentCategories, parentCategoryArray);
        }
    }

    //get and load child categories of a parent category
    private void loadListViewData(int position) {
        ChildCategoryData childCategoryData = new ChildCategoryData();
        childCategoryData.execute(new Integer[] {user.getUserId(), parentCategories.get(position).getParentCategoryId()});
    }

    //load data in spinner using ArrayAdapter
    //resource: https://stackoverflow.com/questions/2784081/android-create-spinner-programmatically-from-array
    private void loadSpinnerData(Spinner spinner, String[] array) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //get and load parent categories (init UI)
    private void initUI() {
        ParentCategoryData parentCategoryData = new ParentCategoryData();
        parentCategoryData.execute(new Integer[] {user.getUserId()});
    }

    //get child categories of a parent categories from server
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private class ChildCategoryData extends AsyncTask<Integer, Void, List<JSONObject>> {
        @Override
        protected List<JSONObject> doInBackground(Integer... params) {
            return RestClient.getAllChildCategoriesFromOneParentCategory(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(List<JSONObject> jsons) {
            childCategories = new ArrayList<>();
            for (int i = 0; i < jsons.size(); i++) {
                ChildCategory childCategory = new ChildCategory(jsons.get(i));
                childCategories.add(childCategory);
            }
            //set the adapter
            ChildCategoryAdapter adapter = new ChildCategoryAdapter();
            mcLvChildCategories.setAdapter(adapter);
        }
    }


    //adapter of the list view
    private class ChildCategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return childCategories.size();
        }

        @Override
        public Object getItem(int i) {
            return childCategories.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_child_category, null);
            }
            TextView iTvChildCategory = (TextView) view.findViewById(R.id.iTvChildCategory);
            iTvChildCategory.setText(childCategories.get(i).getChildCategoryName());
            return view;
        }
    }

    //show a dialog to add child category
    //resource: https://developer.android.com/guide/topics/ui/dialogs.html
    private void showAddChildCategoryDialog() {
        dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.dialog_input_box, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_input_box);
        Button btnPositive = (Button) dialog.findViewById(R.id.dBtnConfirm);
        Button btnNegative = (Button) dialog.findViewById(R.id.dBtnCancel);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.dTvTitle);
        tvTitle.setText("Add Child Category");
        mcEtChildCategory = (EditText) dialog.findViewById(R.id.dEtInputBox);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String childCategoryName = mcEtChildCategory.getText().toString();
                if (isNullEmptyBlank(childCategoryName)) {
                    mcEtChildCategory.setError("This field cannot be empty");
                } else if (isChildCategoryExist(childCategoryName)) {
                    mcEtChildCategory.setError("This category has already existed");
                } else {
                    ParentCategory parentCategory = parentCategories.get(mcSpParentCategories.getSelectedItemPosition());
                    ChildCategory childCategory = new ChildCategory(childCategoryName, parentCategory);
                    addChildCategory(childCategory);
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

    //check if the child category exists
    private boolean isChildCategoryExist(String name) {
        for (ChildCategory childCategory: childCategories) {
            if (name.equalsIgnoreCase(childCategory.getChildCategoryName()))
                return true;
        }
        return false;
    }

    //create a child category (post to server)
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private void addChildCategory(ChildCategory childCategory) {
        new AsyncTask<ChildCategory, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(ChildCategory... params) {
                return RestClient.createChildCategory(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean isCreated) {
                if (isCreated) {
                    dialog.dismiss();
                    loadListViewData(mcSpParentCategories.getSelectedItemPosition());
                    showText("Add Successfully");
                } else {
                    mcEtChildCategory.setError("Failed, please try again");
                }
            }
        }.execute(childCategory);
    }

    //create a parent category (post to server)
    //resource: https://developer.android.com/reference/android/os/AsyncTask.html
    private void addParentCategory(ParentCategory parentCategory) {
        new AsyncTask<ParentCategory, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(ParentCategory... params) {
                return RestClient.createParentCategory(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean isCreated) {
                if (isCreated) {
                    dialog.dismiss();
                    initUI();
                    showText("Add Successfully");
                } else {
                    mcEtParentCategory.setError("Failed, please try again");
                }
            }
        }.execute(parentCategory);
    }

    //show a notification on screen
    private void showText(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    //show a dialog to add parent category
    private void showAddParentCategoryDialog() {
        dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.dialog_input_box, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_input_box);
        Button btnPositive = (Button) dialog.findViewById(R.id.dBtnConfirm);
        Button btnNegative = (Button) dialog.findViewById(R.id.dBtnCancel);
        mcEtParentCategory = (EditText) dialog.findViewById(R.id.dEtInputBox);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.dTvTitle);
        tvTitle.setText("Add Parent Category");
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String parentCategoryName = mcEtParentCategory.getText().toString();
                if (isNullEmptyBlank(parentCategoryName)) {
                    mcEtParentCategory.setError("This field cannot be empty");
                } else if (isParentCategoryExist(parentCategoryName)) {
                    mcEtParentCategory.setError("This category has already existed");
                } else {
                    ParentCategory parentCategory = new ParentCategory(parentCategoryName, user);
                    addParentCategory(parentCategory);
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

    //check if the parent category exists
    private boolean isParentCategoryExist(String name) {
        for (ParentCategory parentCategory: parentCategories) {
            if (name.equalsIgnoreCase(parentCategory.getParentCategoryName())) {
                return true;
            }
        }
        return false;
    }

    //show a dialog to ask user to confirm deleting
    //resource: https://developer.android.com/guide/topics/ui/dialogs.html
    private void askForDelete(final Object object) {
        String title = "";
        String message = "";
        ParentCategory parentCategory;
        ChildCategory childCategory;
        if (object.getClass().toString().equalsIgnoreCase("class edu.monash.fit4039.keepmybalance.ParentCategory")) {
            parentCategory = (ParentCategory) object;
            title = "Remove parent category";
            message = "Confirm to remove this parent category(" + parentCategory.getParentCategoryName() + ") ?";
        } else if (object.getClass().toString().equalsIgnoreCase("class edu.monash.fit4039.keepmybalance.ChildCategory")) {
            childCategory = (ChildCategory) object;
            title = "Remove child category";
            message = "Confirm to remove this child category(" + childCategory.getChildCategoryName() + ") ?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (object.getClass().toString().equalsIgnoreCase("class edu.monash.fit4039.keepmybalance.ParentCategory"))
                            removeParentCategory((ParentCategory) object);
                        else if (object.getClass().toString().equalsIgnoreCase("class edu.monash.fit4039.keepmybalance.ChildCategory")) {
                            removeChildCategory((ChildCategory) object);
                        }
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

    //remove a parent category (firstly check if it can be removed)
    private void removeParentCategory(ParentCategory parentCategory) {
        if (childCategories.isEmpty()) {
            doRemoveParentCategory(parentCategory);
        } else {
            showText("You cannot remove a parent category with existed child categories");
        }
    }

    //remove a child category (firstly chieck if it can be removed)
    private void removeChildCategory(final ChildCategory childCategory) {
        new AsyncTask<ChildCategory, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(ChildCategory... params) {
                return RestClient.isChildCategoryRemovable(childCategory.getChildCategoryId());
            }

            @Override
            protected void onPostExecute(Boolean isRemovable) {
                if (isRemovable) {
                    doRemoveChildCategory(childCategory);
                } else {
                    showText("You cannot remove a child category with existed records");
                }
            }
        }.execute(childCategory);
    }

    //remove the parent category on server side
    private void doRemoveChildCategory(final ChildCategory childCategory) {
        new AsyncTask<ChildCategory, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(ChildCategory... params) {
                return RestClient.deleteChildCategory(params[0].getChildCategoryId());
            }

            @Override
            protected void onPostExecute(Boolean isRemoved) {
                if (isRemoved) {
                    showText("Removed successfully");
                    loadListViewData(mcSpParentCategories.getSelectedItemPosition());
                } else {
                    //server down or losing connection
                    showText("Failed to remove, please try again");
                }
            }
        }.execute(childCategory);
    }

    //remove the child category on server side
    private void doRemoveParentCategory(final ParentCategory parentCategory) {
        new AsyncTask<ParentCategory, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(ParentCategory... params) {
                return RestClient.deleteParentCategory(params[0].getParentCategoryId());
            }

            @Override
            protected void onPostExecute(Boolean isRemoved) {
                if (isRemoved) {
                    showText("Removed successfully");
                    initUI();
                } else {
                    //server down or losing connection
                    showText("Failed to remove, please try again");
                }
            }
        }.execute(parentCategory);
    }
}
