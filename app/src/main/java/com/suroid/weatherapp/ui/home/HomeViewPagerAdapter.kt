package com.suroid.weatherapp.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.suroid.weatherapp.models.local.CityEntity
import com.suroid.weatherapp.ui.weathercards.WeatherCardFragment

class HomeViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private var cityWeatherList: List<CityEntity> = ArrayList()

    override fun getItem(position: Int): Fragment {
        return WeatherCardFragment.newInstance(cityWeatherList[position])
    }

    override fun getCount(): Int {
        return cityWeatherList.size
    }

    fun updateList(data: List<CityEntity>) {
        cityWeatherList = data
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}