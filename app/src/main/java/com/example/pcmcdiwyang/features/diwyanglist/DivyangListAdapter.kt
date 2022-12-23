package com.example.pcmcdiwyang.features.diwyanglist

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.pcmcdiwyang.R
import com.example.pcmcdiwyang.data.model.ApplicantData
import com.example.pcmcdiwyang.scanners.face.tflite.SimilarityClassifier


class DivyangListAdapter(private val applicantList: List<ApplicantData>, private val listner: DivyangListListner) : RecyclerView.Adapter<DivyangListAdapter.ViewHolder>() {

    private var face :SimilarityClassifier.Recognition? = null

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

       /* if (divyangDetails.face!=null){
            holder.profilePic.setImageBitmap(divyangDetails.face)
        }*/

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return applicantList.size
        //return 4
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val profilePic: ImageView = itemView.findViewById(R.id.profilePic)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtAadharNumber: TextView = itemView.findViewById(R.id.txtaadharNumber)
        val txtUDID: TextView = itemView.findViewById(R.id.txtUDID)
        val txtMobileNumber: TextView = itemView.findViewById(R.id.txtMobileNumber)
    }

    interface DivyangListListner{
        fun onItemSelected(applicantData: ApplicantData)
    }
}
