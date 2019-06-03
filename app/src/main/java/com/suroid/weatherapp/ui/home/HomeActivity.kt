package com.suroid.weatherapp.ui.home

import android.Manifest
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding2.view.RxView
import com.patloew.rxlocation.RxLocation
import com.suroid.weatherapp.R
import com.suroid.weatherapp.ui.cityselection.CitySelectionActivity
import com.suroid.weatherapp.utils.CoverTransformer
import com.suroid.weatherapp.utils.Errors
import com.suroid.weatherapp.utils.extensions.setAllOnClickListener
import com.suroid.weatherapp.utils.extensions.showPermissionDialog
import com.suroid.weatherapp.utils.extensions.showToast
import com.suroid.weatherapp.utils.setupProgressAnimation
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.Maybe
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject


class HomeActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: HomeViewModel

    private lateinit var adapter: HomeViewPagerAdapter

    private var animationSet = AnimatorSet()

    @Inject
    lateinit var rxLocation: RxLocation

    private val rxPermissions = RxPermissions(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black_alpha_40)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)

        setupViewPager()
        registerViewListeners()
        registerViewModelObservers()

        setupProgressAnimation(animationSet, progress_bar)
    }

    private fun setupViewPager() {
        adapter = HomeViewPagerAdapter(supportFragmentManager)
        view_pager.adapter = adapter
        view_pager.offscreenPageLimit = 2
        view_pager.pageMargin = (-resources.displayMetrics.widthPixels * 0.25).toInt()
        view_pager.setPageTransformer(false, CoverTransformer(0.2f))
    }

    /**
     * Registering observers for related liveData from viewModel
     */
    private fun registerViewModelObservers() {
        viewModel.cityListLiveData.observe(this, Observer { cityList ->
            cityList?.let {
                if (it.isNotEmpty()) {
                    group_welcome.visibility = View.GONE
                    adapter.updateList(it)
                } else {
                    group_welcome.visibility = View.VISIBLE
                }
            }
        })

        viewModel.loading.observe(this, Observer { visibility ->
            visibility?.let {
                if (it) {
                    group_welcome.visibility = View.GONE
                    progress_bar.visibility = View.VISIBLE
                    animationSet.start()
                } else {
                    animationSet.cancel()
                    group_welcome.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE
                }
            }
        })

        viewModel.fetchCityResult.observe(this, Observer {
            if (it == false) {
                showToast(R.string.cannot_find_location)
            }
        })
    }

    /**
     * Registering related listeners for views
     */
    private fun registerViewListeners() {
        fab.setOnClickListener {
            val intent = Intent(this@HomeActivity, CitySelectionActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@HomeActivity, fab, ViewCompat.getTransitionName(fab)
                    ?: "")
            ActivityCompat.startActivityForResult(this@HomeActivity, intent, 0, options.toBundle())
        }

        group_welcome.setAllOnClickListener(::setClickListener)
    }

    @SuppressLint("CheckResult", "MissingPermission")
    private fun setClickListener(view: View) {
        RxView.clicks(view)
                .compose(rxPermissions.ensureEach(Manifest.permission.ACCESS_COARSE_LOCATION))

                .flatMapMaybe { permission ->
                    if (permission.granted) {
                        rxLocation.location().lastLocation()
                    } else {
                        showPermissionDialog(permission.shouldShowRequestPermissionRationale) {
                            showToast(R.string.permission_error)
                        }
                        Maybe.error(Errors.PermissionError("Could not find location"))
                    }
                }
                .takeUntil(RxView.detaches(view))
                .subscribe({ location ->
                    location?.let {
                        viewModel.fetchForCurrentLocation(location = it)
                    } ?: run { showToast(R.string.cannot_find_location) }
                }, {
                    if (it !is Errors) {
                        showToast(R.string.please_check_gps)
                    }
                    // Resubscribe to clicks on error
                    setClickListener(view)

                })
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}


