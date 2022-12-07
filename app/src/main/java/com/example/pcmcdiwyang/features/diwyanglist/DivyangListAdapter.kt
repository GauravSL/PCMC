package com.example.pcmcdiwyang.features.diwyanglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pcmcdiwyang.R
import com.example.pcmcdiwyang.data.ApplicantData


class DivyangListAdapter(private val applicantList: List<ApplicantData>, private val listner: DivyangListListner) : RecyclerView.Adapter<DivyangListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.divyang_list_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val divyangDetails = applicantList[position]

        holder.txtName.text = "${divyangDetails.firstName} ${divyangDetails.middleName} ${divyangDetails.lastName}"
        holder.txtAadharNumber.text = divyangDetails.addhar
        holder.txtUDID.text = divyangDetails.addhar
        holder.txtMobileNumber.text = divyangDetails.mobile

       // val divyangDetails = ApplicantData()
        holder.itemView.setOnClickListener {
            listner.onItemSelected(divyangDetails)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return applicantList.size
        //return 4
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtAadharNumber: TextView = itemView.findViewById(R.id.txtaadharNumber)
        val txtUDID: TextView = itemView.findViewById(R.id.txtUDID)
        val txtMobileNumber: TextView = itemView.findViewById(R.id.txtMobileNumber)
    }

    interface DivyangListListner{
        fun onItemSelected(applicantData: ApplicantData)
    }
}
