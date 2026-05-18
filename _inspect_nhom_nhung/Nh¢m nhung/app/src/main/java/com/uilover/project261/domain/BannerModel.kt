package com.uilover.project261.domain

import java.io.Serializable

data class BannerModel(
    var id: String = "",  // Banner key (dior, chanel, mac, rare)
    var url: String = ""
) : Serializable

