package com.rbiggin.a2do2gether.feature.connections

import com.nhaarman.mockito_kotlin.*
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.model.UserDetails
import com.rbiggin.a2do2gether.repository.ConnectionsRepository
import com.rbiggin.a2do2gether.repository.SettingsRepository
import com.rbiggin.a2do2gether.repository.UserProfileRepository
import com.rbiggin.a2do2gether.ui.connections.MyConnectionsFragment
import com.rbiggin.a2do2gether.ui.connections.MyConnectionsPresenter
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MyConnectionsPresenterTest {

    private lateinit var presenter: MyConnectionsPresenter
    private val fragment: MyConnectionsFragment = mock()
    private val connectionsRepo: ConnectionsRepository = mock()
    private val userRepo: UserProfileRepository = mock()
    private val settingsRepo: SettingsRepository = mock()
    private val utilities: Utilities = mock()

    private val scheduler = TestScheduler()

    private val pendingRequestsSubject: BehaviorSubject<HashMap<String, UserConnectionRequest>> = BehaviorSubject.create<HashMap<String, UserConnectionRequest>>()

    private val connectionsSubject: BehaviorSubject<HashMap<String, UserDetails>> = BehaviorSubject.create<HashMap<String, UserDetails>>()

    private val connectionSearchSubject: PublishSubject<ArrayList<UserConnectionSearch>> = PublishSubject.create()

    @Before
    fun `configure MyConnections Presenter`() {
        whenever(connectionsRepo.connectionsSubject).doReturn(connectionsSubject)
        whenever(connectionsRepo.connectionSearchSubject).doReturn(connectionSearchSubject)
        whenever(connectionsRepo.pendingRequestsSubject).doReturn(pendingRequestsSubject)

        presenter = MyConnectionsPresenter(connectionsRepo, userRepo, settingsRepo, utilities, scheduler)
    }

    @Test
    fun `on_load presenting_main_view`() {
        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()

        assertThat(presenter.currentView, (equalTo(MyConnectionsPresenter.Window.MAIN_VIEW)))
    }

    @Test
    fun `on_search_view_pressed and_user_not_discoverable presenting_main_view`() {
        whenever(userRepo.isUserDiscoverable()).doReturn(false)

        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()
        presenter.onPlusButtonPressed()

        verify(fragment).onDisplayDialogMessage(Constants.ERROR_USER_NOT_PUBLIC, null)
        assertThat(presenter.currentView, (equalTo(MyConnectionsPresenter.Window.MAIN_VIEW)))
    }

    @Test
    fun `on_search_view_pressed and_user_is_discoverable presenting_search_view`() {
        whenever(userRepo.isUserDiscoverable()).doReturn(true)

        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()

        presenter.onPlusButtonPressed()

        verify(fragment).onDisplayView(MyConnectionsPresenter.Window.SEARCH_VIEW)
        assertThat(presenter.currentView, (equalTo(MyConnectionsPresenter.Window.SEARCH_VIEW)))
    }

    @Test
    fun `on_search_submitted and_no_network_connection display_error_message`() {
        whenever(fragment.hasNetworkConnection()).doReturn(false)

        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()

        presenter.onSearchButtonPressed("")

        verify(fragment).onDisplayDialogMessage(Constants.ERROR_NO_NETWORK, null)
    }

    @Test
    fun `on_search_submitted and_search_string_too_short display_error_message`() {
        whenever(fragment.hasNetworkConnection()).doReturn(true)

        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()

        presenter.onSearchButtonPressed("1234567")

        verify(fragment).onClearSearchView()
        verify(fragment).onDisplayDialogMessage(Constants.ERROR_NICKNAME_STRUCTURE_ERROR, null)
    }

    @Test
    fun `on_search_submitted and_search_has_whitespace display_error_message`() {
        whenever(fragment.hasNetworkConnection()).doReturn(true)

        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()

        presenter.onSearchButtonPressed("123 567")

        verify(fragment).onClearSearchView()
        verify(fragment).onDisplayDialogMessage(Constants.ERROR_NICKNAME_STRUCTURE_ERROR, null)
    }

    @Test
    fun `on_search_submitted and_happy_path trigger_connection_search`() {
        val happyString = "12345678"

        whenever(fragment.hasNetworkConnection()).doReturn(true)

        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()

        presenter.onSearchButtonPressed(happyString)

        verify(connectionsRepo).connectionSearchSubmitted(happyString)

        verify(fragment).displayProgressSpinner(true)
        assertThat(presenter.isProcessingBol, (equalTo(true)))
    }

    @Test
    fun `on_recycler_button_pressed connection_request`() {
        val uid = "uid"

        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()

        presenter.onRecyclerViewButtonPressed(MyConnectionsPresenter.Action.CONNECTION_REQUEST, uid)

        verify(connectionsRepo).submitConnectionRequest(uid)

        verify(fragment).onClearSearchView()
        verify(fragment).onDisplayDialogMessage(Constants.DB_CONNECTION_REQUEST_SUBMITTED, null)
        verify(fragment).onDisplayView(MyConnectionsPresenter.Window.MAIN_VIEW)

        assertThat(presenter.currentView, (equalTo(MyConnectionsPresenter.Window.MAIN_VIEW)))
    }

    @Test
    fun `on_recycler_button_pressed accept_connection_request`() {
        val uid = "uid"

        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()

        presenter.onRecyclerViewButtonPressed(MyConnectionsPresenter.Action.ACCEPT_CONNECTION_REQUEST, uid)

        verify(connectionsRepo).onConnectionRequestResponse(uid, true)
    }

    @Test
    fun `on_recycler_button_pressed reject_connection_request`() {
        val uid = "uid"

        presenter.onViewAttached(fragment)
        presenter.onViewWillShow()

        presenter.onRecyclerViewButtonPressed(MyConnectionsPresenter.Action.REJECT_CONNECTION_REQUEST, uid)

        verify(connectionsRepo).onConnectionRequestResponse(uid, false)
    }
}