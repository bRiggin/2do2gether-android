package com.rbiggin.a2do2gether.ui.main

import android.content.Context
import android.content.ContextWrapper
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
import com.rbiggin.a2do2gether.ui.profile.MyProfileFragment
import com.rbiggin.a2do2gether.ui.settings.SettingsFragment
import com.rbiggin.a2do2gether.ui.todo.ToDoListFragment
import com.rbiggin.a2do2gether.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.*
import javax.inject.Inject

/**
 * Main activity that all primary fragments are contained within.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
                                          IntMainActivity{
    /** injected instance of Activity's presenter. */
    @Inject lateinit var presenter: IntMainPresenter

    /** injected instance of Constants. */
    @Inject lateinit var constants: Constants

    /** Activity's logging TAG */
    private lateinit var tag: String

    /** Activity's menu */
    private var mMenu: Menu? = null

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as MyApplication).daggerComponent.inject(this)

        presenter.setView(this)
        presenter.onViewWillShow(intent.getStringExtra("email"))

        tag = constants.MAIN_ACTIVITY_TAG
    }

    /**
     * Setup Activity
     */
    override fun setupActivity(email: String){
        setSupportActionBar(findViewById(R.id.customToolbar))

        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.action_bar_title, null)

        supportActionBar?.customView = view

        val toggle = ActionBarDrawerToggle(this, drawerLayout, customToolbar, R.string.password, R.string.enter)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        navigationView.menu.getItem(0).setChecked(true)
        navigationView.setCheckedItem(R.id.drawer_to_do_lists)
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.drawerSubHeading).text = email
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
                    currentFragment.menuItemPressed(constants.menuBarItemSharePublish())
                }
                R.id.action_add -> {
                    currentFragment.menuItemPressed(constants.menuBarItemPlus())
                }
                R.id.action_delete -> {
                    currentFragment.menuItemPressed(constants.menuBarItemDelete())
                }
                else -> {
                    return super.onOptionsItemSelected(item)
                }
            }
        }
        return true
    }

    /**
     * Handles menu drawer items pushes.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val backStackCount = supportFragmentManager.backStackEntryCount
        when (item.itemId) {
            R.id.drawer_to_do_lists -> {
                presenter.onNavDrawerItemSelected(constants.fragmentTypeToDo(), backStackCount)
            }
            R.id.drawer_checklists -> {
                presenter.onNavDrawerItemSelected(constants.fragmentTypeChecklists(), backStackCount)
            }
            R.id.drawer_my_connections -> {
                presenter.onNavDrawerItemSelected(constants.fragmentTypeConnections(), backStackCount)
            }
            R.id.drawer_my_profile -> {
                presenter.onNavDrawerItemSelected(constants.fragmentTypeProfile(), backStackCount)
            }
            R.id.drawer_settings -> {
                presenter.onNavDrawerItemSelected(constants.fragmentTypeSettings(), backStackCount)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Launch Fragment
     */
    override fun launchFragment(type: Constants.FragmentType, toBackStack: Boolean){
        val fragment = when(type){
            constants.fragmentTypeToDo() -> {
                ToDoListFragment.newInstance(constants.TODOLIST_FRAGMENT_ID)
            }
            constants.fragmentTypeChecklists() -> {
                ChecklistsFragment.newInstance(constants.CHECKLIST_FRAGMENT_ID)
            }
            constants.fragmentTypeConnections() -> {
                MyConnectionsFragment.newInstance(constants.MY_CONNECTIONS_FRAGMENT_ID)
            }
            constants.fragmentTypeProfile() -> {
               MyProfileFragment.newInstance(constants.MY_PROFILE_FRAGMENT_ID)
            }
            constants.fragmentTypeSettings() -> {
                SettingsFragment.newInstance(constants.SETTINGS_FRAGMENT_ID)
            }
            else -> {
                throw IllegalArgumentException("Main Activity, launchFragment: has been supplied with an illegal input.")
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
    override fun updateActionBar(type: Constants.FragmentType) {
        setupMenuBarItems(type)
        val view = supportActionBar?.customView?.findViewById(R.id.action_bar_title_view) as TextView
        val heading = when(type){
            constants.fragmentTypeToDo() -> {
                getString(R.string.to_do_lists)
            }
            constants.fragmentTypeChecklists() -> {
                getString(R.string.checklists)
            }
            constants.fragmentTypeConnections() -> {
                getString(R.string.my_connections)
            }
            constants.fragmentTypeProfile() -> {
                getString(R.string.my_profile)
            }
            constants.fragmentTypeSettings() -> {
                getString(R.string.settings)
            }
            else -> {
                throw IllegalArgumentException("Main Activity, updateActionBar: has been supplied with an illegal input.")
            }
        }
        view.text = heading
    }

    /**
     * Update Profile Picture
     */
    override fun updateProfilePicture(image: Bitmap) {
        navigationView.getHeaderView(0).findViewById<ImageView>(R.id.drawerImageView).setImageBitmap(image)
    }

    /**
     * Update User Name
     */
    override fun updateUsersName(name: String) {
        navigationView.getHeaderView(0).findViewById<TextView>(R.id.drawerHeading).text = name
    }

    /**
     * Setup Menu Bar Items
     */
    private fun setupMenuBarItems(type: Constants.FragmentType){
        val addView = mMenu?.getItem(0)
        val deleteView = mMenu?.getItem(1)
        val shareView = mMenu?.getItem(2)
        when(type) {
            constants.fragmentTypeToDo() -> {
                addView?.setVisible(true)
                deleteView?.setVisible(true)
                shareView?.setVisible(true)
                shareView?.icon = ResourcesCompat.getDrawable(resources, R.drawable.icon_share, null)
            }
            constants.fragmentTypeChecklists() -> {
                addView?.setVisible(true)
                deleteView?.setVisible(true)
                shareView?.setVisible(true)
                shareView?.icon = ResourcesCompat.getDrawable(resources, R.drawable.icon_publish, null)
            }
            constants.fragmentTypeConnections() -> {
                addView?.setVisible(true)
                deleteView?.setVisible(false)
                shareView?.setVisible(false)
            }
            constants.fragmentTypeProfile() -> {
                addView?.setVisible(false)
                deleteView?.setVisible(false)
                shareView?.setVisible(false)
            }
            constants.fragmentTypeSettings() -> {
                addView?.setVisible(false)
                deleteView?.setVisible(false)
                shareView?.setVisible(false)
            }
            else -> {
                throw IllegalArgumentException("Main Activity, setupMenuBarItem: has been supplied with an illegal input.")
            }
        }
    }

    /**
     * Update Navigation Drawer
     */
    override fun updateNavigationDrawer(type: Constants.FragmentType) {
        when(type) {
            constants.fragmentTypeToDo() -> {
                navigationView.setCheckedItem(R.id.drawer_to_do_lists)
            }
            constants.fragmentTypeChecklists() -> {
                navigationView.setCheckedItem(R.id.drawer_checklists)
            }
            constants.fragmentTypeConnections() -> {
                navigationView.setCheckedItem(R.id.drawer_my_connections)
            }
            constants.fragmentTypeProfile() -> {
                navigationView.setCheckedItem(R.id.drawer_my_profile)
            }
            constants.fragmentTypeSettings() -> {
                navigationView.setCheckedItem(R.id.drawer_settings)
            }
            else -> {
                throw IllegalArgumentException("Main Activity, updateNavigationDrawer: has been supplied with an illegal input.")
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
