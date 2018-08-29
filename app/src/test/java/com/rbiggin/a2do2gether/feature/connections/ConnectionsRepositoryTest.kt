package com.rbiggin.a2do2gether.feature.connections

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhaarman.mockito_kotlin.*
import com.rbiggin.a2do2gether.firebase.FirebaseReadEqualWatcher
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabase
import com.rbiggin.a2do2gether.repository.ConnectionsRepository
import com.rbiggin.a2do2gether.repository.UidProvider
import com.rbiggin.a2do2gether.ui.connections.MyConnectionsPresenter
import io.kotlintest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(FirebaseDatabase::class)
class ConnectionsRepositoryTest{

    private lateinit var repository: ConnectionsRepository

    private val presenter: MyConnectionsPresenter = mock()
    private val databaseApi: IntFirebaseDatabase = mock()
    private val uidProvider: UidProvider = mock()

    private val firebaseReader: FirebaseReadWatcher = mock()
    private val firebaseEqualReader: FirebaseReadEqualWatcher = mock()

    private val reference: DatabaseReference = mock()
    private val database: FirebaseDatabase = mock()

    private val uid = "uid"

    @Before
    fun `configure MyConnections Presenter`() {
        PowerMockito.mockStatic(FirebaseDatabase::class.java)
        Mockito.`when`(FirebaseDatabase.getInstance()).thenReturn(database)
        whenever(database.reference).thenReturn(reference)
        whenever(uidProvider.getUid()).doReturn(uid)

        repository = ConnectionsRepository(databaseApi, uidProvider)
        repository.initialise()
    }

    @Test
    fun `onPresenterDetached allWatchersDestroyed`() {
        repository.connectionsMap["one"] = firebaseReader
        repository.connectionsMap["two"] = firebaseReader
        repository.connectionsMap["three"] = firebaseReader

        repository.pendingRequestsMap["one"] = firebaseReader
        repository.pendingRequestsMap["two"] = firebaseReader
        repository.pendingRequestsMap["three"] = firebaseReader

        repository.connectionsWatcher = firebaseReader
        repository.pendingRequestsWatcher = firebaseReader
        repository.searchResultWatcher = firebaseEqualReader

        repository.presenterDetached()

        assert(repository.connectionsMap.isEmpty())
        assert(repository.pendingRequestsMap.isEmpty())
        verify(firebaseReader, times(8)).detachListener()
        verify(firebaseEqualReader).detachListener()

        repository.connectionsWatcher shouldBe null
        repository.pendingRequestsWatcher shouldBe null
        repository.searchResultWatcher shouldBe null
    }
}