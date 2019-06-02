package com.suroid.weatherapp.ui.cityselection

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.transition.Transition
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.suroid.weatherapp.R
import com.suroid.weatherapp.models.CityEntity
import com.suroid.weatherapp.utils.*
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_city_selection.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CitySelectionActivity : AppCompatActivity(), CitySelectionAdapter.CityAdapterDelegate, HasSupportFragmentInjector {


    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: CitySelectionViewModel
    private lateinit var adapter: CitySelectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_selection)

        adapter = CitySelectionAdapter(this, this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CitySelectionViewModel::class.java)

        setupEnterAnimation()

        registerViewModelObservers()

        registerViewListeners()

        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_view.adapter = adapter
    }

    /**
     * Registering observers for related liveData from viewModel
     */
    private fun registerViewModelObservers() {
        viewModel.cityEntityListLiveData.observe(this, Observer { cityList ->
            cityList?.let {
                adapter.updateCityList(it)
            }
        })

        viewModel.queryText.observe(this, Observer { query ->
            query?.let {
                et_search.setText(it)
            }
        })

        viewModel.citySelectedLivaData.observe(this, Observer {
            performExit()
        })
    }

    /**
     * Registering related listeners for views
     */
    @SuppressLint("CheckResult")
    private fun registerViewListeners() {
        iv_back.setOnClickListener {
            performExit()
        }

        iv_cancel.setOnClickListener {
            viewModel.refreshData()
        }

        RxTextView.textChanges(et_search)
                .skipInitialValue()
                .takeUntil(RxView.detaches(et_search))
                .debounce(200, TimeUnit.MILLISECONDS)
                .subscribe {
                    viewModel.searchForCities(it.toString())
                }
    }

    /**
     * This method sets up enter animation and makes the root view visible after reveal animation
     */
    private fun setupEnterAnimation() {
        window.sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(p0: Transition?) {
                val cx = (activity_root.left + activity_root.right) / 2
                val cy = (activity_root.top + activity_root.bottom) / 2
                animateRevealShow(activity_root, fab.width / 2, R.color.bg_color, cx,
                        cy, object : OnRevealAnimationListener {
                    override fun onRevealShow() {
                        fab.hide()
                        root_layout.animate().alpha(1f).duration = 300
                        et_search.isFocusableInTouchMode = true
                        et_search.requestFocus()
                        showKeyboard(this@CitySelectionActivity, et_search)
                    }

                    override fun onRevealHide() {
                    }
                })
            }

            override fun onTransitionResume(p0: Transition?) {
            }

            override fun onTransitionPause(p0: Transition?) {
            }

            override fun onTransitionCancel(p0: Transition?) {
            }

            override fun onTransitionStart(p0: Transition?) {
            }

        })
    }

    override fun onItemClick(cityEntity: CityEntity) {
        viewModel.saveSelectedCity(cityEntity)
    }

    override fun onBackPressed() {
        performExit()
    }

    /**
     * Performs exit after reveal Animation
     */
    private fun performExit() {
        fab.show()
        hideKeyboard(this, et_search)
        animateRevealHide(activity_root, R.color.bg_color, fab.width / 2,
                object : OnRevealAnimationListener {

                    override fun onRevealShow() {
                    }

                    override fun onRevealHide() {
                        root_layout.alpha = 0f
                        ActivityCompat.finishAfterTransition(this@CitySelectionActivity)

                    }
                })
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}
