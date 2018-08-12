package com.rbiggin.a2do2gether.ui.main

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.checklists.ChecklistsFragment
import com.rbiggin.a2do2gether.ui.connections.MyConnectionsFragment
import com.rbiggin.a2do2gether.ui.login.LoginActivity
import com.rbiggin.a2do2gether.ui.profile.MyProfileFragment
import com.rbiggin.a2do2gether.ui.settings.SettingsFragment
import com.rbiggin.a2do2gether.ui.todo.ToDoListFragment
import com.rbiggin.a2do2gether.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.*
import javax.inject.Inject

/**
 * MainPresenter activity that all primary fragments are contained within.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
                                          MainPresenter.View{

    @Inject lateinit var presenter: MainPresenter

    private lateinit var tag: String

    private var mMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as MyApplication).daggerComponent.inject(this)

        presenter.onViewAttached(this)

        tag = Constants.MAIN_ACTIVITY_TAG
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewWillShow()
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewWillHide()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

    override fun launchLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun setupActivity(email: String){
        setSupportActionBar(findViewById(R.id.appToolbar))

        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.action_bar_title, null)

        supportActionBar?.customView = view

        val toggle = ActionBarDrawerToggle(this, drawerLayout, appToolbar, R.string.password, R.string.enter)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationDrawer.setNavigationItemSelectedListener(this)

        navigationDrawer.menu.getItem(0).setChecked(true)
        navigationDrawer.setCheckedItem(R.id.drawer_to_do_lists)
        navigationDrawer.getHeaderView(0).findViewById<TextView>(R.id.drawerSubHeading).text = email
    }

    /**
     * onBackPressed
     *
     * Description for slightly confusing logic:
     * - if navigation drawer is open, close it
     * - if current fragment is listener, notify fragment, fragment may want to block super function.
     * - if not listener, perform super function.
     */
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainFrameLayout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (currentFragment is IntMainListener){
                if (currentFragment.backPressed()){
                    presenter.onBackPressed()
                    super.onBackPressed()
                }
            } else {
                presenter.onBackPressed()
                super.onBackPressed()
            }
        }
    }

    /**
     * onCreateOptionsMenu
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        mMenu = menu
        return true
    }

    /**
     * onOptionsItemSelected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainFrameLayout)
        if (currentFragment is IntMainListener) {
            when (item.itemId) {
                R.id.action_share -> {
                    currentFragment.menuItemPressed(Constants.MenuBarItem.SHARE_PUBLISH)
                }
                R.id.action_add -> {
                    currentFragment.menuItemPressed(Constants.MenuBarItem.PLUS)
                }
                R.id.action_delete -> {
                    currentFragment.menuItemPressed(Constants.MenuBarItem.DELETE)
                }
                else -> {
                    return super.onOptionsItemSelected(item)
                }
            }
        }
        return true
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val backStackCount = supportFragmentManager.backStackEntryCount
        when (item.itemId) {
            R.id.drawer_to_do_lists -> {
                presenter.onNavDrawerItemSelected(Constants.Fragment.TODO, backStackCount)
            }
            R.id.drawer_checklists -> {
                presenter.onNavDrawerItemSelected(Constants.Fragment.CHECKLIST, backStackCount)
            }
            R.id.drawer_my_connections -> {
                presenter.onNavDrawerItemSelected(Constants.Fragment.MY_CONNECTIONS, backStackCount)
            }
            R.id.drawer_my_profile -> {
                presenter.onNavDrawerItemSelected(Constants.Fragment.MY_PROFILE, backStackCount)
            }
            R.id.drawer_settings -> {
                presenter.onNavDrawerItemSelected(Constants.Fragment.SETTINGS, backStackCount)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun launchFragment(type: Constants.Fragment, toBackStack: Boolean){
        val fragment = when(type){
            Constants.Fragment.TODO -> {
                ToDoListFragment.newInstance(Constants.TODOLIST_FRAGMENT_ID)
            }
            Constants.Fragment.CHECKLIST -> {
                ChecklistsFragment.newInstance(Constants.CHECKLIST_FRAGMENT_ID)
            }
            Constants.Fragment.MY_CONNECTIONS -> {
                MyConnectionsFragment.newInstance(Constants.MY_CONNECTIONS_FRAGMENT_ID)
            }
            Constants.Fragment.MY_PROFILE -> {
               MyProfileFragment.newInstance(Constants.MY_PROFILE_FRAGMENT_ID)
            }
            Constants.Fragment.SETTINGS -> {
                SettingsFragment.newInstance(Constants.SETTINGS_FRAGMENT_ID)
            }
            else -> {
                throw IllegalArgumentException("MainPresenter Activity, launchFragment: has been supplied with an illegal input.")
            }
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrameLayout, fragment)
        if (toBackStack){
            transaction.addToBackStack(type.toString())
        }
        transaction.commit()
    }

    /**
     * Pop Back Stack
     */
    override fun popBackStack() {
        supportFragmentManager.popBackStackImmediate()
    }

    /**
     * Update Action Bar
     */
    override fun updateActionBar(type: Constants.Fragment) {
        setupMenuBarItems(type)
        val view = supportActionBar?.customView?.findViewById(R.id.action_bar_title_view) as TextView
        val heading = when(type){
            Constants.Fragment.TODO -> {
                getString(R.string.to_do_lists)
            }
            Constants.Fragment.CHECKLIST -> {
                getString(R.string.checklists)
            }
            Constants.Fragment.MY_CONNECTIONS -> {
                getString(R.string.my_connections)
            }
            Constants.Fragment.MY_PROFILE -> {
                getString(R.string.my_profile)
            }
            Constants.Fragment.SETTINGS -> {
                getString(R.string.settings)
            }
            else -> {
                throw IllegalArgumentException("MainPresenter Activity, updateActionBar: has been supplied with an illegal input.")
            }
        }
        view.text = heading
    }

    /**
     * Update Profile Picture
     */
    override fun updateProfilePicture(image: Bitmap) {
        navigationDrawer.getHeaderView(0).findViewById<ImageView>(R.id.drawerImageView).setImageBitmap(image)
    }

    /**
     * Update User Name
     */
    override fun updateUsersName(name: String) {
        navigationDrawer.getHeaderView(0).findViewById<TextView>(R.id.drawerHeading).text = name
    }

    /**
     * Setup Menu Bar Items
     */
    private fun setupMenuBarItems(type: Constants.Fragment){
        val addView = mMenu?.getItem(0)
        val deleteView = mMenu?.getItem(1)
        val shareView = mMenu?.getItem(2)
        when(type) {
            Constants.Fragment.TODO -> {
                addView?.setVisible(true)
                deleteView?.setVisible(true)
                shareView?.setVisible(true)
                shareView?.icon = ResourcesCompat.getDrawable(resources, R.drawable.icon_share, null)
            }
            Constants.Fragment.CHECKLIST -> {
                addView?.setVisible(true)
                deleteView?.setVisible(true)
                shareView?.setVisible(true)
                shareView?.icon = ResourcesCompat.getDrawable(resources, R.drawable.icon_publish, null)
            }
            Constants.Fragment.MY_CONNECTIONS -> {
                addView?.setVisible(true)
                deleteView?.setVisible(false)
                shareView?.setVisible(false)
            }
            Constants.Fragment.MY_PROFILE -> {
                addView?.setVisible(false)
                deleteView?.setVisible(false)
                shareView?.setVisible(false)
            }
            Constants.Fragment.SETTINGS -> {
                addView?.setVisible(false)
                deleteView?.setVisible(false)
                shareView?.setVisible(false)
            }
            else -> {
                throw IllegalArgumentException("MainPresenter Activity, setupMenuBarItem: has been supplied with an illegal input.")
            }
        }
    }

    override fun updateNavigationDrawer(type: Constants.Fragment) {
        when(type) {
            Constants.Fragment.TODO -> {
                navigationDrawer.setCheckedItem(R.id.drawer_to_do_lists)
            }
            Constants.Fragment.CHECKLIST -> {
                navigationDrawer.setCheckedItem(R.id.drawer_checklists)
            }
            Constants.Fragment.MY_CONNECTIONS -> {
                navigationDrawer.setCheckedItem(R.id.drawer_my_connections)
            }
            Constants.Fragment.MY_PROFILE -> {
                navigationDrawer.setCheckedItem(R.id.drawer_my_profile)
            }
            Constants.Fragment.SETTINGS -> {
                navigationDrawer.setCheckedItem(R.id.drawer_settings)
            }
            else -> {
                throw IllegalArgumentException("MainPresenter Activity, updateNavigationDrawer: has been supplied with an illegal input.")
            }
        }
    }

    /**
     * Save Profile Picture
     */
    override fun saveProfilePicture(image: Bitmap, uid: String) {
        saveImageToInternalStorage(image, uid)
    }

    /**
     * Save Image to Internal Storage
     */
    private fun saveImageToInternalStorage(bitmapImage: Bitmap, uid: String) {
        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE)
        val mPath = File(directory, "$uid.jpg")
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(mPath)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            try {
                fileOutputStream!!.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
    }

    /**
     * Get Profile Picture
     */
    override fun getProfilePicture(uid: String): Bitmap? {
        return loadImageFromStorage(uid)
    }

    /**
     * Load Image from Storage
     */
    private fun loadImageFromStorage(uid: String): Bitmap?{
        val contextWrapper = ContextWrapper(applicationContext)
        val directory = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE)
        try {
            val file = File(directory, "$uid.jpg")
            return BitmapFactory.decodeStream(FileInputStream(file))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
}
