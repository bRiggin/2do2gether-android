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
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.settings.ChecklistsPresenter
import com.rbiggin.a2do2gether.utils.Constants
import kotlinx.android.synthetic.main.fragment_checklists.*
import timber.log.Timber
import javax.inject.Inject

class ChecklistsFragment : BaseFragment(), ChecklistsPresenter.View {

    @Inject lateinit var presenter: ChecklistsPresenter

    private var checklistsPagerAdapter: SectionsPagerAdapter? = null

    private var checklistManifest: ArrayList<String> = ArrayList()

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
        activity?.let{
            checklistsPagerAdapter = SectionsPagerAdapter(it.supportFragmentManager)

            checklistsViewPager.adapter = checklistsPagerAdapter
            pageIndicator.attachToViewPager(checklistsViewPager)
        } ?: Timber.d("add information") //todo add more information, should be excepotion thrown?
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

    override fun onCheckListManifestUpdate(manifest: ArrayList<String>) {
        checklistManifest = manifest
        checklistsViewPager.adapter?.notifyDataSetChanged()
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun newInstance(id: Int): ChecklistsFragment {
            val fragment = ChecklistsFragment()
            val args = Bundle()
            args.putInt(Constants.FRAGMENT_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}
