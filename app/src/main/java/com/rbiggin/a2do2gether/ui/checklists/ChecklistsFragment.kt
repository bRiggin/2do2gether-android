package com.rbiggin.a2do2gether.ui.checklists

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.main.MainActivity
import com.rbiggin.a2do2gether.ui.settings.ChecklistsPresenter
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_checklists.*
import javax.inject.Inject

class ChecklistsFragment : BaseFragment(), ChecklistsPresenter.View, MainActivity.Listener {

    @Inject lateinit var presenter: ChecklistsPresenter

    @Inject lateinit var utilities: Utilities

    private var resetIndex: Int? = null

    val newItemSubject: PublishSubject<String> = PublishSubject.create()

    val menuItemSubject: PublishSubject<Constants.MenuBarItem> = PublishSubject.create()

    override fun onAttach(context: Context?) {
        (context?.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)
        presenter.onViewAttached(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_checklists, container, false)
    }

    override fun onStart() {
        super.onStart()

        checklistsViewPager.adapter = PagerAdapter(childFragmentManager)
        pageIndicator.attachToViewPager(checklistsViewPager)
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewWillShow()
        checklistsItemAddBtn.clicks().subscribeBy {
            newItemSubject.onNext(checklistEt.text.toString())
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewWillHide()

        checklistsItemAddBtn.clicks().subscribeBy {
            newItemSubject.onNext(checklistEt.text.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

    override fun menuItemPressed(item: Constants.MenuBarItem) {
        menuItemSubject.onNext(item)
    }

    override fun onBackPressed(): Boolean {
        return true
    }

    override fun setAdapterResetIndex(index: Int) {
        resetIndex = index
    }

    override fun onUpdateAdapter() {
        checklistsViewPager.adapter = PagerAdapter(childFragmentManager)
        resetIndex?.let {
            checklistsViewPager.currentItem = it
            resetIndex = null
        }
    }

    override fun clearEditText() {
        checklistEt.text.clear()
        hideKeyboard()
    }

    override fun currentListId(): String? {
        val fragment = (checklistsViewPager.adapter as PagerAdapter).getItem(checklistsViewPager.currentItem)

        //val fragment =  childFragmentManager.fragments.get(checklistsViewPager.currentItem) //findFragmentByTag(
        //        "android:switcher:" + R.id.checklistsViewPager + ":" + checklistsViewPager.currentItem
        //)
        //val fragment = checklistsViewPager.adapter?.getItem()
        if (fragment is ChecklistFragment){
            return fragment.arguments?.getString(Constants.FRAGMENT_ID)
        } else {
            throw Exception()
        }
    }

    override fun displayNewChecklistDialog() {
        mContext?.let {
            utilities.showTextEntryDialog(it, getString(R.string.new_checklist),
                    getString(R.string.new_checklist_hint),
                    posButtonText = getString(R.string.create),
                    negButtonText = getString(R.string.cancel),
                    positiveCode = {text -> presenter.newCurrentChecklist(text)})
        }

    }

    override fun displayDeleteChecklistDialog() {
        mContext?.let {
            utilities.showFunctionDialog(it, getString(R.string.delete_checklist),
                    getString(R.string.delete_checklist_description),
                    posButtonText = getString(R.string.delete),
                    negButtonText = getString(R.string.cancel),
                    positiveCode = {
                        presenter.deleteCurrentChecklist(checklistsViewPager.currentItem)
                        checklistsViewPager.currentItem = 0
                    })
        }
    }

    override fun displayPublishChecklistDialog() {
        mContext?.let {
            utilities.showTextEntryDialog(it, getString(R.string.publish_checklist),
                    getString(R.string.publish_checklist_hint),
                    posButtonText = getString(R.string.publish),
                    negButtonText = getString(R.string.cancel))
        }
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return ChecklistFragment.newInstance(presenter.checklistManifest[position])
        }

        override fun getCount(): Int {
            return presenter.checklistManifest.size
        }
    }

    companion object {
        fun newInstance(id: String): ChecklistsFragment {
            val fragment = ChecklistsFragment()
            val args = Bundle()
            args.putString(Constants.FRAGMENT_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}
