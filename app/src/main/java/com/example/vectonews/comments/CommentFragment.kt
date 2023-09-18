package com.example.vectonews.comments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.signIn.GoogleSignInViewModel
import com.example.vectonews.R
import com.example.vectonews.api.UnsplashPhoto
import com.example.vectonews.databinding.FragmentCommentBinding
import com.example.vectonews.settings.AppSettings
import com.example.vectonews.signIn.CommentSignInGoogleFragment
import com.example.vectonews.signIn.CustomPopupFragment
import com.example.vectonews.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CommentFragment : BottomSheetDialogFragment(R.layout.fragment_comment),
    AdapterComment.OnItemClickListener, AdapterComment.OnItemDeleteListener {


    private val sharedDass: SharedPreferences by lazy {
        requireContext().applicationContext.getSharedPreferences(
            Constants.USER_PROFILE, Context.MODE_PRIVATE
        )
    }


    private val passCommentsToDetailFragment: SharedPreferences by lazy {
        requireContext().applicationContext.getSharedPreferences(
            Constants.passCommentsToDetailFragment, Context.MODE_PRIVATE
        )
    }

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val googleSignInViewModel: GoogleSignInViewModel by viewModels()
    private lateinit var googlesignInClient: GoogleSignInClient

    private lateinit var mLauncher: ActivityResultLauncher<Intent>


    private val adapter: AdapterComment by lazy {
        AdapterComment(
            this, this,
        )
    }

    private val settings: AppSettings by lazy {
        AppSettings(requireContext())
    }


    private val commentViewModel by viewModels<CommentViewModel>()


    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCommentBinding.bind(view)

        val filter = IntentFilter("CommentFragmentGoogleSign")
        requireContext().registerReceiver(TrialSignInGoogle, filter)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googlesignInClient = GoogleSignIn.getClient(requireContext(), gso)


        mLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    googleSignInViewModel.handleGoogleSignInResult(task)

                    if (task.isSuccessful) {
                        showToast("Account Created")
                    }
                }
            }


        val userImage = sharedDass.getString("userImage", "")!!

        Glide.with(requireContext()).load(userImage).centerCrop()
            .error(R.drawable.ic_launcher_background).into(binding.profileImage)



        binding.apply {


            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            bottomSheetBehavior.isDraggable = false
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.bottomSheetLayout.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext(), R.anim.slide_in_from_right
                )
            )

            bottomSheetLayout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels

            closeBs.setOnClickListener {
                try {
                    dismissWithSlideDownAnimation()
                } catch (_: Exception) {
                }
            }





            editText2Simple.setOnClickListener {

                val userId = sharedDass.getString("userId", "")!!

                if (userId.isEmpty()) {
                    val customPopupFragment = CustomPopupFragment()
                    customPopupFragment.show(childFragmentManager, customPopupFragment.tag)
                } else {
                    showKeyboard()
                }


            }



            editText2Simple.setOnFocusChangeListener { view, b ->
                if (b) {
                    val userId = sharedDass.getString("userId", "")!!
                    if (userId.isEmpty()) {
                        val customPopupFragment = CommentSignInGoogleFragment()
                        customPopupFragment.show(childFragmentManager, customPopupFragment.tag)

                        editText2Simple.isEnabled = false
                        handler.postDelayed(Runnable {
                            editText2Simple.isEnabled = true
                        }, 200)


                    } else {
                        showKeyboard()
                    }
                }
            }





            editText2Simple.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (s.length > 0) {

                        try {

                            imgSendCleare.visibility = View.INVISIBLE
                            imgSendSimple.visibility = View.VISIBLE
                            binding.progressUplaoding.visibility = View.INVISIBLE
                        } catch (_: Exception) {
                        }
                    } else {
                        try {
                            imgSendCleare.visibility = View.VISIBLE
                            imgSendSimple.visibility = View.INVISIBLE
                            binding.progressUplaoding.visibility = View.INVISIBLE
                            editText2Simple.clearFocus()

                        } catch (_: Exception) {
                        }
                    }
                }
            })


            imgSendSimple.setOnClickListener {
                val desc_edit_text = editText2Simple.text.toString().trim { it <= ' ' }
                val userId = sharedDass.getString("userId", "")!!

                val connectivityManager =
                    requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo

                if (networkInfo != null && networkInfo.isConnected) {

                    if (desc_edit_text.isNotEmpty() && userId.isNotEmpty()) {

                        try {
                            checkTotalCommentOnPerNewsArticle(desc_edit_text)
                            hideKeyBaord(editText2Simple)
                            addContentCons.setVisibility(View.INVISIBLE)
                            progressUplaoding.visibility = View.VISIBLE
                            imgSendCleare.visibility = View.INVISIBLE
                            imgSendSimple.visibility = View.INVISIBLE
                            textViewError.visibility = View.INVISIBLE

                            handler.postDelayed(Runnable {
                                fectData()

                            }, 400)

                        } catch (_: Exception) {
                        }
                    } else {

                        try {
                            hideKeyBaord(editText2Simple)
                            progressUplaoding.visibility = View.INVISIBLE
                            addContentCons.visibility = View.INVISIBLE
                            textViewError.visibility = View.INVISIBLE
                            imgSendCleare.visibility = View.INVISIBLE
                            imgSendSimple.visibility = View.INVISIBLE

                            showToast("Something went wrong")

                        } catch (_: Exception) {
                        }
                    }


                } else {


                    try {
                        hideKeyBaord(editText2Simple)
                        binding.progressUplaoding.visibility = View.VISIBLE
                        imgSendCleare.visibility = View.INVISIBLE
                        imgSendSimple.visibility = View.INVISIBLE

                        showToast("Check internet connectivity")

                    } catch (_: Exception) {
                    }

                }


            }

            imgSendCleare.setOnClickListener { v: View? ->
                hideKeyBaord(editText2Simple)
                editText2Simple.setText("")
                binding.addContentCons.setVisibility(View.INVISIBLE)
            }


        }

        handler.postDelayed(Runnable {
            fectData()
        }, 500)
    }


    private fun dismissWithSlideDownAnimation() {
        val slideDownAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right)
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

    private fun showKeyboard() {
        try {
            val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editText2Simple, InputMethodManager.SHOW_IMPLICIT)
        } catch (_: Exception) {
        }
    }


    private fun hideKeyBaord(editText: EditText) {
        try {
            editText.clearFocus()
            val imm =
                requireContext().applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editText.windowToken, 0)
        } catch (ignored: java.lang.Exception) {
        }
    }


    private fun checkTotalCommentOnPerNewsArticle(desc_edit_text: String) {

        val newsArticleId = sharedDass.getString("newsArticleId", "")!!

        commentViewModel.checkTotalComments(newsArticleId) { isUnderLimit ->
            if (isUnderLimit) {
                checkIfCommentKeyExistOrNot() // go check if comment key already exist
                saveNewsArticleComment(desc_edit_text)
            } else {
                try {
                    binding.editText2Simple.setText("")
                    binding.progressUplaoding.visibility = View.INVISIBLE
                } catch (_: Exception) {
                }
            }
        }

    }


    private fun checkIfCommentKeyExistOrNot() {

        val newsArticleId = sharedDass.getString("newsArticleId", "")!!

        commentViewModel.checkIfCommentKeyExists(newsArticleId) { commentKeyNotExists ->
            if (commentKeyNotExists) {
                saveCommentKey()
            }
        }


    }


    // Now save the comment key
    private fun saveCommentKey() {
        val newsArticleId = sharedDass.getString("newsArticleId", "")!!

        val database = FirebaseDatabase.getInstance()
        val dataRef = database.getReference(Constants.commentKey)
        val data = DataItem(newsArticleId, Date().toString())
        dataRef.child(newsArticleId).setValue(data)


    }


    private fun saveNewsArticleComment(desc_edit_text: String) {

        val newsArticleId = sharedDass.getString("newsArticleId", "")!!
        val userId = sharedDass.getString("userId", "")!!
        val userName = sharedDass.getString("userName", "")!!
        val userImage = sharedDass.getString("userImage", "")!!

        val timestamp = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH)
        val formattedDate = dateFormat.format(Date(timestamp))


        val commentData = hashMapOf(
            "name" to userName,
            "userImage" to userImage,
            "userId" to userId,
            "desc" to desc_edit_text,
            "postDate" to formattedDate.toString(),
            "articleId" to newsArticleId,
            "commentKey" to "",

            )


        commentViewModel.saveComment(commentData) { isSuccess, pushKey ->
            if (isSuccess) {
                try {
                    binding.editText2Simple.setText("")
                    binding.progressUplaoding.visibility = View.INVISIBLE

                    if (!pushKey.isNullOrEmpty()) {
                        commentData["commentKey"] = pushKey
                        // Now you can update the comment's commentKey field
                        commentViewModel.updateCommentField(
                            newsArticleId, pushKey, "commentKey", pushKey
                        )
                    }


                } catch (_: Exception) {
                }
            } else {
                // Handle error saving comment
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun fectData() {
        binding.apply {

            val connectivityManager =
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            if (networkInfo != null && networkInfo.isConnected) {

                val layoutManager = LinearLayoutManager(requireContext())
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true // Optional, keeps the bottom item visible

                recyclerViewSql.layoutManager = layoutManager
                recyclerViewSql.adapter = adapter

                commentViewModel.newsFeedLiveData.observe(viewLifecycleOwner, Observer { it ->
                    if (it.isNullOrEmpty()) {

                        try {
                            progressBarDownlaods.visibility = View.INVISIBLE
                            addContentCons.visibility = View.VISIBLE
                            addContentCons.text = "Be the first to comment"
                            textViewError.visibility = View.INVISIBLE
                        } catch (_: Exception) {
                        }

                    } else {
                        try {
                            adapter.setData(it)
                            progressBarDownlaods.visibility = View.INVISIBLE
                            addContentCons.visibility = View.INVISIBLE
                            textViewError.visibility = View.INVISIBLE

                        } catch (_: Exception) {
                        }
                    }
                })


            } else {
                try {
                    addContentCons.visibility = View.VISIBLE
                    addContentCons.text = "You are currently offline"
                    progressBarDownlaods.visibility = View.INVISIBLE
                    textViewError.visibility = View.VISIBLE
                    textViewError.setOnClickListener {

                        progressBarDownlaods.visibility = View.VISIBLE
                        addContentCons.visibility = View.INVISIBLE
                        textViewError.visibility = View.INVISIBLE

                        handler.postDelayed(Runnable {
                            fectData()

                        }, 400)

                    }
                } catch (_: Exception) {
                }
            }

        }

    }


    override fun onItemClicked(photo: ModelComment) {

        /// opening the detail comment fragment
        val detailCommentFragment = DetailCommentFragment()
        detailCommentFragment.show(childFragmentManager, detailCommentFragment.tag)
        passCommentsToDetailFragment(
            "" + photo.name.toString(),
            "" + photo.userImage.toString(),
            "" + photo.desc.toString(),
            "" + photo.userId.toString(),
            "" + photo.articleId.toString(),
            "" + photo.commentKey.toString(),
        )

    }


    override fun onItemDeleteListner(photo: ModelComment) {

        //  open delete bs comment
        val bscommentFragment = BSCommentFragment()
        bscommentFragment.show(childFragmentManager, bscommentFragment.tag)
        passCommentsToDetailFragment(
            "" + photo.name,
            "" + photo.userImage,
            "" + photo.desc,
            "" + photo.userId,
            "" + photo.articleId,
            "" + photo.commentKey,
        )

    }


    private val TrialSignInGoogle = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                val typeOfBottomSheet = intent.getStringExtra(Constants.typeOfBottomSheet)

                if (typeOfBottomSheet.equals(Constants.DeleteComment_By_BSc_Order)) {
                    deletUsercomment()
                } else if (typeOfBottomSheet.equals(Constants.OpenComment_By_BSc_Order)) {
                    /// opening the detail comment fragment
                    val detailCommentFragment = DetailCommentFragment()
                    detailCommentFragment.show(childFragmentManager, detailCommentFragment.tag)
                } else if (typeOfBottomSheet.equals(Constants.showSnackbar_Flaged_Report)) {
                    showSnackbar_Flaged_Report("Thanks for letting us know about it")
                } else {
                    signinWithGoogle()
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun signinWithGoogle() {
        val siginIntent = googlesignInClient.signInIntent
        mLauncher.launch(siginIntent)
    }


    private fun showToast(message: String) {
        try {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        } catch (ignored: Exception) {
        }
    }


    private fun funDeleteComments(newsArticleId: String, itemNewsKey: String) {

        commentViewModel.deleteComment(newsArticleId, itemNewsKey) { isSuccess ->
            if (isSuccess) {

            }
        }

    }


    private fun passCommentsToDetailFragment(
        userName: String,
        userImage: String,
        detail_comment: String,
        userId: String,
        newsArticleId: String,
        commentKey: String,
    ) {
        val editor = passCommentsToDetailFragment.edit()
        editor.putString("userName", userName)
        editor.putString("userImage", userImage)
        editor.putString("detail_comment", detail_comment)
        editor.putString("userId", userId)
        editor.putString("newsArticleId", newsArticleId)
        editor.putString("commentKey", commentKey)
        editor.apply()
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            handler.removeCallbacksAndMessages(null)
            _binding = null
            requireContext().unregisterReceiver(TrialSignInGoogle)

        } catch (_: Exception) {
        }
    }


    private fun showSnackbar_Flaged_Report(text: String) {

        try {
            if (settings.getTheme() == AppSettings.THEME_LIGHT) {

                val snackbar = Snackbar.make(binding.bottomSheetLayout, text, 3000)

                snackbar.setTextColor(requireContext().resources.getColor(R.color.black))
                snackbar.setBackgroundTint(requireContext().resources.getColor(R.color.white))
                snackbar.setActionTextColor(requireContext().resources.getColor(R.color.black))
                snackbar.setAction(
                    "UNDO"
                ) { view ->

                    showToast("Okay")

                }
                snackbar.show()

            } else {
                val snackbar = Snackbar.make(binding.bottomSheetLayout, text, 3000)
                snackbar.setTextColor(requireContext().resources.getColor(R.color.white))
                snackbar.setBackgroundTint(requireContext().resources.getColor(R.color.black))
                snackbar.setActionTextColor(requireContext().resources.getColor(R.color.white))
                snackbar.setAction(
                    "UNDO"
                ) { view ->
                    showToast("Okay")
                }
                snackbar.show()

            }

        } catch (_: Exception) {
        }

    }


    private fun deletUsercomment() {

        try {
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton("Yes") { _, _ ->
                val itemNewsKey = passCommentsToDetailFragment.getString("commentKey", "")
                val newsArticleId = passCommentsToDetailFragment.getString("newsArticleId", "")
                funDeleteComments("" + newsArticleId, "" + itemNewsKey)
            }
            builder.setNegativeButton("No") { _, _ -> }
            builder.setTitle("Delete Comment?")
            builder.create().show()
        } catch (_: Exception) {
        }
    }


}