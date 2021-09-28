package gs.location_scanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gs.location_scanner.ui.MainFragment
import gs.location_scanner.ui.ViewDataFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, MainFragment(), MainFragment.TAG)
                .commit()
        }
    }

    fun navigateFromMainToViewData() {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(ViewDataFragment.TAG)
            .replace(R.id.container, ViewDataFragment(), ViewDataFragment.TAG)
            .commit()
    }

    fun navigateFromViewDataToMain() {
        supportFragmentManager
            .beginTransaction()
            .remove(ViewDataFragment())
            .replace(R.id.container, MainFragment(), MainFragment.TAG)
            .commit()
    }
}