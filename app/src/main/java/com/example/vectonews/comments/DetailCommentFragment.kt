package com.example.vectonews.comments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.example.vectonews.R
import com.example.vectonews.databinding.FragmentDetailCommentBinding
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.util.Constants
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar


class DetailCommentFragment :  BottomSheetDialogFragment(R.layout.fragment_detail_comment) {

    private var _binding: FragmentDetailCommentBinding? = null
    private val binding get() = _binding!!

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val passCommentsToDetailFragment: SharedPreferences by lazy {
        requireContext().applicationContext.getSharedPreferences(
            Constants. passCommentsToDetailFragment,
            Context.MODE_PRIVATE
        )
    }

    private val settings: AppSettings by lazy {
        AppSettings(requireContext())
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailCommentBinding.bind(view)


        binding.apply {


            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            bottomSheetBehavior.isDraggable = false
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.bottomSheetLayout.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.slide_in_from_right
                )
            )

            bottomSheetLayout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels

            imgClose2.setOnClickListener {
                try {
                    dismissWithSlideDownAnimation()
                } catch (_: Exception) {
                }
            }

            imageView29.setOnClickListener {

                try {
                    val popupMenu = PopupMenu(requireContext(), binding.imageView29)
                    popupMenu.menu.add("Report an abuse").setOnMenuItemClickListener {
                        try {
                            showSnackbar_Flaged_Report("Thank for you")
                        } catch (ignore: java.lang.Exception) {
                        }
                        false
                    }
                    popupMenu.show()
                } catch (ignored: java.lang.Exception) {
                }


            }

        }


        handler.postDelayed(Runnable {
            loaddata()
        }, 500)



    }



    private fun dismissWithSlideDownAnimation() {
        val slideDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right)
        slideDownAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                dismiss()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        binding.bottomSheetLayout.startAnimation(slideDownAnimation)
    }


    override fun getTheme(): Int {
        return R.style.BottomSheetDialogStyle
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            handler.removeCallbacksAndMessages(null)
            _binding = null

        } catch (_: Exception) {
        }
    }

    private fun loaddata() {
        binding.apply {
            val  userName = passCommentsToDetailFragment.getString("userName", "")
            val  userImage = passCommentsToDetailFragment.getString("userImage", "")
            val  desc = passCommentsToDetailFragment.getString("detail_comment", "")

            textProfileName.text = userName.toString()
            textDetailsComments.text = desc.toString()


            requireContext().let {
                Glide.with(it)
                    .load(userImage.toString())
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBar.isVisible = true
                            textDetailsComments.isVisible = false
                            textProfileName.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            progressBar.isVisible = false
                            textDetailsComments.isVisible = true
                            textProfileName.isVisible = desc != null
                            textProfileName.isVisible = desc != null
                            return false
                        }
                    })
                    .into(imageProfiler3)
            }




        }

    }

    @SuppressLint("ShowToast")
    private fun showSnackbar_Flaged_Report(text: String) {

        if (settings.getTheme() == AppSettings.THEME_DARK){

            val snackbar = Snackbar.make(binding.swipeLayout, text, 3000)

            snackbar.setTextColor(requireContext().resources.getColor(R.color.white))
            snackbar.setBackgroundTint(requireContext().resources.getColor(R.color.white))
            snackbar.setActionTextColor(requireContext().resources.getColor(R.color.white))
            snackbar.setAction(
                "UNDO"
            ) { view ->
                Toast.makeText(view.context.applicationContext, "Okay", Toast.LENGTH_SHORT).show()
            }
            snackbar.show()

        }else {
            val snackbar = Snackbar.make(binding.swipeLayout, text, 3000)
            snackbar.setTextColor(requireContext().resources.getColor(R.color.black))
            snackbar.setBackgroundTint(requireContext().resources.getColor(R.color.black_gray_1))
            snackbar.setActionTextColor(requireContext().resources.getColor(R.color.black))
            snackbar.setAction(
                "UNDO"
            ) { view ->
                Toast.makeText(view.context.applicationContext, "Okay", Toast.LENGTH_SHORT).show()
            }
            snackbar.show()

        }


    }


}