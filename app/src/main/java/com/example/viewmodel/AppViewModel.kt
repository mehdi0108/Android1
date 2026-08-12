package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.StoryItem
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.repository.StoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = StoryRepository(db.storyDao())
    private val userPrefsRepository = UserPreferencesRepository(application)

    val userSettings: StateFlow<UserSettings> = userPrefsRepository.settings

    private val _selectedCategory = MutableStateFlow("CATEGORIES_LIST") // "CATEGORIES_LIST", "NASRUDDIN", "SHAHNAMEH", "JOKE", "RIDDLE", "FACT", "FAVORITE"
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentDetailStory = MutableStateFlow<StoryItem?>(null)
    val currentDetailStory: StateFlow<StoryItem?> = _currentDetailStory.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndPrepopulate()
        }
    }

    val allStories: StateFlow<List<StoryItem>> = repository.allStories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val storiesList: StateFlow<List<StoryItem>> =
        _searchQuery.flatMapLatest { query ->
            if (query.isNotBlank()) {
                repository.searchStories(query)
            } else {
                _selectedCategory.flatMapLatest { category ->
                    when (category) {
                        "FAVORITE" -> repository.favoriteStories
                        "NASRUDDIN" -> repository.getStoriesByCategory("NASRUDDIN")
                        "SHAHNAMEH" -> repository.getStoriesByCategory("SHAHNAMEH")
                        "JOKE" -> repository.getStoriesByCategory("JOKE")
                        "RIDDLE" -> repository.getStoriesByCategory("RIDDLE")
                        "FACT" -> repository.getStoriesByCategory("FACT")
                        else -> repository.allStories
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        _searchQuery.value = "" // clear search when tab changes
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadStoryDetail(id: Int) {
        viewModelScope.launch {
            _currentDetailStory.value = repository.getStoryById(id)
        }
    }

    fun toggleFavorite(story: StoryItem) {
        viewModelScope.launch {
            repository.toggleFavorite(story.id, story.isFavorite)
            if (_currentDetailStory.value?.id == story.id) {
                _currentDetailStory.value = _currentDetailStory.value?.copy(isFavorite = !story.isFavorite)
            }
        }
    }

    fun updateFontSize(size: Float) {
        userPrefsRepository.updateFontSize(size)
    }

    fun updateFontFamily(family: String) {
        userPrefsRepository.updateFontFamily(family)
    }

    fun updateFontColor(hex: String) {
        userPrefsRepository.updateFontColor(hex)
    }

    fun updateThemeMode(mode: String) {
        userPrefsRepository.updateThemeMode(mode)
        userPrefsRepository.updateFontColor("") // reset font color override so theme defaults apply
    }

    fun updateGlassMode(enabled: Boolean) {
        userPrefsRepository.updateGlassMode(enabled)
    }

    fun resetSettings() {
        userPrefsRepository.resetToDefaults()
    }

    fun addCoins(amount: Int) {}
    fun spendCoins(amount: Int): Boolean = true
    fun canClaimDailyReward(): Boolean = false
    fun getRemainingClaimTimeMillis(): Long = 0L
    fun claimDailyReward(): Boolean = true
}


