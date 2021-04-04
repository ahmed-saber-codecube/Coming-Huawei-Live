package com.coming.customer.ui.auth

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coming.customer.R
import com.coming.customer.data.pojo.Country
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.row_country.view.*

class CountryAdapter(internal var context: Context, internal var countries: List<Country>, private val callBack: CallBack) : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_country, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(countries[position])
    }


    override fun getItemCount(): Int {
        return countries.size
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        init {
            itemView.setOnClickListener { view -> callBack.onItemClick(adapterPosition) }
        }

        fun bind(country: Country) = with(itemView) {
            textViewCountryCode.text = country.dialCode
            textViewCountryName.text = country.name
        }
    }

    interface CallBack {

        fun onItemClick(position: Int)

    }

}