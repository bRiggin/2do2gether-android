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
import android.util.Log
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

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        MainPresenter.View {

    @Inject
    lateinit var presenter: MainPresenter

    private val tag: String = Constants.MAIN_ACTIVITY_TAG

    private var mMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as MyApplication).daggerComponent.inject(this)

        Log.w(tag, "intent: $intent")
        Log.w(tag, "intent extra: ${intent.extras}")

        if (intent.hasExtra(Constants.LOAD_FRAGMENT)) {
            presenter.onViewAttached(this, intent.extras.getString(Constants.LOAD_FRAGMENT))
        } else {
            presenter.onViewAttached(this, null)
        }
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

    override fun setupActivity(email: String) {
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
            if (currentFragment is IntMainListener) {
                if (currentFragment.onBackPressed()) {
                    presenter.onBackPressed()
                    super.onBackPressed()
                }
            } else {
                presenter.onBackPressed()
                super.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        mMenu = menu
        presenter.reloadMenuButtons()
        return true
    }

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

    override fun launchFragment(type: Constants.Fragment, toBackStack: Boolean) {
        val fragment = when (type) {
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
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrameLayout, fragment)
        if (toBackStack) {
            transaction.addToBackStack(type.toString())
        }
        transaction.commit()
    }

    override fun popBackStack() {
        supportFragmentManager.popBackStackImmediate()
    }

    override fun updateActionBar(type: Constants.Fragment) {
        setupMenuBarItems(type)
        val view = supportActionBar?.customView?.findViewById(R.id.action_bar_title_view) as TextView
        val heading = when (type) {
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
        }
        view.text = heading
    }

    override fun updateProfilePicture(image: Bitmap) {
        navigationDrawer.getHeaderView(0).findViewById<ImageView>(R.id.drawerImageView).setImageBitmap(image)
    }

    override fun updateUsersName(name: String) {
        navigationDrawer.getHeaderView(0).findViewById<TextView>(R.id.drawerHeading).text = name
    }

    private fun setupMenuBarItems(type: Constants.Fragment) {
        val addView = mMenu?.getItem(0)
        val deleteView = mMenu?.getItem(1)
        val shareView = mMenu?.getItem(2)
        when (type) {
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
        }
    }

    override fun updateNavigationDrawer(type: Constants.Fragment) {
        when (type) {
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
        }
    }

    override fun saveProfilePicture(image: Bitmap, uid: String) {
        saveImageToInternalStorage(image, uid)
    }

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

    override fun getProfilePicture(uid: String): Bitmap? {
        return loadImageFromStorage(uid)
    }

    private fun loadImageFromStorage(uid: String): Bitmap? {
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
