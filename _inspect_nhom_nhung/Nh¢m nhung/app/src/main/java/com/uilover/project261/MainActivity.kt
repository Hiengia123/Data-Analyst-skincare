package com.uilover.project261

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uilover.project261.Helper.ManagmentCart
import com.uilover.project261.domain.OrderItem
import com.uilover.project261.screens.ItemsList.ItemListScreen
import com.uilover.project261.screens.auth.LoginScreen
import com.uilover.project261.screens.auth.RegisterScreen
import com.uilover.project261.screens.cart.CartScreen
import com.uilover.project261.screens.checkout.CheckoutScreen
import com.uilover.project261.screens.dashboard.MainScreen
import com.uilover.project261.screens.detailProduct.DetailScreen
import com.uilover.project261.screens.favorites.FavoritesScreen
import com.uilover.project261.screens.orders.OrderDetailScreen
import com.uilover.project261.screens.orders.OrderHistoryScreen
import com.uilover.project261.screens.profile.ProfileScreen
import com.uilover.project261.screens.search.SearchScreen
import com.uilover.project261.ui.navigation.CheckoutDataHolder
import com.uilover.project261.ui.navigation.Screen
import com.uilover.project261.viewModel.AuthViewModel
import com.uilover.project261.viewModel.CheckoutViewModel
import com.uilover.project261.viewModel.FavoritesViewModel
import com.uilover.project261.viewModel.MainViewModel
import com.uilover.project261.viewModel.OrderViewModel
import com.uilover.project261.viewModel.RatingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable Firebase RTDB disk persistence BEFORE any database access.
        // This allows repeated reads (e.g. getUserRating) to use the local
        // SQLite cache instead of always going to the server.
        com.uilover.project261.Repository.RatingRepository.enablePersistence()
        enableEdgeToEdge()
        setContent {
            AppNavHost()
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val vm: MainViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val checkoutViewModel: CheckoutViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val ratingViewModel: RatingViewModel = viewModel()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(route = Screen.Home.route) {
            MainScreen(
                viewModel = vm,
                authViewModel = authViewModel,
                onOpenItems = { id, title ->
                    navController.navigate(Screen.Items.path(id, title))

                },
                onOpenDetail = { product ->
                    vm.selectedProduct(product)
                    navController.navigate(Screen.Detail.route)
                },
                onOpenSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onOpenCart = {
                    navController.navigate(Screen.Cart.route)
                },
                onOpenProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onOpenOrders = {
                    navController.navigate(Screen.OrderHistory.route)
                },
                onOpenFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }

        composable(route = Screen.Search.route) {
            SearchScreen(
                viewModel = vm,
                onBackClick = {
                    navController.navigateUp()
                },
                onProductClick = { product ->
                    vm.selectedProduct(product)
                    navController.navigate(Screen.Detail.route)
                }
            )
        }

        composable(route = Screen.Cart.route) {
            CartScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onCheckoutClick = {
                    // Check if user is logged in
                    if (!authViewModel.isLoggedIn()) {
                        Toast.makeText(context, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Login.route)
                    } else {
                        // Get cart items and convert to OrderItems
                        val managementCart = ManagmentCart(context)
                        val cartItems = managementCart.getListCart()
                        val orderItems = cartItems.map { product ->
                            OrderItem(
                                productId = product.id,
                                title = product.title,
                                price = product.price,
                                quantity = product.numberInCart,
                                image = product.image,
                                selectedColor = product.selectedColor,
                                selectedWeight = product.selectedWeight,
                                selectedCapacity = product.selectedCapacity
                            )
                        }
                        CheckoutDataHolder.orderItems = orderItems
                        navController.navigate(Screen.Checkout.route)
                    }
                },
                onProductClick = { product ->
                    vm.selectedProduct(product)
                    navController.navigate(Screen.Detail.route)
                }
            )
        }

        composable(
            route = Screen.Items.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""

            ItemListScreen(
                viewModel = vm,
                id = id,
                title = title,
                onBackClick = { navController.navigateUp() },
                onOpenDetail = { productModel ->
                    vm.selectedProduct(productModel)
                    navController.navigate(Screen.Detail.route)
                }
            )

        }

        composable(Screen.Detail.route) {
            val product by vm.selectedProduct.collectAsState()
            if (product == null) {
                return@composable
            }

            DetailScreen(
                item = product!!,
                onBackClick = { navController.navigateUp() },
                onAddToCartClick = {
                    // Don't navigate - let user continue shopping
                    // Toast is already shown in ManagmentCart.insertItem()
                },
                onBuyNowClick = {
                    // Check if user is logged in
                    if (!authViewModel.isLoggedIn()) {
                        Toast.makeText(context, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Login.route)
                    } else {
                        // Convert current product to OrderItem
                        val currentProduct = product!!
                        val orderItem = OrderItem(
                            productId = currentProduct.id,
                            title = currentProduct.title,
                            price = currentProduct.price,
                            quantity = 1, // Buy now = quantity 1
                            image = currentProduct.image,
                            selectedColor = currentProduct.selectedColor,
                            selectedWeight = currentProduct.selectedWeight,
                            selectedCapacity = currentProduct.selectedCapacity
                        )
                        CheckoutDataHolder.orderItems = listOf(orderItem)
                        navController.navigate(Screen.Checkout.route)
                    }
                },
                viewModel = vm,
                favoritesViewModel = favoritesViewModel,
                authViewModel = authViewModel,
                ratingViewModel = ratingViewModel,
                onOpenDetail = { next ->
                    vm.selectedProduct(next)
                    navController.navigate(Screen.Detail.route) {
                        launchSingleTop = true
                    }
                }
            )

        }

        // Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(route = Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigateUp()
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Profile Screen
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.OrderHistory.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        // Checkout Screen
        composable(route = Screen.Checkout.route) {
            CheckoutScreen(
                checkoutViewModel = checkoutViewModel,
                authViewModel = authViewModel,
                orderItems = CheckoutDataHolder.orderItems,
                onBack = {
                    navController.navigateUp()
                },
                onOrderSuccess = { orderId ->
                    // Clear cart after successful order (if from cart)
                    val managementCart = ManagmentCart(context)
                    managementCart.clearCart()

                    // Clear checkout data holder
                    CheckoutDataHolder.orderItems = emptyList()

                    // Navigate to home with success message
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Order History Screen
        composable(route = Screen.OrderHistory.route) {
            OrderHistoryScreen(
                orderViewModel = orderViewModel,
                authViewModel = authViewModel,
                onBack = {
                    navController.navigateUp()
                },
                onOrderClick = { order ->
                    orderViewModel.selectOrder(order)
                    navController.navigate(Screen.OrderDetail.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        // Order Detail Screen
        composable(route = Screen.OrderDetail.route) {
            val selectedOrder by orderViewModel.selectedOrder.collectAsState()
            selectedOrder?.let { order ->
                OrderDetailScreen(
                    order = order,
                    orderViewModel = orderViewModel,
                    onBack = {
                        navController.navigateUp()
                    },
                    onCancelSuccess = {
                        navController.navigateUp()
                    }
                )
            }
        }

        // Favorites Screen
        composable(route = Screen.Favorites.route) {
            FavoritesScreen(
                favoritesViewModel = favoritesViewModel,
                authViewModel = authViewModel,
                onBack = {
                    navController.navigateUp()
                },
                onProductClick = { product ->
                    vm.selectedProduct(product)
                    navController.navigate(Screen.Detail.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

    }
}