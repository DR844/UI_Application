package com.executor.uiapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.executor.uiapplication.db.UserEntity
import com.executor.uiapplication.db.UserViewModel
import com.executor.uiapplication.uitel.LoadingDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), UserAdapter.RowClickListener,
    SearchView.OnQueryTextListener {

    private lateinit var mUserViewModel: UserViewModel
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_toolbar)
        supportActionBar?.title = "Show User"



        add_User.setOnClickListener {
            val intent = Intent(this, NewUserActivity::class.java)
            loadingCall()
            startActivity(intent)
        }


        adapter = UserAdapter(this@MainActivity, this@MainActivity)
        Recycler_View.adapter = adapter
        Recycler_View.layoutManager = LinearLayoutManager(this@MainActivity)

        mUserViewModel = ViewModelProvider(this@MainActivity)[UserViewModel::class.java]

        mUserViewModel.getAllUser.observe(this@MainActivity) {
            adapter.setListData(it)
        }

    }

    override fun onDeleteUserClickListener(userEntity: UserEntity) {
        val dialog = AlertDialog.Builder(this@MainActivity)
        dialog.setTitle("Are you sure delete  ${userEntity.fName}?")
        dialog.setPositiveButton("Yes") { _, _ ->
            GlobalScope.launch {
                mUserViewModel.deleteUser(userEntity)
            }

            loadingCall()

            Toast.makeText(this, userEntity.fName, Toast.LENGTH_SHORT).show()
        }
        dialog.setNegativeButton("No") { _, _ ->
            Toast.makeText(this, "Not Delete", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    override fun onItemClickListener(userEntity: UserEntity) {
        val intent = Intent(this@MainActivity, UsersDetailsActivity::class.java)

        loadingCall()

        intent.putExtra("id", userEntity.id)
        startActivity(intent)

    }

    override fun onUpdateClickListener(userEntity: UserEntity) {
        val intent = Intent(this@MainActivity, UserUpdateActivity::class.java)

        loadingCall()

        intent.putExtra("id", userEntity.id)
        intent.putExtra("dob", userEntity.dob)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val item = menu?.findItem(R.id.search_View)
        val searchView = item?.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
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

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        mUserViewModel.searchDatabase(searchQuery).observe(this) {
            it.let {
                adapter.setListData(it)
            }
        }
    }

    private fun loadingCall() {
        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed({ loading.isDismiss() }, 1000)

    }

}
