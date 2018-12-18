package com.rbiggin.a2do2gether.ui.todo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jakewharton.rxbinding2.view.clicks
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.checklists.ChecklistFragment
import com.rbiggin.a2do2gether.ui.checklists.ChecklistsPresenter
import com.rbiggin.a2do2gether.ui.main.MainActivity
import com.rbiggin.a2do2gether.ui.todo.item.ToDoListItemLayout
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_checklists.*
import kotlinx.android.synthetic.main.fragment_to_do_lists.*
import javax.inject.Inject

class ToDoListsFragment : BaseFragment(), ToDoListsPresenter.View, MainActivity.Listener {

    @Inject
    lateinit var presenter: ToDoListsPresenter

    @Inject
    lateinit var utilities: Utilities

    private var resetIndex: Int? = null

    private val newItemSubject: PublishSubject<String> = PublishSubject.create()
    override fun onNewItemCtreated(): Observable<String> = newItemSubject

    private val menuItemSubject: PublishSubject<Constants.MenuBarItem> = PublishSubject.create()
    override fun onMenuItemSelected(): Observable<Constants.MenuBarItem> = menuItemSubject

    override fun onAttach(context: Context) {
        (context.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)
        presenter.onViewAttached(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_to_do_lists, container, false)
    }

    override fun onStart() {
        super.onStart()
        toDoListsViewPager.adapter = PagerAdapter(childFragmentManager)

        mContext?.let {
            val thing = View.generateViewId()
            val otherThing = ToDoListItemLayout(thing, it)
            //tempLinearLayout.addView(ToDoListItemHeader(otherThing, it))
            //tempLinearLayout.addView(otherThing)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewWillShow()
        toDoListItemAddBtn.clicks().subscribeBy { newItemSubject.onNext(toDoListEt.text.toString()) }
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewWillHide()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

    override fun menuItemPressed(item: Constants.MenuBarItem) = menuItemSubject.onNext(item)

    override fun onBackPressed(): Boolean = true

    override fun setAdapterResetIndex(index: Int) {
        resetIndex = index
    }

    override fun onUpdateAdapter() {
        toDoListsViewPager.adapter = PagerAdapter(childFragmentManager)
        resetIndex?.let {
            toDoListsViewPager.currentItem = it
            resetIndex = null
        }
    }

    override fun clearEditText() {
        toDoListEt.text.clear()
        hideKeyboard()
    }

    override fun currentListId(): String? {
        val fragment = (toDoListsViewPager.adapter as PagerAdapter).getItem(toDoListsViewPager.currentItem)

        if (fragment is ToDoListFragment) {
            return fragment.arguments?.getString(Constants.FRAGMENT_ID)
        } else {
            throw Exception()
        }
    }

    override fun onDisplayPopUpCommand(popUpCommand: Observable<ToDoListsPresenter.PopUpType>): Disposable {
        return popUpCommand.subscribe { command ->
            mContext?.let {
                when (command) {
                    ToDoListsPresenter.PopUpType.SHARE_TO_DO_LIST -> {}
                    ToDoListsPresenter.PopUpType.DELETE_TO_DO_LIST -> {}
                    ToDoListsPresenter.PopUpType.RENAME_TO_DO_LIST -> {}
                    ToDoListsPresenter.PopUpType.NEW_TO_DO_LIST -> {}
                }
            }
        }
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment =
                ToDoListFragment.newInstance(presenter.toDoListsManifest[position])

        override fun getCount(): Int = presenter.toDoListsManifest.size
    }

    companion object {
        fun newInstance(id: String): ToDoListsFragment {
            val fragment = ToDoListsFragment()
            val args = Bundle()
            args.putString(Constants.FRAGMENT_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}
