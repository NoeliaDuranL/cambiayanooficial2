package com.example.cambiayanooficial2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.cambiayanooficial2.databinding.ActivityOnboardingBinding


class OnboardingActivity :  AppCompatActivity(){
    private val binding by lazy {
        ActivityOnboardingBinding.inflate(layoutInflater)
    }

    private lateinit var layouts: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        layouts = intArrayOf(
            R.layout.onboarding_layout_1,
            R.layout.onboarding_layout_2,
            R.layout.onboarding_layout_3
        )
        binding.viewPager.adapter = OnboardingAdapter(this, layouts)

        binding.viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //TODO("Not yet implemented")
            }

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                        binding.actionText.text = getString(R.string.next)
                        binding.actionText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            AppCompatResources.getDrawable(this@OnboardingActivity, R.drawable.ic_arrow_right_double), null)
                    }
                    1 -> {
                        binding.actionText.text = getString(R.string.next)
                        binding.actionText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            AppCompatResources.getDrawable(this@OnboardingActivity, R.drawable.ic_arrow_right_double), null)
                    }
                    2 -> {
                        binding.actionText.text = getString(R.string.start)
                        binding.actionText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            AppCompatResources.getDrawable(this@OnboardingActivity, R.drawable.ic_arrow_rigth_single), null)

                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                //TODO("Not yet implemented")
            }

        })

        binding.actionText.setOnClickListener{
            when(binding.viewPager.currentItem){
                0 -> {
                    binding.viewPager.currentItem = 1
                }
                1 -> {
                    binding.viewPager.currentItem = 2
                }
                2 -> {

                    // Guardamos el estado del onboarding
                    val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putBoolean("hasSeenOnboarding", true)  // Marcamos que el usuario vio el onboarding
                        apply()
                    }
                    // Navegar a la actividad de bienvenida
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
            }
        }
    }

    inner class OnboardingAdapter(private val context: Context, private val layouts: IntArray): PagerAdapter(){
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

}