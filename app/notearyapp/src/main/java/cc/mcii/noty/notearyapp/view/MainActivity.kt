package cc.mcii.noty.notearyapp.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import cc.mcii.noty.core.preference.PreferenceManager
import cc.mcii.noty.notearyapp.R
import cc.mcii.noty.notearyapp.databinding.MainActivityBinding
import cc.mcii.noty.utils.ext.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        val binding = MainActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)

        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        observeNavElements(binding, navHostFragment.navController)

        with(navHostFragment.navController) {
            appBarConfiguration = AppBarConfiguration(graph)
            setupActionBarWithNavController(this, appBarConfiguration)
        }

        observeUiModePreferences()
    }

    private fun observeUiModePreferences() {
        preferenceManager.uiModeFlow.asLiveData().observe(this) {
            val uiMode = when (it) {
                true -> AppCompatDelegate.MODE_NIGHT_YES
                false -> AppCompatDelegate.MODE_NIGHT_NO
            }

            AppCompatDelegate.setDefaultNightMode(uiMode)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = item.onNavDestinationSelected(
        findNavController(R.id.nav_host_fragment)
    ) || super.onOptionsItemSelected(item)

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment)
        .navigateUp(appBarConfiguration)

    private fun observeNavElements(
        binding: MainActivityBinding,
        navController: NavController
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                else -> binding.topAppBar.show()
            }
        }
    }
}
