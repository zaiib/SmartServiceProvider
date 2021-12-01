package com.serviceprovider.smartserviceprovider.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.serviceprovider.smartserviceprovider.Activities.FilterService;
import com.serviceprovider.smartserviceprovider.Activities.FilterService;
import com.serviceprovider.smartserviceprovider.Models.ModelService;
import com.serviceprovider.smartserviceprovider.R;
import com.squareup.picasso.Picasso;



import java.util.ArrayList;

public class AdapterServiceSeller extends RecyclerView.Adapter<AdapterServiceSeller.HolderServiceSeller>  implements Filterable {

    private final Context context;
    public ArrayList<ModelService> serviceList,filterList;
    private FilterService filter;

    public AdapterServiceSeller(Context context, ArrayList<ModelService> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        this.filterList = filterList;
        this.filter = filter;
    }

    @NonNull

    @Override
    public HolderServiceSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_service_seller,parent,false);
        // View view= LayoutInflater.from(context).inflate(R.layout.row_service_seller,parent,false);
        return new HolderServiceSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterServiceSeller.HolderServiceSeller holder, int position) {

        //get Data
        ModelService modelService=  serviceList.get(position);
        String id =modelService.getProductId();
        String uid =modelService.getUid();
        String discountAvailable=modelService.getDiscountAvailable();
        String discountNote=modelService.getDiscountNote();
        String discountPrice=modelService.getDiscountPrice();
        String serviceCategory=modelService.getServiceCategory();
        String serviceDescription=modelService.getServiceDescription();
        String originalPrice=modelService.getOriginalPrice();
        String icon=modelService.getServiceIcon();
        String quantity=modelService.getServiceQuantity();
        String name=modelService.getSellerName();

        //set data
        holder.nameTv.setText(name);
        holder.quantityTv.setText(quantity);
        holder.discountNoteTv.setText(discountNote);
        holder.discountedPriceTv.setText("Rs."+discountPrice);
        holder.originalPriceTv.setText("Rs."+originalPrice);
        if (discountAvailable.equals("true")){
            // product is on discount
            holder.discountedPriceTv.setVisibility(View.VISIBLE);
            holder.discountNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);// add strike
        }
        else {
            // product is not on discount
            holder.discountedPriceTv.setVisibility(View.GONE);
            holder.discountNoteTv.setVisibility(View.GONE);
        }
        try{
            Picasso.get().load(icon).placeholder(R.drawable.ic_baseline_person_add_24).into(holder.serviceIconIV);
        }
        catch (Exception e){
            holder.serviceIconIV.setImageResource(R.drawable.ic_baseline_person_add_24);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // handle item click, show item details

            }
        });

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    @Override
    public Filter getFilter() {

        if (filter==null){
            filter=new FilterService(this,filterList);
        }

        return filter;

    }

    class HolderServiceSeller extends RecyclerView.ViewHolder {

        private ImageView serviceIconIV;
        private TextView nameTv,quantityTv,discountedPriceTv,originalPriceTv,discountNoteTv;

        /*holds view of RecyclerView*/
        public HolderServiceSeller(@NonNull  View itemView) {
            super(itemView);

            serviceIconIV=itemView.findViewById(R.id.serviceIconIV);
            discountNoteTv=itemView.findViewById(R.id.discountNoteTv);
            nameTv=itemView.findViewById(R.id.nameTv);
            quantityTv=itemView.findViewById(R.id.quantityTv);
            discountedPriceTv=itemView.findViewById(R.id.discountedPriceTv);
            originalPriceTv=itemView.findViewById(R.id.originalPriceTv);
        }
    }
}
