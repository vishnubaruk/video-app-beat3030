package com.example.tiktok_analog.util.dataclasses

import kotlinx.serialization.*
import com.example.tiktok_analog.util.enums.SortType

@Serializable
data class AppConfig(val sortType: SortType = SortType.ByPopularity, val adFrequency: Int = 5)