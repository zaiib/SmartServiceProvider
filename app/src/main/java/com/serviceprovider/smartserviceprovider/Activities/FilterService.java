package com.serviceprovider.smartserviceprovider.Activities;


import android.widget.Filter;

import com.serviceprovider.smartserviceprovider.Adapters.AdapterServiceSeller;
import com.serviceprovider.smartserviceprovider.Models.ModelService;

import java.util.ArrayList;

public class FilterService extends Filter {

    private final AdapterServiceSeller adapter;
    private final ArrayList<ModelService> filterList;

    public FilterService(AdapterServiceSeller adapter, ArrayList<ModelService> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        FilterResults results=new FilterResults();
        // validate  data  for Search query

        if (constraint !=null && constraint.length()>0){
            //search filed not empty,searching something, perform search
            // change upper case, to make case insensitive
            constraint=constraint.toString().toUpperCase();
            // service our filter list
            ArrayList<ModelService> filteredModels=new ArrayList<>();

            for (int i=0;i<filterList.size();i++){
                //search file not empty, search
                //check by name , category
                if (filterList.get(i).getSellerName().toUpperCase().contains(constraint) ||
                        filterList.get(i).getServiceCategory().toUpperCase().contains(constraint)) {
                    //add filtered data to list
                    filteredModels.add(filterList.get(i));
                }
            }
            //search filed  empty,not searching return original /all / complete list

            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        adapter.serviceList=(ArrayList<ModelService>) results.values;
        // refresh adapter
        adapter.notifyDataSetChanged();
    }










}
