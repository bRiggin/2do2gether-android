package com.rbiggin.a2do2gether.ui.checklists

import android.content.Context
import android.os.Bundle
import android.provider.SyncStateContract
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.settings.ChecklistsPresenter
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_checklists.*
import timber.log.Timber
import javax.inject.Inject



class ChecklistsFragment : BaseFragment(), ChecklistsPresenter.View {

    @Inject lateinit var presenter: ChecklistsPresenter

    private var checklistsPagerAdapter: SectionsPagerAdapter? = null

    private var checklistManifest: ArrayList<String> = ArrayList()

    val newItemSubject: PublishSubject<String> = PublishSubject.create()

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
        presenter.onViewWillShow()

        activity?.let{
            checklistsPagerAdapter = SectionsPagerAdapter(it.supportFragmentManager)

            checklistsViewPager.adapter = checklistsPagerAdapter
            pageIndicator.attachToViewPager(checklistsViewPager)
        } ?: Timber.d("add information") //todo add more information, should be excepotion thrown?
    }

    override fun onResume() {
        super.onResume()
        checklistsItemAddBtn.clicks().subscribeBy {
            newItemSubject.onNext(checklistEt.text.toString())
        }
    }

    override fun onCheckListManifestUpdate(manifest: ArrayList<String>) {
        checklistManifest = manifest
        checklistsViewPager.adapter?.notifyDataSetChanged()
    }

    override fun clearEditText() {
        checklistEt.text.clear()
        hideKeyboard()
    }

    override fun currentListId(): String? {
        val fragment = activity?.supportFragmentManager?.findFragmentByTag(
                "android:switcher:" + R.id.checklistsViewPager + ":" + checklistsViewPager.currentItem
        ) as ChecklistFragment

        return fragment.mFragmentId
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return ChecklistFragment.newInstance(checklistManifest[position])
        }

        override fun getCount(): Int {
            return checklistManifest.size
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewWillHide()

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
