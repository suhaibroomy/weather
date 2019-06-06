package com.suroid.weatherapp.ui.cityselection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suroid.weatherapp.R
import com.suroid.weatherapp.models.CityEntity

/**
 * Adapter for recycler of [CitySelectionActivity] , it can handle grid and list layout
 */
class CitySelectionAdapter(private val context: Context, private val cityAdapterDelegate: CityAdapterDelegate) : RecyclerView.Adapter<CitySelectionAdapter.CityViewHolder>() {

    interface CityAdapterDelegate {
        fun onItemClick(cityEntity: CityEntity)
    }

    private lateinit var cityEntityList: List<CityEntity>
    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CityViewHolder {
        return CityViewHolder(layoutInflater.inflate(R.layout.item_city_list, viewGroup, false))
    }

    override fun getItemCount(): Int {
        return if (::cityEntityList.isInitialized) cityEntityList.size else 0
    }

    override fun onBindViewHolder(viewHolder: CityViewHolder, position: Int) {
        viewHolder.onBind(cityEntityList[position])
        viewHolder.itemView.setOnClickListener {
            cityAdapterDelegate.onItemClick(cityEntityList[position])
        }
    }

    /**
     * Updates the city List and notifies the adapter
     * @param cityEntityList updated cities list
     */
    fun updateCityList(cityEntityList: List<CityEntity>) {
        this.cityEntityList = cityEntityList
        notifyDataSetChanged()
    }


    class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvName = itemView.findViewById<TextView?>(R.id.tv_name)

        fun onBind(cityEntity: CityEntity) {
            tvName?.text = tvName?.context?.getString(R.string.city_country_format, cityEntity.name, cityEntity.country)
        }

    }

}
