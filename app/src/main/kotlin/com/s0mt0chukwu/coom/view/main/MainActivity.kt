package com.s0mt0chukwu.coom.view.main

import android.appwidget.AppWidgetManager
import android.content.*
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.s0mt0chukwu.coom.R
import com.s0mt0chukwu.coom.base.activity.BaseActivity
import com.s0mt0chukwu.coom.util.CoomManager
import com.s0mt0chukwu.coom.view.dialog.AddCoomDlg
import com.s0mt0chukwu.coom.view.dialog.ImportCoomDlg
import com.s0mt0chukwu.coom.view.main.adapter.MainAdapter
import com.s0mt0chukwu.coom.widget.CoomWidget
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import android.content.ClipData
import android.support.v9.widget.LinearLayoutManager
import android.support.v9.widget.helper.ItemTouchHelper
import com.google.android.gms.ads.MobileAds
import com.s0mt0chukwu.coom.listener.OnCoomML
import com.s0mt0chukwu.coom.util.CoomTchHlprCllbk
import com.s0mt0chukwu.coom.util.LinearSpacItmDecor
import com.s0mt0chukwu.coom.util.StringUtil
import com.s0mt0chukwu.coom.view.main.adapter.item.Coom
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity: BaseActivity(), MainContract.View {

    // firebase
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    private var mDay: String = ""
    private lateinit var mPresenter: MainPresenter
    private lateinit var mAdapter: MainAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var addCoomDialog: AddCoomDialog
    private lateinit var importCoomDialog: ImportCoomDialog
    private lateinit var mCoomTouchHelper: ItemTouchHelper
    private val filter: IntentFilter by lazy {
        IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
        }
    }
    private val mChangeDateBroadcastReceiver: BroadcastReceiver by lazy {
        object: BroadcastReceiver() {
            override fun onReceive(p0: Context, p1: Intent) {
                when(p1.action) {
                    Intent.ACTION_TIME_TICK -> {
                        val day = StringUtil.getCurrentDay()
                        if(mDay != day) {
                            mDay = day
                            mPresenter.refreshAllData()
                        }

                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate")

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // actionbar
        setSupportActionBar(main_toolbar)

        // adapter
        mAdapter = MainAdapter()
        mAdapter.notifyDataSetChanged()

        mRecyclerView = rv_main.apply {
            adapter = mAdapter

            // linear
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(LinearSpacingItemDecoration(context,8, true))

            // grid
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpacingItemDecoration(context,2, 16, false))
            recycledViewPool.setMaxRecycledViews(MainAdapter.Coom_TYPE_ITEM, 0)
        }

        mCoomTouchHelper = ItemTouchHelper(CoomTouchHelperCallback(object: OnCoomMoveListener {
            override fun onItemMove(startPosition: Int, endPosition: Int) {
                mPresenter.moveCoomItem(startPosition, endPosition)
            }
        }))
        mCoomTouchHelper.attachToRecyclerView(mRecyclerView)

        // presenter
        mPresenter = MainPresenter().apply {
            attachView(this@MainActivity)
            setAdapterView(mAdapter)
            setAdapterModel(mAdapter)
            setTouchHelper(mCoomTouchHelper)

            addCoomItems(CoomManager.getCooms(this@MainActivity))
            addPlusView()
        }

        // use ad
           useAd()
    }

    override fun showToast(msg: String) {
        toast(msg)
    }

    override fun showAddCoomDialog() {
        addCoomDialog = AddCoomDialog(this, View.OnClickListener {
            addCoomDialog.addCoom()?.let {
                mPresenter.addCoomItem(it)
                mPresenter.refreshAllData()
            }
        })
        addCoomDialog.show()
    }

    override fun showModifyCoomDialog(position: Int, Coom: Coom) {
        addCoomDialog = AddCoomDialog(this, View.OnClickListener {
            mPresenter.changeItem(position, addCoomDialog.modifyCoom()!!)
            mPresenter.refreshAllData()
        }, Coom.title, Coom)
        addCoomDialog.show()
    }

    override fun scrollToLastItem() {
        mRecyclerView.smoothScrollToPosition(mPresenter.getItemCount())
    }

    override fun getContext(): Context = this

    override fun updateWidget() {
        val ids = AppWidgetManager
                .getInstance(this)
                .getAppWidgetIds(
                        ComponentName(this, CoomWidget::class.java)
                )
        val intent = Intent(this, CoomWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        }
        sendBroadcast(intent)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mChangeDateBroadcastReceiver, filter)
        if(mDay == "") {
            mDay = StringUtil.getCurrentDay()
        }
    }

    override fun onPause() {
        unregisterReceiver(mChangeDateBroadcastReceiver)
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.Coom_export -> {
                val Cooms = mPresenter.getCoomsWithJson()

                // kij
                val clipboardManager = this.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("label", Cooms)
                clipboardManager.primaryClip = clipData

                // kiut
                val CoomShareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, Cooms)
                    type = "text/plain"
                }

                startActivity(CoomShareIntent)
            }
            R.id.Coom_import -> {
                importCoomDialog = ImportCoomDialog(this, View.OnClickListener {
                    val Coom = importCoomDialog.getCooms()
                    if(Coom != null) {
                        for(item in Coom) {
                            mPresenter.addCoomItem(item)
                        }
                    }

                })
                importCoomDialog.show()
            }
            R.id.Coom_remove_all -> {
                getContext().alert(getString(R.string.check_delete)) {
                    yesButton { mPresenter.clearCoomItems() }
                    noButton { }
                }.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun useAd() {
        val ADMOB_APP_ID = this.getString(R.string.admob_app_id)
        val ADMOB_AD_UNIT_ID = this.getString(R.string.admob_unit_id)

        // admob
        MobileAds.initialize(this, ADMOB_APP_ID)

        // native add
        // ad view
           val builder = AdLoader.Builder(this, ADMOB_AD_UNIT_ID)

        mPresenter.useAd(builder)
    }
}