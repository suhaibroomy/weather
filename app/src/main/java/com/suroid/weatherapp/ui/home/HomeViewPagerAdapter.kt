package com.suroid.weatherapp.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.models.CityWeatherEntity
import com.suroid.weatherapp.ui.weathercards.WeatherCardFragment

class HomeViewPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {
    private var cityWeatherList: List<CityEntity> = ArrayList()

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return WeatherCardFragment.newInstance(cityWeatherList[position])
    }

    override fun getCount(): Int {
        return cityWeatherList.size
    }

    fun updateList(data: List<CityEntity>) {
        cityWeatherList = data
        notifyDataSetChanged()
    }
}