package com.uilover.project261.screens.search

// EXAMPLE: How to integrate SearchScreen into your app

/**
 * HOW TO USE THE SEARCH FUNCTION
 *
 * This file shows examples of how to integrate the search functionality
 * into your existing app navigation and UI.
 */

// ═══════════════════════════════════════════════════════════════
// EXAMPLE 1: Add Search Button to Dashboard/Home Screen
// ═══════════════════════════════════════════════════════════════

/*
@Composable
fun DashboardTopBar(onSearchClick: () -> Unit) {
    TopAppBar(
        title = { Text("Mỹ Phẩm Cao Cấp") },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm sản phẩm",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.primary_pink),
            titleContentColor = Color.White
        )
    )
}
*/

// ═══════════════════════════════════════════════════════════════
// EXAMPLE 2: Add to Navigation Graph
// ═══════════════════════════════════════════════════════════════

/*
@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                viewModel = viewModel,
                onSearchClick = {
                    navController.navigate("search")
                },
                onProductClick = { product ->
                    viewModel.selectedProduct(product)
                    navController.navigate("detail")
                }
            )
        }

        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductClick = { product ->
                    viewModel.selectedProduct(product)
                    navController.navigate("detail")
                }
            )
        }

        composable("detail") {
            val product by viewModel.selectedProduct.collectAsState()
            product?.let {
                DetailScreen(
                    item = it,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}
*/

// ═══════════════════════════════════════════════════════════════
// EXAMPLE 3: Direct Usage in any Composable
// ═══════════════════════════════════════════════════════════════

/*
@Composable
fun MyScreen(viewModel: MainViewModel) {
    var showSearch by remember { mutableStateOf(false) }

    if (showSearch) {
        SearchScreen(
            viewModel = viewModel,
            onBackClick = { showSearch = false },
            onProductClick = { product ->
                // Handle product click
                showSearch = false
            }
        )
    } else {
        // Your normal screen content
        Column {
            Button(onClick = { showSearch = true }) {
                Text("Mở tìm kiếm")
            }
        }
    }
}
*/

// ═══════════════════════════════════════════════════════════════
// EXAMPLE 4: Test Search Function Programmatically
// ═══════════════════════════════════════════════════════════════

/*
// In your ViewModel or composable
fun testSearchFunction(viewModel: MainViewModel) {
    val searchResults by viewModel.searchProducts("dior").observeAsState(emptyList())

    LaunchedEffect(searchResults) {
        println("Search results for 'dior': ${searchResults.size} products")
        searchResults.forEach { product ->
            println("- ${product.title}")
        }
    }
}
*/

// ═══════════════════════════════════════════════════════════════
// EXAMPLE 5: Add Search to Bottom Navigation
// ═══════════════════════════════════════════════════════════════

/*
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, "Trang chủ") },
            label = { Text("Trang chủ") },
            selected = currentRoute == "dashboard",
            onClick = { onNavigate("dashboard") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, "Tìm kiếm") },
            label = { Text("Tìm kiếm") },
            selected = currentRoute == "search",
            onClick = { onNavigate("search") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, "Giỏ hàng") },
            label = { Text("Giỏ hàng") },
            selected = currentRoute == "cart",
            onClick = { onNavigate("cart") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, "Tài khoản") },
            label = { Text("Tài khoản") },
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") }
        )
    }
}
*/

// ═══════════════════════════════════════════════════════════════
// EXAMPLE 6: Search with Custom Suggestions
// ═══════════════════════════════════════════════════════════════

/*
@Composable
fun SearchScreenWithHistory(
    viewModel: MainViewModel,
    tinyDB: TinyDB,
    onBackClick: () -> Unit,
    onProductClick: (ProductModel) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchHistory = remember { tinyDB.getListString("search_history") }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = {
                searchQuery = it
                // Save to history when user searches
                if (it.length > 2) {
                    val history = searchHistory.toMutableList()
                    if (!history.contains(it)) {
                        history.add(0, it)
                        if (history.size > 10) history.removeLast()
                        tinyDB.putListString("search_history", ArrayList(history))
                    }
                }
            },
            onBackClick = onBackClick,
            onClearClick = { searchQuery = "" },
            onSearch = { }
        )

        if (searchQuery.isEmpty()) {
            // Show search history
            LazyColumn {
                items(searchHistory) { query ->
                    Text(
                        text = query,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { searchQuery = query }
                            .padding(16.dp)
                    )
                }
            }
        } else {
            val results by viewModel.searchProducts(searchQuery).observeAsState(emptyList())
            SearchResults(
                products = results,
                onProductClick = onProductClick
            )
        }
    }
}
*/

// ═══════════════════════════════════════════════════════════════
// QUICK TEST SEARCHES FOR YOUR DATABASE
// ═══════════════════════════════════════════════════════════════

/*
Test these searches in your app:

✅ "dior" → Should find: Dior lipstick, Dior cleanser
✅ "son" → Should find: All lipsticks
✅ "999" → Should find: Dior Rouge 999
✅ "chanel" → Should find: Chanel sunscreen, Chanel cleanser
✅ "mac" → Should find: MAC lipstick, MAC primer
✅ "rare" → Should find: All Rare Beauty products
✅ "kem chống nắng" → Should find: All sunscreens
✅ "sữa rửa mặt" → Should find: All cleansers
✅ "lì" → Should find: Matte lipsticks
✅ "selena" → Should find: Rare Beauty (Selena Gomez)
✅ "ruby woo" → Should find: MAC Ruby Woo
✅ "spf 50" → Should find: Products with SPF 50
*/

