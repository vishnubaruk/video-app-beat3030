package com.example.tiktok_analog.ui.legacy.main

// TODO: refactor MainActivity to this state system

//class MainActivity : AppCompatActivity() {
//
//    var uiStateStack: Stack<UIState> = Stack()
//
//    private val stateHeader: Map<UIState, String> = mapOf(
//        UIState.Menu to "Главная",
//        UIState.Filter to "Главная",
//        UIState.AddVideo to "Добавить видео",
//        UIState.StartTranslation to "Поток",
//        UIState.Comments to "Ваши комментарии",
//        UIState.Favorite to "Избранное"
//    )
//
//
//    private fun getCurrentState(): UIState {
//        return uiStateStack.peek()
//    }
//
//    private fun addState(state: UIState): Unit {
//        uiStateStack.push(state)
//        sectionTitleText.text = stateHeader[state]
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        addState(UIState.MainPage)
//
//        openMenuButton.setOnClickListener {
//            openMenu()
//        }
//
//        openFilterButton.setOnClickListener {
//            openFilter()
//        }
//
//        closeMenuButton.setOnClickListener {
//            closeMenu()
//        }
//
//        closeFilterButton.setOnClickListener {
//            closeFilter()
//        }
//
//        applyFilterButton.setOnClickListener {
//            closeFilter()
//        }
//
//        // menu management
//
//        profile.setOnClickListener {
//            closeMenu()
//            closeFilter()
//
//        }
//    }
//
//    private fun openMenu() {
//        // Toast.makeText(applicationContext, "Menu Opened!", Toast.LENGTH_SHORT).show()
//        closeFilter()
//
//        openMenuButton.visibility = View.GONE
//        closeMenuButton.visibility = View.VISIBLE
//
//        menuLayout.visibility = View.VISIBLE
//
//        addState(UIState.Menu)
//    }
//
//    private fun closeMenu() {
//        // Toast.makeText(applicationContext, "Menu Closed!", Toast.LENGTH_SHORT).show()
//
//        openMenuButton.visibility = View.VISIBLE
//        closeMenuButton.visibility = View.GONE
//
//        menuLayout.visibility = View.GONE
//
//        sectionTitleText.text = "Главная"
//    }
//
//    private fun openFilter() {
//        // Toast.makeText(applicationContext, "Filter Opened!", Toast.LENGTH_SHORT).show()
//        closeMenu()
//
//        openFilterButton.visibility = View.GONE
//        closeFilterButton.visibility = View.VISIBLE
//
//        filterLayout.visibility = View.VISIBLE
//
//        addState(UIState.Filter)
//    }
//
//    private fun closeFilter() {
//        // Toast.makeText(applicationContext, "Filter Closed!", Toast.LENGTH_SHORT).show()
//
//        openFilterButton.visibility = View.VISIBLE
//        closeFilterButton.visibility = View.GONE
//
//        filterLayout.visibility = View.GONE
//    }
//
//
//    private fun onEnterState(state: UIState) {
//        when (state) {
//            UIState.Menu -> {
//
//            }
//            UIState.MainPage -> TODO()
//            UIState.Filter -> TODO()
//            UIState.AddVideo -> TODO()
//            UIState.StartTranslation -> TODO()
//            UIState.Comments -> TODO()
//            UIState.Favorite -> TODO()
//            UIState.Settings -> TODO()
//            UIState.Profile -> TODO()
//        }
//    }
//
//    private fun onLeaveState(state: UIState) {
//        when (state) {
//            UIState.Menu -> TODO()
//            UIState.MainPage -> TODO()
//            UIState.Filter -> TODO()
//            UIState.AddVideo -> TODO()
//            UIState.StartTranslation -> TODO()
//            UIState.Comments -> TODO()
//            UIState.Favorite -> TODO()
//            UIState.Settings -> TODO()
//            UIState.Profile -> TODO()
//        }
//    }
//
//    override fun onBackPressed() {
//        // super.onBackPressed()
//
//        when (getCurrentState()) {
//            UIState.Menu -> closeMenu()
//            UIState.MainPage -> super.finish()
//            UIState.Filter -> closeFilter()
//            UIState.AddVideo -> TODO()
//            UIState.StartTranslation -> TODO()
//            UIState.Comments -> TODO()
//            UIState.Favorite -> TODO()
//            UIState.Settings -> TODO()
//            UIState.Profile -> TODO()
//        }
//
//        uiStateStack.pop()
//    }
//}