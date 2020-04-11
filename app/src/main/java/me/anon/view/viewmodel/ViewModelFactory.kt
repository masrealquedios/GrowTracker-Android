package me.anon.view.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import me.anon.view.MainApplication2

/**
 * // TODO: Add class description
 */
class ViewModelFactory(
	private val application: MainApplication2,
	owner: SavedStateRegistryOwner,
	defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs)
{
	override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T = with(modelClass) {
		when {
			isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application.plantsRepository)
			else -> throw IllegalAccessException("Invalid view model")
		} as T
	}
}
