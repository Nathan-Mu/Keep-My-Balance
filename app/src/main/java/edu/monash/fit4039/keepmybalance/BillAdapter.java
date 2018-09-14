package edu.monash.fit4039.keepmybalance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import static edu.monash.fit4039.keepmybalance.KMBConstant.CUSTOM_DARK_RED;
import static edu.monash.fit4039.keepmybalance.KMBConstant.CUSTOM_GREEN;
import static edu.monash.fit4039.keepmybalance.KMBConstant.EXPENSE;
import static edu.monash.fit4039.keepmybalance.KMBConstant.INCOME;

/**
 * Created by nathan on 21/5/17.
 */

//resource: https://developer.android.com/reference/android/widget/ExpandableListView.html
public class BillAdapter extends BaseExpandableListAdapter {
    private Map<String, List<FundChange>> map = null;
    private List<String> parentList = null;
    private Context context;

    public BillAdapter(List<String> parentList, Map<String, List<FundChange>> map, Context context) {
        this.parentList = parentList;
        this.map = map;
        this.context = context;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String key = parentList.get(groupPosition);
        return (map.get(key).get(childPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //get child item view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        String key = parentList.get(groupPosition);
        FundChange record = map.get(key).get(childPosition);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_child_fund_change_list, null);
        }
        TextView iTvCategory = (TextView) view.findViewById(R.id.iTvCategory);
        TextView iTvAmount = (TextView) view.findViewById(R.id.iTvAmount);

        //set the text of category and amount
        iTvCategory.setText(record.getChildCategoryId().getParentCategory().getParentCategoryName() + "-" + record.getChildCategoryId().getChildCategoryName());
        iTvAmount.setText(String.valueOf(record.getAmount()));
        DecimalFormat df =new DecimalFormat("#0.00");
        String textAmount = df.format(record.getAmount());

        if (record.getChangeType().equalsIgnoreCase(INCOME)) {
            //if it is an income, set green as color
            iTvAmount.setText(textAmount);
            iTvAmount.setTextColor(CUSTOM_GREEN);
        } else if (record.getChangeType().equalsIgnoreCase(EXPENSE)){
            //if it is an expense, set red as color
            iTvAmount.setText(textAmount);
            iTvAmount.setTextColor(CUSTOM_DARK_RED);
        }
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String key = parentList.get(groupPosition);
        int size = map.get(key).size();
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //get parent view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        String textDate = parentList.get(groupPosition);
        Double amount = 0.0;
        //calculate the balance of one day
        for (FundChange f: map.get(textDate))
        {
            if (f.getChangeType().equalsIgnoreCase(EXPENSE))
                amount -= f.getAmount();
            else if (f.getChangeType().equalsIgnoreCase(INCOME))
                amount += f.getAmount();
        }
        //money format
        DecimalFormat df =new DecimalFormat("#0.00");
        String textAmount = "";

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_parent_fund_change_list, null);
        }
        TextView iTvDate = (TextView) view.findViewById(R.id.iTvDate);
        TextView iTvTotalAmount = (TextView) view.findViewById(R.id.iTvTotalAmount);
        iTvDate.setText(parentList.get(groupPosition));

        //set text and color of the amount
        if (amount >= 0) {
            textAmount = "+ " + df.format(amount);
            iTvTotalAmount.setText(textAmount);
            iTvTotalAmount.setTextColor(CUSTOM_GREEN);
        } else {
            textAmount = df.format(-amount);
            iTvTotalAmount.setText(textAmount);
            iTvTotalAmount.setTextColor(CUSTOM_DARK_RED);
        }

        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
