package com.example.pcmcdiwyang

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.pcmcdiwyang.databinding.ActivitySplashScreenBinding

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private var isNavigation = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_side)
        val slideAnimation1 = AnimationUtils.loadAnimation(this, R.anim.slide_side)
        binding.imageLogo.visibility = View.VISIBLE
        binding.imageLogo.startAnimation(slideAnimation)

        slideAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                    binding.imageName.startAnimation(slideAnimation1)
                binding.imageName.visibility = View.VISIBLE

            }
            override fun onAnimationRepeat(animation: Animation) {}
        })

        slideAnimation1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                Handler().postDelayed({
                    startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                    finish()
                }, 1000)
                   /* startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    finish()*/
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}