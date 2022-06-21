package com.executor.uiapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.executor.uiapplication.db.UserEntity
import com.executor.uiapplication.db.UserViewModel
import com.executor.uiapplication.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_users_details.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), UserAdapter.RowClickListener,
    SearchView.OnQueryTextListener {

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var mAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(main_toolbar)
        supportActionBar?.title = getString(R.string.main_toolbar)


        add_User.setOnClickListener {
            val liIntent = Intent(this, NewUserActivity::class.java)
            loadingCall()
            startActivity(liIntent)
        }


        mAdapter = UserAdapter(this@MainActivity, this@MainActivity)
        Recycler_View.adapter = mAdapter
        Recycler_View.layoutManager = LinearLayoutManager(this@MainActivity)

        mUserViewModel = ViewModelProvider(this@MainActivity)[UserViewModel::class.java]

        mUserViewModel.getAllUser.observe(this@MainActivity) {
            mAdapter.setListData(it)
        }

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY && add_User.isShown) {
                add_User.hide()
            }
            if (scrollY < oldScrollY && !add_User.isShown) {
                add_User.show()
            }
            if (scrollY == 0) {
                add_User.show()
            }
        })


    }

    override fun onDeleteUserClickListener(userEntity: UserEntity) {
        val lDialog = AlertDialog.Builder(this@MainActivity)
        lDialog.setTitle(getString(R.string.delete_title))
        lDialog.setPositiveButton(getString(R.string.Yes)) { _, _ ->
            GlobalScope.launch {
                mUserViewModel.deleteUser(userEntity)
            }

            loadingCall()

            Toast.makeText(this, userEntity.fName, Toast.LENGTH_SHORT).show()
        }
        lDialog.setNegativeButton(getString(R.string.No)) { _, _ ->
//            Toast.makeText(this, "Not Delete", Toast.LENGTH_SHORT).show()
        }
        lDialog.show()
    }

    override fun onItemClickListener(userEntity: UserEntity) {
        val liIntent = Intent(this@MainActivity, UsersDetailsActivity::class.java)

        loadingCall()

        liIntent.putExtra(getString(R.string.id), userEntity.id)
        startActivity(liIntent)

    }

    override fun onUpdateClickListener(userEntity: UserEntity) {
        val liIntent = Intent(this@MainActivity, UserUpdateActivity::class.java)

        loadingCall()

        liIntent.putExtra(getString(R.string.id), userEntity.id)
        liIntent.putExtra(getString(R.string.dob), userEntity.dob)
        startActivity(liIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val loItem = menu?.findItem(R.id.search_View)
        val lSearchView = loItem?.actionView as SearchView
        lSearchView.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchDatabase(newText)
        }
        return true
    }

    private fun searchDatabase(lsQuery: String) {
        val lsSearchQuery = "%$lsQuery%"
        mUserViewModel.searchDatabase(lsSearchQuery).observe(this) {
            it.let {
                mAdapter.setListData(it)
            }
        }
    }

    private fun loadingCall() {
        val loLoading = LoadingDialog(this)
        loLoading.startLoading()
        val loHandler = Handler()
        loHandler.postDelayed({ loLoading.isDismiss() }, 1000)

    }

}
