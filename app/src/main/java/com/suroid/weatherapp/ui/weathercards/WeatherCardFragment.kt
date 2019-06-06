package com.suroid.weatherapp.ui.weathercards

import android.animation.AnimatorSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.suroid.weatherapp.R
import com.suroid.weatherapp.databinding.FragmentWeatherCardBinding
import com.suroid.weatherapp.di.Injectable
import com.suroid.weatherapp.models.local.CityEntity
import com.suroid.weatherapp.utils.extensions.fadeInImage
import com.suroid.weatherapp.utils.setupProgressAnimation
import kotlinx.android.synthetic.main.fragment_weather_card.*
import javax.inject.Inject

class WeatherCardFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: WeatherCardViewModel
    private lateinit var binding: FragmentWeatherCardBinding
    private val animationSet = AnimatorSet()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(WeatherCardViewModel::class.java)

        arguments?.getParcelable<CityEntity>(CITY_ENTITY)?.let {
            viewModel.setupWithCity(it)
        }

        binding.viewModel = viewModel
        registerViewModelObservers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_weather_card, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProgressAnimation(animationSet, progress_bar)
    }

    /**
     * Registering observers for related liveData from viewModel
     */
    private fun registerViewModelObservers() {
        viewModel.image.observe(this, Observer { icon ->
            icon?.let {
                iv_background.fadeInImage(it)
            }
        })

        viewModel.loadingStatus.observe(this, Observer { visibility ->
            visibility?.let {
                if (it) {
                    progress_bar.visibility = View.VISIBLE
                    animationSet.start()
                } else {
                    animationSet.cancel()
                    progress_bar.visibility = View.GONE
                }
            }
        })
    }

    companion object {

        private const val CITY_ENTITY = "city_entity"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param cityEntity [CityEntity] instance to be passed.
         * @return A new instance of fragment [WeatherCardFragment].
         */
        @JvmStatic
        fun newInstance(cityEntity: CityEntity) =
                WeatherCardFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(CITY_ENTITY, cityEntity)
                    }
                }
    }

}