package com.suroid.weatherapp.ui.home

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.models.CityWeatherEntity
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
}